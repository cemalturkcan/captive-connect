package com.cemalturkcan.captiveconnect.localization

enum class AppLanguage(
    val tag: String,
    val displayName: String,
) {
    English("en_US", "English"),
    Turkish("tr_TR", "Türkçe"),
    ;

    companion object {
        fun fromTag(tag: String?): AppLanguage? {
            val languageCode = tag
                ?.trim()
                ?.substringBefore('-')
                ?.substringBefore('_')
                ?.lowercase()
                ?.takeIf { it.isNotEmpty() }
                ?: return null

            return when (languageCode) {
                "tr" -> Turkish
                "en" -> English
                else -> null
            }
        }
    }
}
