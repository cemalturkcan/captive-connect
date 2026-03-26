package com.cemalturkcan.captiveconnect.localization

fun normalizeToBcp47LanguageTag(languageTag: String?): String? {
    val raw = languageTag?.trim()?.takeIf { it.isNotEmpty() } ?: return null
    val normalized = raw.replace('_', '-')
    val parts = normalized.split('-').filter { it.isNotEmpty() }
    if (parts.isEmpty()) return null

    val language = parts[0].lowercase()
    val region = parts.getOrNull(1)?.uppercase()?.takeIf { it.isNotEmpty() }

    return if (region == null) {
        language
    } else {
        "$language-$region"
    }
}

fun normalizeToStorageLanguageTag(languageTag: String?): String? {
    val bcp47 = normalizeToBcp47LanguageTag(languageTag) ?: return null
    val parts = bcp47.split('-')
    val language = parts[0]
    val region = parts.getOrNull(1)

    return if (region == null) {
        language
    } else {
        "${language}_${region}"
    }
}
