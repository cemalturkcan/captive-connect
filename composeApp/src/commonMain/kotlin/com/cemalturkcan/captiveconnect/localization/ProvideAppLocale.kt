package com.cemalturkcan.captiveconnect.localization

import androidx.compose.runtime.Composable

@Composable
expect fun ProvideAppLocale(
    languageTag: String?,
    content: @Composable () -> Unit,
)
