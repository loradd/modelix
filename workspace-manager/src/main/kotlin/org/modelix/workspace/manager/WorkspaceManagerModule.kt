/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.modelix.workspace.manager

import com.charleskorn.kaml.Yaml
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.apache.commons.text.StringEscapeUtils
import java.awt.SystemColor.text

fun Application.workspaceManagerModule() {

    val manager = WorkspaceManager()

    install(Routing)

    routing {
        defaultResource("html/index.html")

        post("new") {
            val workspace = manager.newWorkspace()
            call.respondRedirect("edit/${workspace.id}")
        }

        post("update") {
            val yamlText = call.receiveParameters()["content"]
            if (yamlText == null) {
                call.respond(HttpStatusCode.BadRequest, "Content missing")
                return@post
            }
            val workspace: Workspace
            try {
                workspace = Yaml.default.decodeFromString<Workspace>(yamlText)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Parse error")
                return@post
            }
            manager.update(workspace)
            call.respondRedirect("edit/${workspace.id}")
        }

        get("/health") {
            call.respondText("healthy", ContentType.Text.Plain, HttpStatusCode.OK)
        }

        get("edit/{workspaceId}") {
            val id = call.parameters["workspaceId"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Workspace ID is missing")
                return@get
            }
            val workspace = manager.getWorkspace(id)
            if (workspace == null) {
                call.respond(HttpStatusCode.NotFound, "Workspace $id not found")
                return@get
            }
            val yaml = Yaml.default.encodeToString(workspace)
            val htmlTemplate = Application::class.java.classLoader.getResource("html/edit.html")?.readText()
            if (htmlTemplate == null) {
                call.respond(HttpStatusCode.InternalServerError, "HTML template not found")
                return@get
            }
            val html = htmlTemplate
                .replace("{{content}}", StringEscapeUtils.escapeHtml4(yaml))
                .replace("{{workspaceId}}", id)
            this.call.respondText(html, ContentType.Text.Html, HttpStatusCode.OK)
        }

        get("download-modules/{workspaceId}/queue") {
            val workspaceId = call.parameters["workspaceId"]!!
            val job = manager.buildWorkspaceDownloadFileAsync(workspaceId)
            val respondStatus: suspend (String, String)->Unit = { text, refresh ->
                val html = """
                    <html>
                    <head>
                        <meta http-equiv="refresh" content="$refresh">
                    <head>
                    <body>
                        $text
                        <br/>
                        <br/>
                        <pre>${StringEscapeUtils.escapeHtml4(job.output.joinToString("\n"))}</pre>
                    </body>
                    </html>
                """.trimIndent()
                call.respondText(html, ContentType.Text.Html, HttpStatusCode.OK)
            }
            when (job.status) {
                Status.New, Status.Queued -> respondStatus("Workspace is queued for building ...", "3")
                Status.Running -> respondStatus("Downloading and building modules ...", "3")
                Status.Failed -> respondStatus("Failed to build the workspace ...", "3")
                Status.Successful -> {
                    val fileName = "workspace-$workspaceId.zip"
                    respondStatus("""Downloading <a href="$fileName">$fileName</a>""", "0; url=$fileName")
                }
            }
        }

        get("download-modules/{workspaceId}/status") {
            val workspaceId = call.parameters["workspaceId"]!!
            val job = manager.buildWorkspaceDownloadFileAsync(workspaceId)
            call.respondText(job.status.toString(), ContentType.Text.Plain, HttpStatusCode.OK)
        }

        get("download-modules/{workspaceId}/workspace-{workspaceId}.zip") {
            val id = call.parameters["workspaceId"]!!
            val workspace = manager.getWorkspace(id)
            if (workspace == null) {
                call.respondText("Workspace $id not found", ContentType.Text.Plain, HttpStatusCode.NotFound)
            } else {
                val file = manager.getDownloadFile(workspace)
                if (file.exists()) {
                    call.respondFile(file)
                } else {
                    call.respondText("""File doesn't exist yet. <a href="queue">Start a build job for the workspace.</a>""", ContentType.Text.Html, HttpStatusCode.NotFound)
                }
            }
        }

        static {
            resources("html")
        }
    }

    install(CORS) {
        anyHost()
        header(HttpHeaders.ContentType)
        method(HttpMethod.Options)
        method(HttpMethod.Get)
        method(HttpMethod.Put)
        method(HttpMethod.Post)
    }
}
