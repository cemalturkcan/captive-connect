package com.cemalturkcan.captiveconnect.ui.primitives

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_BACKGROUND
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_BORDER
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_BORDER_FOCUS
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_BORDER_MID
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_SURFACE_ELEVATED
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_TEXT_DIM
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_TEXT_PRIMARY
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_TEXT_SECONDARY
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_WHITE
import com.cemalturkcan.captiveconnect.ui.tokens.BORDER_WIDTH_1
import com.cemalturkcan.captiveconnect.ui.tokens.RADIUS_BUTTON
import com.cemalturkcan.captiveconnect.ui.tokens.SIZE_BUTTON_HEIGHT
import com.cemalturkcan.captiveconnect.ui.tokens.SIZE_SPINNER
import com.cemalturkcan.captiveconnect.ui.tokens.SIZE_SPINNER_STROKE

private enum class ButtonMode { IDLE, LOADING, SUCCESS, ERROR }

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    isSuccess: Boolean = false,
    isError: Boolean = false,
    enabled: Boolean = true,
) {
    val mode = when {
        isLoading -> ButtonMode.LOADING
        isSuccess -> ButtonMode.SUCCESS
        isError -> ButtonMode.ERROR
        else -> ButtonMode.IDLE
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(SIZE_BUTTON_HEIGHT),
        enabled = enabled && mode != ButtonMode.LOADING,
        shape = RoundedCornerShape(RADIUS_BUTTON),
        border = resolveBorder(mode, enabled),
        colors = ButtonDefaults.buttonColors(
            containerColor = resolveContainerColor(mode),
            contentColor = resolveContentColor(mode),
            disabledContainerColor = COLOR_SURFACE_ELEVATED,
            disabledContentColor = COLOR_TEXT_DIM,
        ),
    ) {
        AnimatedContent(
            targetState = mode,
            transitionSpec = {
                (fadeIn() + scaleIn(initialScale = 0.8f))
                    .togetherWith(fadeOut() + scaleOut(targetScale = 0.8f))
            },
        ) { currentMode ->
            Box(contentAlignment = Alignment.Center) {
                if (currentMode == ButtonMode.LOADING) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(SIZE_SPINNER),
                        color = COLOR_TEXT_SECONDARY,
                        strokeWidth = SIZE_SPINNER_STROKE,
                    )
                } else {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}

private fun resolveContainerColor(mode: ButtonMode) = when (mode) {
    ButtonMode.IDLE -> COLOR_WHITE
    ButtonMode.LOADING -> COLOR_SURFACE_ELEVATED
    ButtonMode.SUCCESS -> COLOR_WHITE
    ButtonMode.ERROR -> COLOR_BACKGROUND
}

private fun resolveContentColor(mode: ButtonMode) = when (mode) {
    ButtonMode.IDLE -> COLOR_BACKGROUND
    ButtonMode.LOADING -> COLOR_TEXT_SECONDARY
    ButtonMode.SUCCESS -> COLOR_BACKGROUND
    ButtonMode.ERROR -> COLOR_TEXT_PRIMARY
}

private fun resolveBorder(mode: ButtonMode, enabled: Boolean): BorderStroke? = when {
    !enabled -> BorderStroke(BORDER_WIDTH_1, COLOR_BORDER)
    mode == ButtonMode.LOADING -> BorderStroke(BORDER_WIDTH_1, COLOR_BORDER_MID)
    mode == ButtonMode.ERROR -> BorderStroke(BORDER_WIDTH_1, COLOR_BORDER_FOCUS)
    else -> null
}
