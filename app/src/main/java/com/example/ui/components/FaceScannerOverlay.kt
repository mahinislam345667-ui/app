package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple

@Composable
fun FaceScannerOverlay(
    modifier: Modifier = Modifier,
    isScanning: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "LaserScanner")
    val laserYRatio by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LaserPosition"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        val boxWidth = width * 0.75f
        val boxHeight = height * 0.75f
        val left = (width - boxWidth) / 2f
        val top = (height - boxHeight) / 2f
        val cornerLen = 32.dp.toPx()
        val strokeWidth = 3.dp.toPx()

        // Draw Corner Target Markers
        val path = Path().apply {
            // Top Left
            moveTo(left, top + cornerLen)
            lineTo(left, top)
            lineTo(left + cornerLen, top)

            // Top Right
            moveTo(left + boxWidth - cornerLen, top)
            lineTo(left + boxWidth, top)
            lineTo(left + boxWidth, top + cornerLen)

            // Bottom Right
            moveTo(left + boxWidth, top + boxHeight - cornerLen)
            lineTo(left + boxWidth, top + boxHeight)
            lineTo(left + boxWidth - cornerLen, top + boxHeight)

            // Bottom Left
            moveTo(left + cornerLen, top + boxHeight)
            lineTo(left, top + boxHeight)
            lineTo(left, top + boxHeight - cornerLen)
        }

        drawPath(
            path = path,
            color = NeonCyan,
            style = Stroke(width = strokeWidth)
        )

        // Subtle Target Box Frame
        drawRoundRect(
            color = NeonCyan.copy(alpha = 0.2f),
            topLeft = Offset(left, top),
            size = Size(boxWidth, boxHeight),
            cornerRadius = CornerRadius(16.dp.toPx()),
            style = Stroke(width = 1.dp.toPx())
        )

        // Animated Laser Beam Line
        if (isScanning) {
            val laserY = top + (boxHeight * laserYRatio)
            val laserBrush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    NeonCyan,
                    NeonPurple,
                    NeonCyan,
                    Color.Transparent
                )
            )

            // Laser line
            drawLine(
                brush = laserBrush,
                start = Offset(left - 10f, laserY),
                end = Offset(left + boxWidth + 10f, laserY),
                strokeWidth = 4.dp.toPx()
            )

            // Soft glow gradient behind laser
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(NeonCyan.copy(alpha = 0.25f), Color.Transparent),
                    startY = laserY - 30.dp.toPx(),
                    endY = laserY
                ),
                topLeft = Offset(left, laserY - 30.dp.toPx()),
                size = Size(boxWidth, 30.dp.toPx())
            )
        }
    }
}
