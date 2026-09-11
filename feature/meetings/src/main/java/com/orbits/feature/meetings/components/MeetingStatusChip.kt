package com.orbits.feature.meetings.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MeetingStatusChip(
    status: String,
    modifier: Modifier = Modifier
) {
    val (color, text) = when (status.lowercase()) {
        "scheduled" -> Pair(MaterialTheme.colorScheme.secondary, "Scheduled")
        "live" -> Pair(MaterialTheme.colorScheme.error, "Live")
        "ended" -> Pair(MaterialTheme.colorScheme.onSurfaceVariant, "Ended")
        "cancelled" -> Pair(MaterialTheme.colorScheme.error, "Cancelled")
        else -> Pair(MaterialTheme.colorScheme.onSurfaceVariant, status)
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
