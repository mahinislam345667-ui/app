package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CircularScoreBar(
    score: Int,
    label: String,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    strokeWidth: Dp = 10.dp,
    gradientColors: List<Color> = listOf(NeonCyan, NeonPurple)
) {
    val progressAnim = remember { Animatable(0f) }

    LaunchedEffect(score) {
        progressAnim.animateTo(
            targetValue = (score.coerceIn(0, 100) / 100f),
            animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.toPx()
            val strokePx = strokeWidth.toPx()
            val radius = (canvasWidth - strokePx) / 2f

            // Background Track
            drawCircle(
                color = DarkSurfaceVariant,
                radius = radius,
                style = Stroke(width = strokePx)
            )

            // Animated Progress Arc
            val sweepAngle = progressAnim.value * 360f
            drawArc(
                brush = Brush.sweepGradient(gradientColors),
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${(progressAnim.value * 100).toInt()}%",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = (size.value * 0.22f).sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            if (label.isNotEmpty()) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = (size.value * 0.11f).sp,
                        color = TextSecondary
                    )
                )
            }
        }
    }
}
