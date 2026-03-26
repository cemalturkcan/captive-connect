package com.cemalturkcan.captiveconnect.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.cemalturkcan.captiveconnect.domain.model.PortalInfo
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_BACKGROUND
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_BORDER
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_SURFACE_ELEVATED
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_TEXT_DIM
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_WHITE
import com.cemalturkcan.captiveconnect.ui.tokens.BORDER_WIDTH_1
import com.cemalturkcan.captiveconnect.ui.tokens.LETTER_SPACING_TOGGLE
import com.cemalturkcan.captiveconnect.ui.tokens.RADIUS_TOGGLE
import com.cemalturkcan.captiveconnect.ui.tokens.RADIUS_TOGGLE_CONTAINER
import com.cemalturkcan.captiveconnect.ui.tokens.SPACING_1
import com.cemalturkcan.captiveconnect.ui.tokens.SPACING_2
import com.cemalturkcan.captiveconnect.ui.tokens.SPACING_5

@Composable
fun PortalPicker(
    portals: List<PortalInfo>,
    selectedPortalId: String,
    onPortalSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerShape = RoundedCornerShape(RADIUS_TOGGLE_CONTAINER)
    val pillShape = RoundedCornerShape(RADIUS_TOGGLE)

    Row(
        modifier = modifier
            .background(COLOR_SURFACE_ELEVATED, containerShape)
            .border(BORDER_WIDTH_1, COLOR_BORDER, containerShape)
            .padding(SPACING_1)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(SPACING_1),
    ) {
        portals.forEach { portal ->
            PortalPill(
                label = portal.name,
                isSelected = portal.id == selectedPortalId,
                onClick = { onPortalSelected(portal.id) },
                shape = pillShape,
            )
        }
    }
}

@Composable
private fun PortalPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    shape: RoundedCornerShape,
) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(
            letterSpacing = LETTER_SPACING_TOGGLE,
        ),
        color = if (isSelected) COLOR_BACKGROUND else COLOR_TEXT_DIM,
        modifier = Modifier
            .clip(shape)
            .background(if (isSelected) COLOR_WHITE else COLOR_SURFACE_ELEVATED)
            .clickable(onClick = onClick)
            .padding(horizontal = SPACING_5, vertical = SPACING_2),
    )
}
