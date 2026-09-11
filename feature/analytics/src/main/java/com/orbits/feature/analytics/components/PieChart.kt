package com.orbits.feature.analytics.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.orbits.domain.analytics.CategoryBreakdown
import androidx.compose.ui.graphics.Color as ComposeColor

@Composable
fun PieChart(
    data: List<CategoryBreakdown>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) {
        Box(
            modifier = modifier.height(200.dp),
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

    val colors = listOf(
        ComposeColor(0xFF7C6CF8),
        ComposeColor(0xFF2DD4A0),
        ComposeColor(0xFFFFD166),
        ComposeColor(0xFFFF6B8A),
        ComposeColor(0xFFA855F7),
        ComposeColor(0xFF22D3EE),
        ComposeColor(0xFFF472B6),
        ComposeColor(0xFF34D399)
    )

    val total = data.sumOf { it.count }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Pie chart
        Canvas(
            modifier = Modifier.size(150.dp)
        ) {
            var startAngle = -90f

            data.forEachIndexed { index, item ->
                val sweepAngle = (item.count.toFloat() / total) * 360f
                val color = if (item.color != null) {
                    try {
                        ComposeColor(android.graphics.Color.parseColor(item.color))
                    } catch (e: Exception) {
                        colors[index % colors.size]
                    }
                } else {
                    colors[index % colors.size]
                }

                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    size = Size(size.width, size.height)
                )
                startAngle += sweepAngle
            }
        }

        // Legend
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            data.forEachIndexed { index, item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                if (item.color != null) {
                                    try {
                                        ComposeColor(android.graphics.Color.parseColor(item.color))
                                    } catch (e: Exception) {
                                        colors[index % colors.size]
                                    }
                                } else {
                                    colors[index % colors.size]
                                }
                            )
                    )
                    Text(
                        text = "${item.category} (${item.getFormattedPercentage()})",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp),
                        maxLines = 1
                    )
                }
            }
        }
    }
}
