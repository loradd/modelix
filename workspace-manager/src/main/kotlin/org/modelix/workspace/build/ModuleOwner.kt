/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.modelix.workspace.build

import kotlin.io.path.pathString

abstract class ModuleOwner(val path: ModulePath) {
    val modules: MutableMap<ModuleId, FoundModule> = HashMap()

    fun getWorkspaceRelativePath(): String {
        return "\$MODELIX_WORKSPACE\$/" + path.getWorkspaceRelativePath().pathString
    }
}