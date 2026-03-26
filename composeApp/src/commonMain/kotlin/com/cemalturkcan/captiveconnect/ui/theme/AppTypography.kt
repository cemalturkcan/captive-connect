package com.cemalturkcan.captiveconnect.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.cemalturkcan.captiveconnect.ui.tokens.FONT_SIZE_BODY
import com.cemalturkcan.captiveconnect.ui.tokens.FONT_SIZE_BUTTON
import com.cemalturkcan.captiveconnect.ui.tokens.FONT_SIZE_LABEL
import com.cemalturkcan.captiveconnect.ui.tokens.FONT_SIZE_SETTINGS_TITLE
import com.cemalturkcan.captiveconnect.ui.tokens.FONT_SIZE_STATUS
import com.cemalturkcan.captiveconnect.ui.tokens.LETTER_SPACING_BUTTON
import com.cemalturkcan.captiveconnect.ui.tokens.LETTER_SPACING_INPUT
import com.cemalturkcan.captiveconnect.ui.tokens.LETTER_SPACING_LABEL
import com.cemalturkcan.captiveconnect.ui.tokens.LETTER_SPACING_SETTINGS_TITLE
import com.cemalturkcan.captiveconnect.ui.tokens.LETTER_SPACING_STATUS
import com.cemalturkcan.captiveconnect.ui.tokens.LINE_HEIGHT_BODY
import com.cemalturkcan.captiveconnect.ui.tokens.LINE_HEIGHT_BUTTON
import com.cemalturkcan.captiveconnect.ui.tokens.LINE_HEIGHT_LABEL
import com.cemalturkcan.captiveconnect.ui.tokens.LINE_HEIGHT_SETTINGS_TITLE
import com.cemalturkcan.captiveconnect.ui.tokens.LINE_HEIGHT_STATUS

@Composable
fun appTypography(): Typography {
    val mono = dmMonoFamily()
    val display = bebasNeueFamily()
    val sans = dmSansFamily()

    return Typography(
        displaySmall = TextStyle(
            fontFamily = display,
            fontSize = FONT_SIZE_SETTINGS_TITLE,
            lineHeight = LINE_HEIGHT_SETTINGS_TITLE,
            fontWeight = FontWeight.Normal,
            letterSpacing = LETTER_SPACING_SETTINGS_TITLE,
        ),
        headlineSmall = TextStyle(
            fontFamily = display,
            fontSize = FONT_SIZE_BUTTON,
            lineHeight = LINE_HEIGHT_BUTTON,
            fontWeight = FontWeight.Normal,
            letterSpacing = LETTER_SPACING_BUTTON,
        ),
        titleMedium = TextStyle(
            fontFamily = sans,
            fontSize = FONT_SIZE_BODY,
            lineHeight = LINE_HEIGHT_BODY,
            fontWeight = FontWeight.Medium,
        ),
        bodyLarge = TextStyle(
            fontFamily = sans,
            fontSize = FONT_SIZE_BODY,
            lineHeight = LINE_HEIGHT_BODY,
            fontWeight = FontWeight.Normal,
        ),
        bodyMedium = TextStyle(
            fontFamily = mono,
            fontSize = FONT_SIZE_BODY,
            lineHeight = LINE_HEIGHT_BODY,
            fontWeight = FontWeight.Normal,
            letterSpacing = LETTER_SPACING_INPUT,
        ),
        bodySmall = TextStyle(
            fontFamily = mono,
            fontSize = FONT_SIZE_STATUS,
            lineHeight = LINE_HEIGHT_STATUS,
            fontWeight = FontWeight.Normal,
            letterSpacing = LETTER_SPACING_STATUS,
        ),
        labelLarge = TextStyle(
            fontFamily = display,
            fontSize = FONT_SIZE_BUTTON,
            lineHeight = LINE_HEIGHT_BUTTON,
            fontWeight = FontWeight.Normal,
            letterSpacing = LETTER_SPACING_BUTTON,
        ),
        labelMedium = TextStyle(
            fontFamily = mono,
            fontSize = FONT_SIZE_LABEL,
            lineHeight = LINE_HEIGHT_LABEL,
            fontWeight = FontWeight.Normal,
            letterSpacing = LETTER_SPACING_LABEL,
        ),
        labelSmall = TextStyle(
            fontFamily = mono,
            fontSize = FONT_SIZE_STATUS,
            lineHeight = LINE_HEIGHT_STATUS,
            fontWeight = FontWeight.Normal,
            letterSpacing = LETTER_SPACING_STATUS,
        ),
    )
}
