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
import captiveconnect.composeapp.generated.resources.content_description_back
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_BORDER_MID
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_SURFACE
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_TEXT_SECONDARY
import com.cemalturkcan.captiveconnect.ui.tokens.BORDER_WIDTH_1
import com.cemalturkcan.captiveconnect.ui.tokens.RADIUS_ICON_BUTTON
import com.cemalturkcan.captiveconnect.ui.tokens.SIZE_BACK_ICON
import com.cemalturkcan.captiveconnect.ui.tokens.SIZE_ICON_BUTTON
import org.jetbrains.compose.resources.stringResource

private val ChevronLeftIcon: ImageVector
    get() = ImageVector.Builder(
        name = "ChevronLeft",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(
            fill = null,
            stroke = SolidColor(Color.White),
            strokeLineWidth = 2.5f,
        ) {
            moveTo(15f, 18f)
            lineTo(9f, 12f)
            lineTo(15f, 6f)
        }
    }.build()

@Composable
fun BackIconButton(
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
            imageVector = ChevronLeftIcon,
            contentDescription = stringResource(Res.string.content_description_back),
            modifier = Modifier.size(SIZE_BACK_ICON),
            tint = COLOR_TEXT_SECONDARY,
        )
    }
}
