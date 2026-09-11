package com.orbits.feature.analytics.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.orbits.domain.analytics.FocusTime

@Composable
fun LineChart(
    data: List<FocusTime>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) {
        Box(
            modifier = modifier.height(150.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No data available",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val maxHours = data.maxOfOrNull { it.hours }?.coerceAtLeast(1.0) ?: 1.0

    val primaryColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            val width = size.width
            val height = size.height
            val padding = 20f
            val chartWidth = width - padding * 2
            val chartHeight = height - padding * 2

            if (data.size > 1) {
                val path = Path()
                val xStep = chartWidth / (data.size - 1)

                data.forEachIndexed { index, point ->
                    val x = padding + index * xStep
                    val y = padding + chartHeight - (point.hours / maxHours * chartHeight).toFloat()

                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                }

                drawPath(
                    path = path,
                    color = primaryColor,
                    style = Stroke(width = 3f, cap = StrokeCap.Round)
                )

                // Fill area
                val fillPath = Path().apply {
                    val firstX = padding
                    val lastX = padding + (data.size - 1) * xStep
                    moveTo(firstX, padding + chartHeight)
                    data.forEachIndexed { index, point ->
                        val x = padding + index * xStep
                        val y = padding + chartHeight - (point.hours / maxHours * chartHeight).toFloat()
                        lineTo(x, y)
                    }
                    lineTo(lastX, padding + chartHeight)
                    close()
                }

                drawPath(
                    path = fillPath,
                    color = primaryColor.copy(alpha = 0.2f)
                )
            }
        }

        // X-axis labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            data.forEach { point ->
                Text(
                    text = point.getFormattedDate(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}
