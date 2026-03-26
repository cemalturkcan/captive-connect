package com.cemalturkcan.captiveconnect.ui.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import captiveconnect.composeapp.generated.resources.Res
import captiveconnect.composeapp.generated.resources.settings
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_BORDER_MID
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_SURFACE
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_TEXT_SECONDARY
import com.cemalturkcan.captiveconnect.ui.tokens.BORDER_WIDTH_1
import com.cemalturkcan.captiveconnect.ui.tokens.RADIUS_ICON_BUTTON
import com.cemalturkcan.captiveconnect.ui.tokens.SIZE_ICON_BUTTON
import com.cemalturkcan.captiveconnect.ui.tokens.SIZE_ICON_BUTTON_ICON
import org.jetbrains.compose.resources.stringResource

private val GearIcon: ImageVector
    get() = ImageVector.Builder(
        name = "Gear",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(
            fill = null,
            stroke = SolidColor(Color.White),
            strokeLineWidth = 2f,
        ) {
            moveTo(12f, 15f)
            arcTo(3f, 3f, 0f, true, true, 12f, 9f)
            arcTo(3f, 3f, 0f, true, true, 12f, 15f)
        }
        path(
            fill = null,
            stroke = SolidColor(Color.White),
            strokeLineWidth = 2f,
        ) {
            moveTo(19.4f, 15f)
            arcToRelative(1.65f, 1.65f, 0f, false, false, 0.33f, 1.82f)
            lineToRelative(0.06f, 0.06f)
            arcToRelative(2f, 2f, 0f, false, true, -2.83f, 2.83f)
            lineToRelative(-0.06f, -0.06f)
            arcToRelative(1.65f, 1.65f, 0f, false, false, -1.82f, -0.33f)
            arcToRelative(1.65f, 1.65f, 0f, false, false, -1f, 1.51f)
            verticalLineTo(21f)
            arcToRelative(2f, 2f, 0f, false, true, -4f, 0f)
            verticalLineToRelative(-0.09f)
            arcTo(1.65f, 1.65f, 0f, false, false, 9f, 19.4f)
            arcToRelative(1.65f, 1.65f, 0f, false, false, -1.82f, 0.33f)
            lineToRelative(-0.06f, 0.06f)
            arcToRelative(2f, 2f, 0f, false, true, -2.83f, -2.83f)
            lineToRelative(0.06f, -0.06f)
            arcTo(1.65f, 1.65f, 0f, false, false, 4.68f, 15f)
            arcToRelative(1.65f, 1.65f, 0f, false, false, -1.51f, -1f)
            horizontalLineTo(3f)
            arcToRelative(2f, 2f, 0f, false, true, 0f, -4f)
            horizontalLineToRelative(0.09f)
            arcTo(1.65f, 1.65f, 0f, false, false, 4.6f, 9f)
            arcToRelative(1.65f, 1.65f, 0f, false, false, -0.33f, -1.82f)
            lineToRelative(-0.06f, -0.06f)
            arcToRelative(2f, 2f, 0f, false, true, 2.83f, -2.83f)
            lineToRelative(0.06f, 0.06f)
            arcTo(1.65f, 1.65f, 0f, false, false, 9f, 4.68f)
            arcToRelative(1.65f, 1.65f, 0f, false, false, 1f, -1.51f)
            verticalLineTo(3f)
            arcToRelative(2f, 2f, 0f, false, true, 4f, 0f)
            verticalLineToRelative(0.09f)
            arcToRelative(1.65f, 1.65f, 0f, false, false, 1f, 1.51f)
            arcToRelative(1.65f, 1.65f, 0f, false, false, 1.82f, -0.33f)
            lineToRelative(0.06f, -0.06f)
            arcToRelative(2f, 2f, 0f, false, true, 2.83f, 2.83f)
            lineToRelative(-0.06f, 0.06f)
            arcTo(1.65f, 1.65f, 0f, false, false, 19.4f, 9f)
            arcToRelative(1.65f, 1.65f, 0f, false, false, 1.51f, 1f)
            horizontalLineTo(21f)
            arcToRelative(2f, 2f, 0f, false, true, 0f, 4f)
            horizontalLineToRelative(-0.09f)
            arcToRelative(1.65f, 1.65f, 0f, false, false, -1.51f, 1f)
            close()
        }
    }.build()

@Composable
fun SettingsIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedIconButton(
        onClick = onClick,
        modifier = modifier.size(SIZE_ICON_BUTTON),
        shape = RoundedCornerShape(RADIUS_ICON_BUTTON),
        border = BorderStroke(BORDER_WIDTH_1, COLOR_BORDER_MID),
        colors = IconButtonDefaults.outlinedIconButtonColors(
            containerColor = COLOR_SURFACE,
            contentColor = COLOR_TEXT_SECONDARY,
        ),
    ) {
        Icon(
            imageVector = GearIcon,
            contentDescription = stringResource(Res.string.settings),
            modifier = Modifier.size(SIZE_ICON_BUTTON_ICON),
            tint = COLOR_TEXT_SECONDARY,
        )
    }
}
