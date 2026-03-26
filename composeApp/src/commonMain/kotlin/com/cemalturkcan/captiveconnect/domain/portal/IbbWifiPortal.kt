package com.cemalturkcan.captiveconnect.domain.portal

import com.cemalturkcan.captiveconnect.domain.model.Credentials
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

private const val PORTAL_BASE = "http://captive.ibbwifi.istanbul"
private const val CSRF_PATTERN = """name="__RequestVerificationToken"[^>]*value="([^"]+)""""
private const val USER_ID_PATTERN = """data-userid="([^"]+)""""
private const val FLAG_CODE = "tr"
private const val REQUEST_TIMEOUT_MS = 30_000L
private const val CONNECT_TIMEOUT_MS = 15_000L
private const val MIN_USER_ID_LENGTH = 10

private val TRUSTED_HOSTS = setOf("captive.ibbwifi.istanbul", "ibbwifi.istanbul")

class IbbWifiPortal : CaptivePortal {

    override val name: String = "IBB WiFi Istanbul"

    private val json = Json { ignoreUnknownKeys = true }

    override fun canHandle(entryUrl: String): Boolean {
        val host = extractHost(entryUrl) ?: return false
        return TRUSTED_HOSTS.any { host == it || host.endsWith(".$it") }
    }

    override suspend fun login(
        entryUrl: String,
        credentials: Credentials,
    ): LoginResult {
        val client = createClient()
        return try {
            executeLogin(client, entryUrl, credentials)
        } catch (e: Exception) {
            LoginResult.Failure(e.message ?: "unknown error")
        } finally {
            client.close()
        }
    }

    private suspend fun executeLogin(
        client: HttpClient, entryUrl: String, credentials: Credentials,
    ): LoginResult {
        val portalBase = extractPortalBase(entryUrl)
        val loginState = fetchLoginState(client, entryUrl, portalBase, credentials)
            ?: return LoginResult.Failure("login state not resolved")
        return submitLogin(client, portalBase, loginState, credentials)
    }

    private suspend fun fetchLoginState(
        client: HttpClient, entryUrl: String,
        portalBase: String, credentials: Credentials,
    ): LoginState? {
        var cookies = ""

        val portalPage = fetchPortalPage(client, entryUrl, portalBase, cookies)
            ?: return null
        cookies = portalPage.cookies

        val csrfToken = extractCsrf(portalPage.body) ?: return null

        val landingResult = postLandingCheck(
            client, portalBase, csrfToken, cookies, credentials,
        )
        cookies = landingResult.cookies
        val loginUrl = landingResult.loginUrl ?: return null
        if (!isTrustedUrl(loginUrl)) return null

        val loginPage = client.get(loginUrl) {
            header("Cookie", cookies)
            header("Accept", "text/html")
        }
        cookies = mergeCookies(cookies, extractSetCookies(loginPage.headers))
        val loginPageBody = loginPage.bodyAsText()

        val updatedCsrf = extractCsrf(loginPageBody) ?: csrfToken
        val userId = extractUserId(loginPageBody)
            ?: extractUserIdFromUrl(loginUrl)
            ?: return null

        return LoginState(csrf = updatedCsrf, userId = userId, cookies = cookies)
    }

    private suspend fun submitLogin(
        client: HttpClient, portalBase: String,
        loginState: LoginState, credentials: Credentials,
    ): LoginResult {
        val response = client.post("$portalBase/${loginState.userId}/Login") {
            contentType(ContentType.Application.Json)
            header("X-CSRF-TOKEN", loginState.csrf)
            header("Cookie", loginState.cookies)
            header("Referer", "$portalBase/${loginState.userId}/Login")
            header("Origin", portalBase)
            setBody(
                json.encodeToString(
                    LoginBody.serializer(),
                    LoginBody(Password = credentials.password, KVKK = true),
                ),
            )
        }
        return handleLoginResponse(client, response, portalBase)
    }

    private suspend fun fetchPortalPage(
        client: HttpClient, entryUrl: String,
        portalBase: String, initialCookies: String,
    ): PageResult? {
        val response = client.get(entryUrl) {
            header("Accept", "text/html")
            if (initialCookies.isNotEmpty()) header("Cookie", initialCookies)
        }
        var cookies = mergeCookies(initialCookies, extractSetCookies(response.headers))

        if (isRedirect(response.status)) {
            val location = response.headers["Location"] ?: return null
            val resolvedUrl = resolveUrl(location, portalBase)
            if (!isTrustedUrl(resolvedUrl)) return null
            val redirected = client.get(resolvedUrl) {
                header("Accept", "text/html")
                header("Cookie", cookies)
            }
            cookies = mergeCookies(cookies, extractSetCookies(redirected.headers))
            return PageResult(body = redirected.bodyAsText(), cookies = cookies)
        }

        return PageResult(body = response.bodyAsText(), cookies = cookies)
    }

