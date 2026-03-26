package com.cemalturkcan.captiveconnect.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

@Composable
actual fun ProvideAppLocale(
    languageTag: String?,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val baseConfiguration = LocalConfiguration.current
    val normalizedLanguageTag = normalizeToBcp47LanguageTag(languageTag)

    val locale = remember(normalizedLanguageTag) {
        if (normalizedLanguageTag == null) {
            Locale.getDefault()
        } else {
            Locale.forLanguageTag(normalizedLanguageTag)
        }
    }

    SideEffect {
        Locale.setDefault(locale)
    }

    val updatedConfiguration = remember(baseConfiguration, locale) {
        createLocalizedConfiguration(baseConfiguration, locale)
    }

    val updatedContext = remember(context, updatedConfiguration) {
        context.createConfigurationContext(updatedConfiguration)
    }

    CompositionLocalProvider(
        LocalConfiguration provides updatedConfiguration,
        LocalContext provides updatedContext,
    ) {
        content()
    }
}
