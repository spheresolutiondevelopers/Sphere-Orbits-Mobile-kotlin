package com.orbits.feature.tasks.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TaskPriorityChip(
    priority: String,
    modifier: Modifier = Modifier
) {
    val (color, text) = when (priority.lowercase()) {
        "critical" -> Pair(MaterialTheme.colorScheme.error, "Critical")
        "high" -> Pair(MaterialTheme.colorScheme.tertiary, "High")
        "medium" -> Pair(MaterialTheme.colorScheme.secondary, "Medium")
        else -> Pair(MaterialTheme.colorScheme.onSurfaceVariant, "Low")
    }

    AssistChip(
        onClick = {},
        label = { Text(text, style = MaterialTheme.typography.labelSmall) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = color.copy(alpha = 0.15f),
            labelColor = color
        ),
        modifier = modifier
    )
}
