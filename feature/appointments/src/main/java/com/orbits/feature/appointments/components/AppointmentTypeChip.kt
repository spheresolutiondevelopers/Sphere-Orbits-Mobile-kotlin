package com.orbits.feature.appointments.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AppointmentTypeChip(
    type: String,
    modifier: Modifier = Modifier
) {
    val (color, text, icon) = when (type.lowercase()) {
        "general" -> Triple(MaterialTheme.colorScheme.onSurfaceVariant, "General", "📌")
        "doctor" -> Triple(MaterialTheme.colorScheme.error, "Doctor", "🏥")
        "business" -> Triple(MaterialTheme.colorScheme.primary, "Business", "💼")
        "personal" -> Triple(MaterialTheme.colorScheme.secondary, "Personal", "👤")
        "meeting" -> Triple(MaterialTheme.colorScheme.tertiary, "Meeting", "🤝")
        "consultation" -> Triple(MaterialTheme.colorScheme.primary, "Consultation", "💬")
        "interview" -> Triple(MaterialTheme.colorScheme.secondary, "Interview", "🎯")
        "dentist" -> Triple(MaterialTheme.colorScheme.error, "Dentist", "🦷")
        "therapy" -> Triple(MaterialTheme.colorScheme.tertiary, "Therapy", "🧠")
        "legal" -> Triple(MaterialTheme.colorScheme.primary, "Legal", "⚖️")
        else -> Triple(MaterialTheme.colorScheme.onSurfaceVariant, type, "📋")
    }

    AssistChip(
        onClick = {},
        label = { Text("$icon $text", style = MaterialTheme.typography.labelSmall) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = color.copy(alpha = 0.15f),
            labelColor = color
        ),
        modifier = modifier
    )
}
