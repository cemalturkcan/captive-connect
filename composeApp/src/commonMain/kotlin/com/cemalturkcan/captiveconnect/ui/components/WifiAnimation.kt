package com.cemalturkcan.captiveconnect.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.cemalturkcan.captiveconnect.domain.model.ConnectionState
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_ERROR_X
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_TEXT_DIM
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_WHITE
import com.cemalturkcan.captiveconnect.ui.theme.COLOR_WIFI_CHECKING
import kotlin.math.PI
import kotlin.math.min
import kotlin.math.sin

private const val ANIMATION_WIDTH_FRACTION = 0.45f
private const val ARC_COUNT = 3
private const val ARC_SWEEP_ANGLE = 60f
private const val ARC_START_ANGLE = -120f
private const val ARC_STROKE_WIDTH_RATIO = 0.035f
private const val DOT_RADIUS_RATIO = 0.04f
private const val ARC_GAP_RATIO = 0.08f
private const val ARC_MIN_RADIUS_RATIO = 0.25f
private const val PULSE_DURATION = 1200
private const val ACTIVE_ALPHA_MIN = 0.4f
private const val ACTIVE_ALPHA_MAX = 1.0f
private const val ERROR_X_STROKE_RATIO = 0.025f
private const val ERROR_X_SIZE_RATIO = 0.12f

@Composable
fun WifiAnimation(
    connectionState: ConnectionState,
    modifier: Modifier = Modifier,
) {
    val primaryColor by animateColorAsState(
        targetValue = resolveColor(connectionState),
        animationSpec = tween(durationMillis = 350),
    )

    val isAnimating = connectionState is ConnectionState.Checking
        || connectionState is ConnectionState.LoggingIn
        || connectionState is ConnectionState.Verifying

    val isConnected = connectionState is ConnectionState.Connected
        || connectionState is ConnectionState.AlreadyOnline

    val isError = connectionState is ConnectionState.Error

    val infiniteTransition = rememberInfiniteTransition()

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = ACTIVE_ALPHA_MIN,
        targetValue = ACTIVE_ALPHA_MAX,
        animationSpec = infiniteRepeatable(
            animation = tween(PULSE_DURATION, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
    )

    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(PULSE_DURATION * 2, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
    )

    val errorXProgress by animateFloatAsState(
        targetValue = if (isError) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
    )

    Box(
        modifier = modifier
            .fillMaxWidth(ANIMATION_WIDTH_FRACTION)
            .aspectRatio(1.33f),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasSize = min(size.width, size.height)
            val center = Offset(size.width / 2f, size.height / 2f)
            val strokeWidth = canvasSize * ARC_STROKE_WIDTH_RATIO
            val dotRadius = canvasSize * DOT_RADIUS_RATIO
            val arcGap = canvasSize * ARC_GAP_RATIO
            val minRadius = canvasSize * ARC_MIN_RADIUS_RATIO

            for (index in 0 until ARC_COUNT) {
                val arcAlpha = when {
                    isAnimating -> resolveAnimatingAlpha(
                        index, wavePhase, pulseAlpha,
                    )
                    isConnected -> ACTIVE_ALPHA_MAX
                    else -> ACTIVE_ALPHA_MAX
                }

                val radius = minRadius + (index * arcGap)

                drawWifiArc(
                    center = center,
                    radius = radius,
                    color = primaryColor.copy(alpha = arcAlpha),
                    strokeWidth = strokeWidth,
                )
            }

            drawCircle(
                color = primaryColor,
                radius = dotRadius,
                center = center,
            )

            if (errorXProgress > 0f) {
                drawErrorX(
                    center = center,
                    size = canvasSize * ERROR_X_SIZE_RATIO,
                    strokeWidth = canvasSize * ERROR_X_STROKE_RATIO,
                    color = COLOR_ERROR_X,
                    progress = errorXProgress,
                )
            }
        }
    }
}

private fun resolveColor(state: ConnectionState): Color = when (state) {
    is ConnectionState.Idle -> COLOR_TEXT_DIM
    is ConnectionState.Checking -> COLOR_WIFI_CHECKING
    is ConnectionState.LoggingIn -> COLOR_WIFI_CHECKING
    is ConnectionState.Verifying -> COLOR_WIFI_CHECKING
    is ConnectionState.Connected -> COLOR_WHITE
    is ConnectionState.AlreadyOnline -> COLOR_WHITE
    is ConnectionState.Error -> COLOR_TEXT_DIM
}

private fun resolveAnimatingAlpha(
    index: Int,
    wavePhase: Float,
    pulseAlpha: Float,
): Float {
    val offset = index.toFloat() / ARC_COUNT
    val phase = (wavePhase + offset) % 1f
    val wave = (sin(phase * 2 * PI) * 0.5 + 0.5).toFloat()
    return ACTIVE_ALPHA_MIN + (wave * (pulseAlpha - ACTIVE_ALPHA_MIN))
}

private fun DrawScope.drawWifiArc(
    center: Offset,
    radius: Float,
    color: Color,
    strokeWidth: Float,
) {
    drawArc(
        color = color,
        startAngle = ARC_START_ANGLE,
        sweepAngle = ARC_SWEEP_ANGLE,
        useCenter = false,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = Size(radius * 2, radius * 2),
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
    )
}

private fun DrawScope.drawErrorX(
    center: Offset,
    size: Float,
    strokeWidth: Float,
    color: Color,
    progress: Float,
) {
    val halfSize = size * progress

    drawLine(
        color = color,
        start = Offset(center.x - halfSize, center.y - halfSize),
        end = Offset(center.x + halfSize, center.y + halfSize),
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round,
    )
    drawLine(
        color = color,
        start = Offset(center.x + halfSize, center.y - halfSize),
        end = Offset(center.x - halfSize, center.y + halfSize),
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round,
    )
}
