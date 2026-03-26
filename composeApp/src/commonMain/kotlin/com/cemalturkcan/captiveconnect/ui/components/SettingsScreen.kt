package com.cemalturkcan.captiveconnect.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import captiveconnect.composeapp.generated.resources.Res
import captiveconnect.composeapp.generated.resources.language
import captiveconnect.composeapp.generated.resources.language_description
import captiveconnect.composeapp.generated.resources.settings
import captiveconnect.composeapp.generated.resources.version_label
import com.cemalturkcan.captiveconnect.localization.AppLanguage
import com.cemalturkcan.captiveconnect.ui.primitives.BackIconButton
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_BACKGROUND
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_BORDER
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_SURFACE
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_TEXT_DIM
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_TEXT_PRIMARY
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_TEXT_SECONDARY
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_WHITE
import com.cemalturkcan.captiveconnect.ui.tokens.BORDER_WIDTH_1
import com.cemalturkcan.captiveconnect.ui.tokens.RADIUS_CARD
import com.cemalturkcan.captiveconnect.ui.tokens.SPACING_10
import com.cemalturkcan.captiveconnect.ui.tokens.SPACING_3
import com.cemalturkcan.captiveconnect.ui.tokens.SPACING_4
import com.cemalturkcan.captiveconnect.ui.tokens.SPACING_5
import com.cemalturkcan.captiveconnect.ui.tokens.SPACING_6
import com.cemalturkcan.captiveconnect.ui.tokens.SPACING_8
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreen(
    selectedLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onBack: () -> Unit,
    versionName: String,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(COLOR_BACKGROUND)
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        SettingsTopBar(onBack = onBack)
        Spacer(modifier = Modifier.height(SPACING_3))
        SettingsContent(
            selectedLanguage = selectedLanguage,
            onLanguageSelected = onLanguageSelected,
            versionName = versionName,
        )
    }
}

@Composable
private fun SettingsTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SPACING_10, vertical = SPACING_8),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BackIconButton(onClick = onBack)
        Spacer(modifier = Modifier.width(SPACING_5))
        Text(
            text = stringResource(Res.string.settings).uppercase(),
            style = MaterialTheme.typography.displaySmall,
            color = COLOR_WHITE,
        )
    }
}

@Composable
private fun SettingsContent(
    selectedLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    versionName: String,
) {
    Column(
        modifier = Modifier.padding(horizontal = SPACING_10),
        verticalArrangement = Arrangement.spacedBy(SPACING_3),
    ) {
        SettingsRow {
            Column {
                Text(
                    text = stringResource(Res.string.language),
                    style = MaterialTheme.typography.titleMedium,
                    color = COLOR_TEXT_PRIMARY,
                )
                Text(
                    text = stringResource(Res.string.language_description),
                    style = MaterialTheme.typography.labelMedium,
                    color = COLOR_TEXT_DIM,
                )
            }
            LanguagePicker(
                selectedLanguage = selectedLanguage,
                onLanguageSelected = onLanguageSelected,
            )
        }

        SettingsRow {
            Text(
                text = stringResource(Res.string.version_label),
                style = MaterialTheme.typography.titleMedium,
                color = COLOR_TEXT_PRIMARY,
            )
            Text(
                text = versionName,
                style = MaterialTheme.typography.labelSmall,
                color = COLOR_TEXT_SECONDARY,
            )
        }
    }
}

@Composable
private fun SettingsRow(
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(RADIUS_CARD),
        color = COLOR_SURFACE,
        border = BorderStroke(BORDER_WIDTH_1, COLOR_BORDER),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SPACING_6, vertical = SPACING_4),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            content()
        }
    }
}
