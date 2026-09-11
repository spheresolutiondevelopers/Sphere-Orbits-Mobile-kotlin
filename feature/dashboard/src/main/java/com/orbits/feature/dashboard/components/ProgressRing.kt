package com.orbits.feature.dashboard.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbits.domain.analytics.DashboardStats

@Composable
fun ProgressRing(
    stats: DashboardStats?,
    modifier: Modifier = Modifier
) {
    val progress = (stats?.week?.tasksCompletionRate ?: 75.0) / 100.0
    val tasksCompleted = stats?.week?.tasksCompleted ?: 18
    val totalTasks = stats?.week?.getTotalTasks() ?: 24
    
    val animatedProgress by animateFloatAsState(
        targetValue = progress.toFloat(),
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "progress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
            .padding(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Ring Wrap
            Box(
                modifier = Modifier.size(88.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 6.dp.toPx()
                    val radius = (size.minDimension - strokeWidth) / 2
                    
                    // Background ring
                    drawCircle(
                        color = Color(0xFF232235), // CardDark2 approximation
                        radius = radius,
                        style = Stroke(width = strokeWidth)
                    )

                    // Progress ring with gradient
                    val gradient = Brush.linearGradient(
                        colors = listOf(Color(0xFF7C6CF8), Color(0xFFFF6B8A)),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, size.height)
                    )
                    
                    drawArc(
                        brush = gradient,
                        startAngle = -90f,
                        sweepAngle = 360f * animatedProgress,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(animatedProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 28.sp
                        )
                    )
                    Text(
                        text = "DONE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            // Ring Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Great week!",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
                Text(
                    text = "You've completed $tasksCompleted of $totalTasks tasks this week. Keep it up!",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    )
                )
                
                // Mini Bars
                Row(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .height(32.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    val barHeights = listOf(0.4f, 0.7f, 0.9f, 0.6f, 0.8f, 0.5f, 0.3f)
                    barHeights.forEachIndexed { index, height ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(height)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color(0xFF7C6CF8), Color(0xFFA855F7))
                                    ),
                                    alpha = if (index == 4) 1f else 0.6f
                                )
                        )
                    }
                }
            }
        }
    }
}
