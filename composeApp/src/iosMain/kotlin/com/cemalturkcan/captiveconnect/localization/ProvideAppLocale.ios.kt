package com.cemalturkcan.captiveconnect.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSUserDefaults

private const val APPLE_LANGUAGES_KEY = "AppleLanguages"

@Composable
actual fun ProvideAppLocale(
    languageTag: String?,
    content: @Composable () -> Unit,
) {
    val fallback = requireNotNull(normalizeToBcp47LanguageTag(AppLanguage.English.tag))
    val newLanguageTag = normalizeToBcp47LanguageTag(languageTag) ?: fallback

    remember(newLanguageTag) {
        NSUserDefaults.standardUserDefaults.setObject(
            arrayListOf(newLanguageTag),
            forKey = APPLE_LANGUAGES_KEY,
        )
        newLanguageTag
    }

    content()
}
