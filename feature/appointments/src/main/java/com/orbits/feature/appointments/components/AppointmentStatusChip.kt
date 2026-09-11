package com.orbits.feature.appointments.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AppointmentStatusChip(
    status: String,
    modifier: Modifier = Modifier
) {
    val (color, text) = when (status.lowercase()) {
        "scheduled" -> Pair(MaterialTheme.colorScheme.secondary, "Scheduled")
        "confirmed" -> Pair(MaterialTheme.colorScheme.primary, "Confirmed")
        "cancelled" -> Pair(MaterialTheme.colorScheme.error, "Cancelled")
        "completed" -> Pair(MaterialTheme.colorScheme.primary, "Completed")
        "rescheduled" -> Pair(MaterialTheme.colorScheme.tertiary, "Rescheduled")
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
