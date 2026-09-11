package com.orbits.feature.analytics.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TrendIndicator(
    trend: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val (color, icon, label) = when (trend.lowercase()) {
        "up" -> Triple(
            MaterialTheme.colorScheme.primary,
            "↑",
            "Improving"
        )
        "down" -> Triple(
            MaterialTheme.colorScheme.error,
            "↓",
            "Declining"
        )
        else -> Triple(
            MaterialTheme.colorScheme.onSurfaceVariant,
            "→",
            "Stable"
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = color.copy(alpha = 0.15f)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = icon,
                    color = color,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = label,
                    color = color,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = value,
                    color = color,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
