package com.orbits.feature.tasks.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TaskStatusChip(
    status: String,
    modifier: Modifier = Modifier
) {
    val (color, text) = when (status.lowercase()) {
        "completed" -> Pair(MaterialTheme.colorScheme.primary, "Completed")
        "in_progress" -> Pair(MaterialTheme.colorScheme.secondary, "In Progress")
        "cancelled" -> Pair(MaterialTheme.colorScheme.error, "Cancelled")
        "deferred" -> Pair(MaterialTheme.colorScheme.onSurfaceVariant, "Deferred")
        else -> Pair(MaterialTheme.colorScheme.onSurfaceVariant, "Pending")
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
