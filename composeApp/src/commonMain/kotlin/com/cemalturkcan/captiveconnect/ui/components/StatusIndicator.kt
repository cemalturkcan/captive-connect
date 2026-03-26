package com.cemalturkcan.captiveconnect.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import captiveconnect.composeapp.generated.resources.Res
import captiveconnect.composeapp.generated.resources.content_description_copy_log
import captiveconnect.composeapp.generated.resources.error_login_failed
import captiveconnect.composeapp.generated.resources.error_network
import captiveconnect.composeapp.generated.resources.error_unsupported_portal
import captiveconnect.composeapp.generated.resources.error_verification_failed
import captiveconnect.composeapp.generated.resources.status_already_online
import captiveconnect.composeapp.generated.resources.status_checking
import captiveconnect.composeapp.generated.resources.status_connected
import captiveconnect.composeapp.generated.resources.status_logging_in
import captiveconnect.composeapp.generated.resources.status_verifying
import com.cemalturkcan.captiveconnect.domain.model.ConnectionState
import com.cemalturkcan.captiveconnect.domain.model.ErrorType
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_TEXT_DIM
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_TEXT_SECONDARY
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_WHITE
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_WIFI_CHECKING
import com.cemalturkcan.captiveconnect.ui.tokens.SIZE_COPY_BUTTON
import com.cemalturkcan.captiveconnect.ui.tokens.SIZE_COPY_ICON
import com.cemalturkcan.captiveconnect.ui.tokens.SIZE_STATUS_DOT
import com.cemalturkcan.captiveconnect.ui.tokens.SPACING_3
import org.jetbrains.compose.resources.stringResource

@Composable
fun StatusIndicator(
    connectionState: ConnectionState,
    modifier: Modifier = Modifier,
) {
    val isVisible = connectionState !is ConnectionState.Idle
    val errorLog = (connectionState as? ConnectionState.Error)?.debugLog.orEmpty()

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        val dotColor = resolveStatusDotColor(connectionState)
        val statusText = resolveStatusMessage(connectionState)

        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Canvas(modifier = Modifier.size(SIZE_STATUS_DOT)) {
                drawCircle(color = dotColor)
            }
            Spacer(modifier = Modifier.width(SPACING_3))
            Text(
                text = statusText,
                style = MaterialTheme.typography.bodySmall,
                color = COLOR_TEXT_SECONDARY,
            )
            if (errorLog.isNotEmpty()) {
                Spacer(modifier = Modifier.width(SPACING_3))
                CopyLogButton(debugLog = errorLog)
            }
        }
    }
}

@Composable
@Suppress("DEPRECATION")
private fun CopyLogButton(debugLog: String) {
    val clipboardManager = LocalClipboardManager.current
    val description = stringResource(Res.string.content_description_copy_log)
    Box(
        modifier = Modifier
            .size(SIZE_COPY_BUTTON)
            .clickable(onClickLabel = description) {
                clipboardManager.setText(AnnotatedString(debugLog))
            },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(SIZE_COPY_ICON)) {
            drawCopyIcon(COLOR_TEXT_DIM)
        }
    }
}

private fun DrawScope.drawCopyIcon(color: Color) {
    val s = size.minDimension
    val sw = s * 0.14f
    val stroke = Stroke(width = sw)
    val r = CornerRadius(sw)
    val f = s * 0.64f
    val o = s - f
    drawRoundRect(color, Offset(o, 0f), Size(f, f), r, style = stroke)
    drawRoundRect(color, Offset(0f, o), Size(f, f), r, style = stroke)
}

@Composable
private fun resolveStatusMessage(state: ConnectionState): String = when (state) {
    is ConnectionState.Idle -> ""
    is ConnectionState.Checking -> stringResource(Res.string.status_checking)
    is ConnectionState.LoggingIn -> stringResource(Res.string.status_logging_in)
    is ConnectionState.Verifying -> stringResource(Res.string.status_verifying)
    is ConnectionState.Connected -> stringResource(Res.string.status_connected)
    is ConnectionState.AlreadyOnline -> stringResource(Res.string.status_already_online)
    is ConnectionState.Error -> resolveErrorMessage(state)
}

@Composable
private fun resolveErrorMessage(error: ConnectionState.Error): String = when (error.type) {
    ErrorType.NETWORK -> stringResource(Res.string.error_network)
    ErrorType.UNSUPPORTED_PORTAL -> stringResource(Res.string.error_unsupported_portal)
    ErrorType.LOGIN_FAILED -> stringResource(Res.string.error_login_failed)
    ErrorType.VERIFICATION_FAILED -> stringResource(Res.string.error_verification_failed)
}

private fun resolveStatusDotColor(state: ConnectionState) = when (state) {
    is ConnectionState.Idle -> COLOR_TEXT_DIM
    is ConnectionState.Checking,
    is ConnectionState.LoggingIn,
    is ConnectionState.Verifying -> COLOR_WIFI_CHECKING
    is ConnectionState.Connected -> COLOR_WHITE
    is ConnectionState.AlreadyOnline -> COLOR_WHITE
    is ConnectionState.Error -> COLOR_WIFI_CHECKING
}
