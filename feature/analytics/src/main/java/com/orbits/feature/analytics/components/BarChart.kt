package com.orbits.feature.analytics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.orbits.domain.analytics.TaskCompletionTrend

@Composable
fun BarChart(
    data: TaskCompletionTrend,
    modifier: Modifier = Modifier
) {
    if (!data.isValid()) {
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

    val maxValue = data.getMaxValue().coerceAtLeast(1)

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Text(
                    text = "Completed",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(MaterialTheme.colorScheme.secondary)
                )
                Text(
                    text = "Created",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(MaterialTheme.colorScheme.error)
                )
                Text(
                    text = "Overdue",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Bars
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            data.labels.forEachIndexed { index, label ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    // Completed bar
                    val completedHeight = if (data.completed[index] > 0) {
                        (data.completed[index].toFloat() / maxValue * 120).coerceAtLeast(4f)
                    } else 4f
                    Box(
                        modifier = Modifier
                            .width(16.dp)
                            .height(completedHeight.dp)
                            .background(MaterialTheme.colorScheme.primary)
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Created bar
                    val createdHeight = if (data.created[index] > 0) {
                        (data.created[index].toFloat() / maxValue * 120).coerceAtLeast(4f)
                    } else 4f
                    Box(
                        modifier = Modifier
                            .width(16.dp)
                            .height(createdHeight.dp)
                            .background(MaterialTheme.colorScheme.secondary)
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Overdue bar
                    val overdueHeight = if (data.overdue[index] > 0) {
                        (data.overdue[index].toFloat() / maxValue * 120).coerceAtLeast(4f)
                    } else 4f
                    Box(
                        modifier = Modifier
                            .width(16.dp)
                            .height(overdueHeight.dp)
                            .background(MaterialTheme.colorScheme.error)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
