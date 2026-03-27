package com.cemalturkcan.captiveconnect.domain.portal

import com.cemalturkcan.captiveconnect.domain.model.Credentials
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.defaultRequest
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

private const val FLAG_CODE = "tr"
private const val REQUEST_TIMEOUT_MS = 30_000L
private const val CONNECT_TIMEOUT_MS = 15_000L
private const val MAX_REDIRECTS = 5

class IbbWifiPortal : CaptivePortal {

    override val id: String = "ibb_wifi"
    override val name: String = "ibbWiFi"
    override val defaultEntryUrl: String = PORTAL_BASE

    private val json = Json { ignoreUnknownKeys = true }
    private var debugLog = StringBuilder()

    private fun log(msg: String) {
        debugLog.appendLine(msg)
    }

    override fun canHandle(entryUrl: String): Boolean {
        val host = extractHost(entryUrl) ?: return false
        return TRUSTED_HOSTS.any { host == it || host.endsWith(".$it") }
    }

    override suspend fun login(
        entryUrl: String,
        credentials: Credentials,
    ): LoginResult {
        debugLog = StringBuilder()
        log("=== IBB WiFi Login ===")
        log("Entry: $entryUrl")
        log("Phone: ${maskPhone(credentials.phoneNumber)} | CC: ${credentials.countryCode}")
        val client = createClient()
        return try {
            executeLogin(client, entryUrl, credentials)
        } catch (e: Exception) {
            log("EXCEPTION: ${e.message}")
            LoginResult.Failure(e.message ?: "unknown error", debugLog = debugLog.toString())
        } finally {
            client.close()
        }
    }

    private suspend fun executeLogin(
        client: HttpClient, entryUrl: String, credentials: Credentials,
    ): LoginResult {
        val portalBase = extractPortalBase(entryUrl)
        log("Base: $portalBase")
        val loginState = fetchLoginState(client, entryUrl, portalBase, credentials)
        if (loginState == null) {
            log("FAILED: login state not resolved")
            return LoginResult.Failure("login state not resolved", debugLog = debugLog.toString())
        }
        return submitLogin(client, portalBase, loginState, credentials)
    }

    private suspend fun fetchLoginState(
        client: HttpClient, entryUrl: String,
        portalBase: String, credentials: Credentials,
    ): LoginState? {
        var cookies = ""
        val portalPage = fetchPortalPage(client, entryUrl, portalBase, cookies)
            ?: return null.also { log("FAILED: portal page not loaded") }
        cookies = portalPage.cookies

        val csrfToken = extractCsrf(portalPage.body)
            ?: return null.also { log("FAILED: CSRF not found") }
        log("CSRF: ${csrfToken.take(20)}...")

        val landingResult = postLandingCheck(client, portalBase, csrfToken, cookies, credentials)
        cookies = landingResult.cookies
        val loginUrl = landingResult.loginUrl
            ?: return null.also { log("FAILED: login URL not resolved") }
        if (!isTrustedUrl(loginUrl)) return null.also { log("FAILED: untrusted URL $loginUrl") }
        log("Login URL: $loginUrl")

        val loginPage = client.get(loginUrl) {
            header("Cookie", cookies)
            header("Accept", "text/html")
        }
        log("Login page: ${loginPage.status}")
        cookies = mergeCookies(cookies, extractSetCookies(loginPage.headers))
        val loginPageBody = loginPage.bodyAsText()

        val updatedCsrf = extractCsrf(loginPageBody) ?: csrfToken
        val userId = extractUserId(loginPageBody) ?: extractUserIdFromUrl(loginUrl)
            ?: return null.also { log("FAILED: user ID not found") }
        log("User ID: $userId")
        return LoginState(csrf = updatedCsrf, userId = userId, cookies = cookies)
    }

    private suspend fun submitLogin(
        client: HttpClient, portalBase: String,
        loginState: LoginState, credentials: Credentials,
    ): LoginResult {
        val loginPath = "$portalBase/${loginState.userId}/Login"
        log("Login POST: $loginPath")
        val response = client.post(loginPath) {
            contentType(ContentType.Application.Json)
            header("X-CSRF-TOKEN", loginState.csrf)
            header("Cookie", loginState.cookies)
            header("Referer", loginPath)
            header("Origin", portalBase)
            setBody(
                json.encodeToString(
                    LoginBody.serializer(),
                    LoginBody(Password = credentials.password, KVKK = true),
                ),
            )
        }
        log("Login response: ${response.status}")
        return handleLoginResponse(client, response, portalBase)
    }

    private suspend fun fetchPortalPage(
        client: HttpClient, entryUrl: String,
        portalBase: String, initialCookies: String,
    ): PageResult? {
        var url = entryUrl
        var cookies = initialCookies
        repeat(MAX_REDIRECTS) {
            val response = client.get(url) {
                header("Accept", "text/html")
                if (cookies.isNotEmpty()) header("Cookie", cookies)
            }
            log("Fetch $url → ${response.status}")
            cookies = mergeCookies(cookies, extractSetCookies(response.headers))
            if (!isRedirect(response.status)) {
                return PageResult(body = response.bodyAsText(), cookies = cookies)
            }
            val location = response.headers["Location"]
            if (location.isNullOrEmpty()) {
                log("Redirect without Location header")
                return null
            }
            url = resolveUrl(location, portalBase)
            if (!isTrustedUrl(url)) {
                log("Untrusted redirect: $url")
                return null
            }
        }
        log("Too many redirects ($MAX_REDIRECTS)")
        return null
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
        log("LandingCheck: ${response.status}")
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

    private fun parseLandingResponse(body: String, portalBase: String): String? {
        log("Landing body: ${body.take(200)}")
        return runCatching {
            val parsed = json.decodeFromString(LandingCheckResponse.serializer(), body)
            if (!parsed.isSuccess) return null
            if (parsed.url.isNotEmpty()) resolveUrl(parsed.url, portalBase) else null
        }.getOrElse {
            val uid = extractUserId(body)
            if (uid != null) "$portalBase/$uid/Login" else null
        }
    }

    private suspend fun handleLoginResponse(
        client: HttpClient, response: HttpResponse, portalBase: String,
    ): LoginResult {
        if (isRedirect(response.status)) {
            val finalUrl = response.headers["Location"] ?: ""
            val resolved = resolveUrl(finalUrl, portalBase)
            log("Login redirect: $resolved")
            if (finalUrl.isNotEmpty() && isTrustedUrl(resolved)) {
                runCatching { client.get(resolved) }
            }
            return LoginResult.Success(debugLog = debugLog.toString())
        }
        val body = response.bodyAsText()
        log("Login body: ${body.take(200)}")
        return runCatching {
            val parsed = json.decodeFromString(LoginResponse.serializer(), body)
            when {
                !parsed.isSuccess -> LoginResult.Failure("login rejected", debugLog = debugLog.toString())
                parsed.url.isNotEmpty() -> {
                    val resolved = resolveUrl(parsed.url, portalBase)
                    if (isTrustedUrl(resolved)) runCatching { client.get(resolved) }
                    LoginResult.Success(debugLog = debugLog.toString())
                }
                else -> LoginResult.Success(debugLog = debugLog.toString())
            }
        }.getOrElse { LoginResult.Failure("unexpected response", debugLog = debugLog.toString()) }
    }

    private fun createClient(): HttpClient = HttpClient {
        followRedirects = false
        install(HttpTimeout) {
            requestTimeoutMillis = REQUEST_TIMEOUT_MS
            connectTimeoutMillis = CONNECT_TIMEOUT_MS
        }
        defaultRequest {
            header("User-Agent", USER_AGENT)
        }
    }
}
