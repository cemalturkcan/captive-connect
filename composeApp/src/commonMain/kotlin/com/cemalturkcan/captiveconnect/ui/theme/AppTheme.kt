package com.cemalturkcan.captiveconnect.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DARK_COLOR_SCHEME,
        typography = appTypography(),
        shapes = APP_SHAPES,
        content = content,
    )
}
