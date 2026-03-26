package com.cemalturkcan.captiveconnect.localization

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import java.util.Locale

private const val PREFERENCES_NAME = "captive_connect_prefs"
private const val KEY_LANGUAGE_TAG = "language_tag"

fun createAppLocaleContext(baseContext: Context): Context {
    val storedLanguageTag = baseContext
        .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        .getString(KEY_LANGUAGE_TAG, null)
    val normalizedLanguageTag = normalizeToBcp47LanguageTag(storedLanguageTag)
        ?: return baseContext
    val locale = Locale.forLanguageTag(normalizedLanguageTag)
    val updatedConfiguration = createLocalizedConfiguration(
        baseContext.resources.configuration,
        locale,
    )

    Locale.setDefault(locale)
    return baseContext.createConfigurationContext(updatedConfiguration)
}

internal fun createLocalizedConfiguration(
    baseConfiguration: Configuration,
    locale: Locale,
): Configuration {
    return Configuration(baseConfiguration).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setLocales(LocaleList(locale))
        } else {
            @Suppress("DEPRECATION")
            setLocale(locale)
        }
        setLayoutDirection(locale)
    }
}
