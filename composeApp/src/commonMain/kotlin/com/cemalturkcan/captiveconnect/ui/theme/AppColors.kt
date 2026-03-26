package com.cemalturkcan.captiveconnect.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

val COLOR_BACKGROUND = Color(0xFF0A0A0A)
val COLOR_SURFACE = Color(0xFF111111)
val COLOR_SURFACE_ELEVATED = Color(0xFF1A1A1A)
val COLOR_BORDER = Color(0x14FFFFFF)
val COLOR_BORDER_MID = Color(0x24FFFFFF)
val COLOR_BORDER_FOCUS = Color(0x47FFFFFF)
val COLOR_TEXT_PRIMARY = Color(0xFFF0F0F0)
val COLOR_TEXT_SECONDARY = Color(0xFF777777)
val COLOR_TEXT_DIM = Color(0xFF3A3A3A)
val COLOR_WHITE = Color(0xFFFFFFFF)
val COLOR_WIFI_CHECKING = Color(0xFF555555)
val COLOR_ERROR_X = Color(0x8CFFFFFF)

val DARK_COLOR_SCHEME = darkColorScheme(
    primary = COLOR_WHITE,
    onPrimary = COLOR_BACKGROUND,
    primaryContainer = COLOR_SURFACE_ELEVATED,
    onPrimaryContainer = COLOR_TEXT_PRIMARY,
    secondary = COLOR_TEXT_SECONDARY,
    onSecondary = COLOR_TEXT_PRIMARY,
    secondaryContainer = COLOR_SURFACE_ELEVATED,
    onSecondaryContainer = COLOR_TEXT_PRIMARY,
    tertiary = COLOR_WHITE,
    onTertiary = COLOR_BACKGROUND,
    background = COLOR_BACKGROUND,
    onBackground = COLOR_TEXT_PRIMARY,
    surface = COLOR_SURFACE,
    onSurface = COLOR_TEXT_PRIMARY,
    surfaceVariant = COLOR_SURFACE_ELEVATED,
    onSurfaceVariant = COLOR_TEXT_SECONDARY,
    outline = COLOR_BORDER,
    outlineVariant = COLOR_SURFACE_ELEVATED,
    error = COLOR_TEXT_DIM,
    onError = COLOR_TEXT_PRIMARY,
    inverseSurface = COLOR_TEXT_PRIMARY,
    inverseOnSurface = COLOR_BACKGROUND,
)
