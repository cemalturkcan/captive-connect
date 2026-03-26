package com.cemalturkcan.captiveconnect.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key

@Composable
fun AppEnvironment(
    languageTag: String?,
    content: @Composable () -> Unit,
) {
    ProvideAppLocale(languageTag = languageTag) {
        key(languageTag) {
            content()
        }
    }
}
