package com.cemalturkcan.captiveconnect.domain.portal

import io.ktor.http.HttpStatusCode

internal const val PORTAL_BASE = "http://captive.ibbwifi.istanbul"
internal const val USER_AGENT = "Mozilla/5.0"
internal const val MIN_USER_ID_LENGTH = 10

internal val TRUSTED_HOSTS = setOf("captive.ibbwifi.istanbul", "ibbwifi.istanbul")

private val CSRF_REGEX = Regex("""name="__RequestVerificationToken"[^>]*value="([^"]+)"""")
private val USER_ID_REGEX = Regex("""data-userid="([^"]+)"""")

internal fun extractCsrf(html: String): String? =
    CSRF_REGEX.find(html)?.groupValues?.getOrNull(1)

internal fun extractUserId(html: String): String? =
    USER_ID_REGEX.find(html)?.groupValues?.getOrNull(1)

internal fun extractUserIdFromUrl(url: String): String? =
    extractPath(url).split("/").filter { it.isNotEmpty() }
        .firstOrNull { it.length > MIN_USER_ID_LENGTH && it != "Login" }

internal fun extractSetCookies(headers: io.ktor.http.Headers): String =
    headers.getAll("Set-Cookie")
        ?.mapNotNull { it.split(";").firstOrNull()?.trim() }
        ?.joinToString("; ") ?: ""

internal fun mergeCookies(existing: String, fresh: String): String {
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

internal fun extractPortalBase(entryUrl: String): String =
    Regex("""(https?://[^/?]+)""").find(entryUrl)?.groupValues?.getOrNull(1) ?: PORTAL_BASE

internal fun extractHost(url: String): String? =
    Regex("""https?://([^/?:]+)""").find(url)?.groupValues?.getOrNull(1)

internal fun extractPath(url: String): String =
    Regex("""https?://[^/?]+(/[^?]*)""").find(url)?.groupValues?.getOrNull(1) ?: ""

internal fun isTrustedUrl(url: String): Boolean {
    val host = extractHost(url) ?: return false
    return TRUSTED_HOSTS.any { host == it || host.endsWith(".$it") }
}

internal fun resolveUrl(url: String, base: String): String =
    if (url.startsWith("http")) url else "$base$url"

internal fun isRedirect(status: HttpStatusCode): Boolean =
    status == HttpStatusCode.MovedPermanently
        || status == HttpStatusCode.Found
        || status == HttpStatusCode.SeeOther
        || status == HttpStatusCode.TemporaryRedirect
        || status == HttpStatusCode.PermanentRedirect

internal fun maskPhone(phone: String): String =
    if (phone.length > 4) "${phone.take(3)}***${phone.takeLast(2)}" else "***"
