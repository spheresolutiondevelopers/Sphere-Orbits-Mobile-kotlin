package com.orbits.feature.appointments.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.orbits.domain.appointments.Appointment

@Composable
fun AppointmentCard(
    appointment: Appointment,
    onAppointmentClick: () -> Unit,
    onStatusUpdate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onAppointmentClick() },
        colors = CardDefaults.cardColors(
            containerColor = when (appointment.status) {
                "cancelled" -> MaterialTheme.colorScheme.errorContainer
                "completed" -> MaterialTheme.colorScheme.surfaceVariant
                "confirmed" -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Type + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppointmentTypeChip(type = appointment.appointmentType)
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = appointment.getFormattedTime(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                AppointmentStatusChip(status = appointment.status)
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Title
            Text(
                text = appointment.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Location
            if (appointment.location != null) {
                Text(
                    text = "📍 ${appointment.location}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Virtual indicator
            if (appointment.isVirtual && appointment.meetingLink != null) {
                Text(
                    text = "🎥 ${appointment.getMeetingPlatformDisplayName() ?: "Virtual"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (appointment.status !in listOf("cancelled", "completed")) {
                    OutlinedButton(
                        onClick = { onStatusUpdate("cancelled") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = { onStatusUpdate("confirmed") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Confirm")
                    }
                } else if (appointment.status == "scheduled" || appointment.status == "confirmed") {
                    Button(
                        onClick = { onStatusUpdate("completed") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Complete")
                    }
                }
            }
        }
    }
}
