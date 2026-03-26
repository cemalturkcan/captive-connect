package com.cemalturkcan.captiveconnect.data

import com.cemalturkcan.captiveconnect.domain.portal.DetectionResult
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode

private const val CONNECTIVITY_CHECK_URL =
    "http://connectivitycheck.gstatic.com/generate_204"
private const val REQUEST_TIMEOUT_MS = 15_000L
private const val CONNECT_TIMEOUT_MS = 10_000L

private val TRUSTED_PORTAL_HOSTS = setOf(
    "ibbwifi.istanbul",
    "captive.ibbwifi.istanbul"
)

class ConnectivityChecker {

    suspend fun check(): DetectionResult {
        val client = HttpClient {
            install(HttpRedirect) { checkHttpMethod = false }
            install(HttpTimeout) {
                requestTimeoutMillis = REQUEST_TIMEOUT_MS
                connectTimeoutMillis = CONNECT_TIMEOUT_MS
            }
            followRedirects = false
        }
        return try {
            val response: HttpResponse = client.get(CONNECTIVITY_CHECK_URL)
            when (response.status) {
                HttpStatusCode.NoContent -> DetectionResult.Online
                HttpStatusCode.Found, HttpStatusCode.MovedPermanently,
                HttpStatusCode.TemporaryRedirect, HttpStatusCode.PermanentRedirect,
                -> parseRedirect(response.headers["Location"].orEmpty())
                else -> DetectionResult.Unknown("status: ${response.status.value}")
            }
        } catch (e: Exception) {
            DetectionResult.Unknown(e.message ?: "connectivity check failed")
        } finally {
            client.close()
        }
    }

    private fun parseRedirect(location: String): DetectionResult {
        val host = extractHost(location)
        val isTrusted = TRUSTED_PORTAL_HOSTS.any { trusted ->
            host == trusted || host.endsWith(".$trusted")
        }
        return if (isTrusted) DetectionResult.PortalFound(location)
        else DetectionResult.Unknown("redirect to: $location")
    }

    private fun extractHost(url: String): String =
        url.substringAfter("://").substringBefore("/").substringBefore(":").lowercase()
}