    private suspend fun postLandingCheck(
        client: HttpClient, portalBase: String, csrfToken: String,
        currentCookies: String, credentials: Credentials,
    ): LandingResult {
        val response = client.post("$portalBase/LandingCheck") {
            contentType(ContentType.Application.Json)
            header("X-CSRF-TOKEN", csrfToken)
            header("Cookie", currentCookies)
            header("Referer", "$portalBase/")
            header("Origin", portalBase)
            setBody(
                json.encodeToString(
                    LandingCheckBody.serializer(),
                    LandingCheckBody(
                        PhoneNumber = credentials.phoneNumber,
                        CountryCode = credentials.countryCode,
                        FlagCode = FLAG_CODE,
                    ),
                ),
            )
        }
        val cookies = mergeCookies(currentCookies, extractSetCookies(response.headers))
        return LandingResult(
            loginUrl = resolveLandingUrl(response, portalBase),
            cookies = cookies,
        )
    }

    private suspend fun resolveLandingUrl(
        response: HttpResponse, portalBase: String,
    ): String? = when {
        isRedirect(response.status) -> {
            val location = response.headers["Location"] ?: ""
            if (location.isNotEmpty()) resolveUrl(location, portalBase) else null
        }
        response.status == HttpStatusCode.OK ->
            parseLandingResponse(response.bodyAsText(), portalBase)
        else -> null
    }

    private fun parseLandingResponse(body: String, portalBase: String): String? =
        runCatching {
            val parsed = json.decodeFromString(LandingCheckResponse.serializer(), body)
            if (!parsed.isSuccess) return null
            if (parsed.url.isNotEmpty()) resolveUrl(parsed.url, portalBase) else null
        }.getOrElse {
            val uid = extractUserId(body)
            if (uid != null) "$portalBase/$uid/Login" else null
        }

    private suspend fun handleLoginResponse(
        client: HttpClient, response: HttpResponse, portalBase: String,
    ): LoginResult {
        if (isRedirect(response.status)) {
            val finalUrl = response.headers["Location"] ?: ""
            val resolved = resolveUrl(finalUrl, portalBase)
            if (finalUrl.isNotEmpty() && isTrustedUrl(resolved)) {
                runCatching { client.get(resolved) }
            }
            return LoginResult.Success
        }
        val body = response.bodyAsText()
        return runCatching {
            val parsed = json.decodeFromString(LoginResponse.serializer(), body)
            when {
                !parsed.isSuccess -> LoginResult.Failure("login rejected")
                parsed.url.isNotEmpty() -> {
                    val resolved = resolveUrl(parsed.url, portalBase)
                    if (isTrustedUrl(resolved)) runCatching { client.get(resolved) }
                    LoginResult.Success
                }
                else -> LoginResult.Success
            }
        }.getOrElse { LoginResult.Failure("unexpected response") }
    }

    private fun createClient(): HttpClient = HttpClient {
        followRedirects = false
        install(HttpTimeout) {
            requestTimeoutMillis = REQUEST_TIMEOUT_MS
            connectTimeoutMillis = CONNECT_TIMEOUT_MS
        }
    }

    private fun extractCsrf(html: String): String? =
        Regex(CSRF_PATTERN).find(html)?.groupValues?.getOrNull(1)

    private fun extractUserId(html: String): String? =
        Regex(USER_ID_PATTERN).find(html)?.groupValues?.getOrNull(1)

    private fun extractUserIdFromUrl(url: String): String? =
        extractPath(url).split("/").filter { it.isNotEmpty() }
            .firstOrNull { it.length > MIN_USER_ID_LENGTH && it != "Login" }

    private fun extractSetCookies(headers: io.ktor.http.Headers): String =
        headers.getAll("Set-Cookie")
            ?.mapNotNull { it.split(";").firstOrNull()?.trim() }
            ?.joinToString("; ") ?: ""

    private fun mergeCookies(existing: String, fresh: String): String {
        val cookieMap = linkedMapOf<String, String>()
        parseCookiePairs(existing, cookieMap)
        parseCookiePairs(fresh, cookieMap)
        return cookieMap.entries.joinToString("; ") { "${it.key}=${it.value}" }
    }

    private fun parseCookiePairs(raw: String, into: MutableMap<String, String>) {
        if (raw.isBlank()) return
        raw.split(";").map { it.trim() }.filter { it.contains("=") }.forEach {
            val key = it.substringBefore("=").trim()
            val value = it.substringAfter("=").trim()
            if (key.isNotEmpty()) into[key] = value
        }
    }

    private fun extractPortalBase(entryUrl: String): String =
        Regex("""(https?://[^/?]+)""").find(entryUrl)?.groupValues?.getOrNull(1) ?: PORTAL_BASE

    private fun extractHost(url: String): String? =
        Regex("""https?://([^/?:]+)""").find(url)?.groupValues?.getOrNull(1)

    private fun extractPath(url: String): String =
        Regex("""https?://[^/?]+(/[^?]*)""").find(url)?.groupValues?.getOrNull(1) ?: ""

    private fun isTrustedUrl(url: String): Boolean {
        val host = extractHost(url) ?: return false
        return TRUSTED_HOSTS.any { host == it || host.endsWith(".$it") }
    }

    private fun resolveUrl(url: String, base: String): String =
        if (url.startsWith("http")) url else "$base$url"

    private fun isRedirect(status: HttpStatusCode): Boolean =
        status == HttpStatusCode.MovedPermanently
            || status == HttpStatusCode.Found
            || status == HttpStatusCode.SeeOther
            || status == HttpStatusCode.TemporaryRedirect
            || status == HttpStatusCode.PermanentRedirect
}
