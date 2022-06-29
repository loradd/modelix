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
package org.modelix.authorization

import com.auth0.jwt.interfaces.DecodedJWT
import com.google.common.cache.CacheBuilder
import org.keycloak.authorization.client.AuthzClient
import org.keycloak.authorization.client.Configuration
import org.keycloak.representations.idm.authorization.AuthorizationRequest
import org.keycloak.representations.idm.authorization.Permission
import org.keycloak.representations.idm.authorization.PermissionRequest
import org.keycloak.representations.idm.authorization.ResourceRepresentation
import org.modelix.authorization.ktor.getClientSecret
import java.util.concurrent.TimeUnit

object KeycloakUtils {
    val authzClient = AuthzClient.create(Configuration(
        "http://172.16.2.56:31310/",
        "modelix",
        "modelix",
        mapOf("secret" to getClientSecret()),
        null
    ))

    private val cache = CacheBuilder.newBuilder()
        .expireAfterWrite(1, TimeUnit.MINUTES)
        .build<String, List<Permission>>()
    private val existingResources = HashSet<String>()

    @Synchronized
    fun getPermissions(accessToken: DecodedJWT): List<Permission> {
        return cache.get(accessToken.token) { queryPermissions(accessToken) }
    }

    private fun queryPermissions(accessToken: DecodedJWT): List<Permission> {
        try {
            val rpt = authzClient.authorization(accessToken.token).authorize(AuthorizationRequest()).token
            val introspect = authzClient.protection().introspectRequestingPartyToken(rpt)
            return introspect.permissions
        } catch (e: Exception) {
            throw RuntimeException("Can't get permissions for token: ${accessToken.token}", e)
        }
    }

    @Synchronized
    fun hasPermission(accessToken: DecodedJWT, resourceName: String, scope: String): Boolean {
        ensureResourcesExists(resourceName)
        val allPermissions = getPermissions(accessToken)
        val forResource = allPermissions.filter { it.resourceName == resourceName }
        if (forResource.isEmpty()) return false
        val scopes: Set<String> = forResource.mapNotNull { it.scopes }.flatten().toSet()
        if (scopes.isEmpty()) {
            // If the permissions are not restricted to any scope we assume they are valid for all scopes.
            return true
        }
        return scopes.contains(scope)
    }

    private fun hasPermissions(accessToken: DecodedJWT, resourceNames: List<String>): Boolean {
        try {
            val ticket = authzClient.protection(accessToken.token).permission().create(resourceNames.map { PermissionRequest(it) }).ticket
            val rpt = authzClient.authorization(accessToken.token).authorize(AuthorizationRequest(ticket)).token
            val introspect = authzClient.protection().introspectRequestingPartyToken(rpt)
            return introspect.active
        } catch (e: Exception) {
            throw RuntimeException("Can't get permissions for token: ${accessToken.token}", e)
        }
    }

    private fun createPermission(ownerToken: DecodedJWT?, resourceName: String): String {
        val protection = if (ownerToken == null) authzClient.protection() else authzClient.protection(ownerToken.token)
        val newResource = protection.resource().create(ResourceRepresentation(resourceName))
        return newResource.id
    }

    @Synchronized
    fun ensureResourcesExists(resourceName: String) {
        if (existingResources.contains(resourceName)) return
        if (authzClient.protection().resource().findByName(resourceName) == null) {
            authzClient.protection().resource().create(ResourceRepresentation(resourceName))
            cache.invalidateAll()
        }
        existingResources += resourceName
    }
}