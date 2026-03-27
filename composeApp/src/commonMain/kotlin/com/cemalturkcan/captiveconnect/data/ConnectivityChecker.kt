package com.cemalturkcan.captiveconnect.data

import com.cemalturkcan.captiveconnect.domain.portal.DetectionResult
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode

private val CONNECTIVITY_CHECK_URLS = listOf(
    "http://connectivitycheck.gstatic.com/generate_204",
    "http://1.1.1.1/generate_204",
)
private const val REQUEST_TIMEOUT_MS = 10_000L
private const val CONNECT_TIMEOUT_MS = 5_000L
private const val USER_AGENT = "Mozilla/5.0"

private val TRUSTED_PORTAL_HOSTS = setOf(
    "ibbwifi.istanbul",
    "captive.ibbwifi.istanbul",
)

class ConnectivityChecker {

    suspend fun check(): DetectionResult {
        val client = createClient()
        return try {
            checkWithUrls(client)
        } finally {
            client.close()
        }
    }

    private suspend fun checkWithUrls(client: HttpClient): DetectionResult {
        for (url in CONNECTIVITY_CHECK_URLS) {
            val result = trySingleCheck(client, url)
            if (result !is DetectionResult.Unknown) return result
        }
        return DetectionResult.Unknown("all connectivity checks failed")
    }

    private suspend fun trySingleCheck(
        client: HttpClient, url: String,
    ): DetectionResult = try {
        val response: HttpResponse = client.get(url)
        when (response.status) {
            HttpStatusCode.NoContent -> DetectionResult.Online
            HttpStatusCode.Found, HttpStatusCode.MovedPermanently,
            HttpStatusCode.TemporaryRedirect, HttpStatusCode.PermanentRedirect,
            -> parseRedirect(response.headers["Location"].orEmpty())
            else -> DetectionResult.Unknown("status: ${response.status.value}")
        }
    } catch (_: Exception) {
        DetectionResult.Unknown("failed: $url")
    }

    private fun createClient(): HttpClient = HttpClient {
        install(HttpRedirect) { checkHttpMethod = false }
        install(HttpTimeout) {
            requestTimeoutMillis = REQUEST_TIMEOUT_MS
            connectTimeoutMillis = CONNECT_TIMEOUT_MS
        }
        followRedirects = false
        defaultRequest {
            header("User-Agent", USER_AGENT)
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
