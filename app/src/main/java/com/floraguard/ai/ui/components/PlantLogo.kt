package com.floraguard.ai.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp

@Composable
fun PlantLogo(
    modifier: Modifier = Modifier,
    animate: Boolean = true
) {
    val transition = rememberInfiniteTransition(label = "plant_logo")
    val colorT by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color_t"
    )
    val pulse by transition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val activeLeaf = if (animate) {
        lerp(Color(0xFF5D4037), Color(0xFF2F8F4E), colorT)
    } else {
        Color(0xFF2F8F4E)
    }
    val activeStem = if (animate) {
        lerp(Color(0xFF4E342E), Color(0xFF1F6E4A), colorT)
    } else {
        Color(0xFF1F6E4A)
    }
    val scale = if (animate) pulse else 1f

    Canvas(modifier = modifier) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val stemHeight = size.height * 0.35f * scale
        val stemTop = centerY - stemHeight * 0.35f
        val stemBottom = centerY + stemHeight * 0.65f

        drawLine(
            color = activeStem,
            start = Offset(centerX, stemTop),
            end = Offset(centerX, stemBottom),
            strokeWidth = size.minDimension * 0.06f * scale,
            cap = StrokeCap.Round
        )

        val leafSize = Size(
            width = size.width * 0.30f * scale,
            height = size.height * 0.18f * scale
        )
        drawOval(
            color = activeLeaf,
            topLeft = Offset(centerX - leafSize.width * 1.1f, centerY - leafSize.height * 0.7f),
            size = leafSize
        )
        drawOval(
            color = activeLeaf,
            topLeft = Offset(centerX + leafSize.width * 0.1f, centerY - leafSize.height * 0.9f),
            size = leafSize
        )

        drawLine(
            color = activeStem.copy(alpha = 0.7f),
            start = Offset(centerX - leafSize.width * 0.45f, centerY - leafSize.height * 0.5f),
            end = Offset(centerX - leafSize.width * 0.05f, centerY - leafSize.height * 0.85f),
            strokeWidth = size.minDimension * 0.02f * scale,
            cap = StrokeCap.Round
        )
        drawLine(
            color = activeStem.copy(alpha = 0.7f),
            start = Offset(centerX + leafSize.width * 0.55f, centerY - leafSize.height * 0.7f),
            end = Offset(centerX + leafSize.width * 0.15f, centerY - leafSize.height * 1.0f),
            strokeWidth = size.minDimension * 0.02f * scale,
            cap = StrokeCap.Round
        )

        drawCircle(
            color = activeLeaf.copy(alpha = 0.18f),
            radius = size.minDimension * 0.44f * scale,
            center = Offset(centerX, centerY)
        )

        drawCircle(
            color = activeLeaf.copy(alpha = 0.25f),
            radius = size.minDimension * 0.12f * scale,
            center = Offset(centerX, centerY + size.height * 0.18f)
        )

        drawCircle(
            color = activeStem.copy(alpha = 0.55f),
            radius = size.minDimension * 0.06f * scale,
            center = Offset(centerX, stemBottom)
        )

        drawArc(
            color = activeStem.copy(alpha = 0.35f),
            startAngle = -180f,
            sweepAngle = 160f,
            useCenter = false,
            topLeft = Offset(centerX - size.minDimension * 0.28f, stemBottom - size.minDimension * 0.10f),
            size = Size(size.minDimension * 0.56f, size.minDimension * 0.22f),
            style = Stroke(width = size.minDimension * 0.025f)
        )
    }
}
