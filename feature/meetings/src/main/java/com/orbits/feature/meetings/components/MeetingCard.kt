package com.orbits.feature.meetings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.orbits.domain.meetings.Meeting

@Composable
fun MeetingCard(
    meeting: Meeting,
    onMeetingClick: () -> Unit,
    onJoinClick: () -> Unit,
    onStatusUpdate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onMeetingClick() },
        colors = CardDefaults.cardColors(
            containerColor = when (meeting.status) {
                "live" -> MaterialTheme.colorScheme.primaryContainer
                "cancelled" -> MaterialTheme.colorScheme.errorContainer
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Title + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = meeting.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                MeetingStatusChip(status = meeting.status)
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Description
            if (meeting.description != null) {
                Text(
                    text = meeting.description ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Date, Time, Platform
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = meeting.getFormattedDateTime(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (meeting.meetingPlatform != null) {
                    Text(
                        text = "📱 ${meeting.getPlatformDisplayName()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (meeting.meetingLink != null && meeting.status == "live") {
                    Text(
                        text = "🔴 Live",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (meeting.status in listOf("scheduled", "live")) {
                    JoinMeetingButton(
                        onClick = onJoinClick,
                        isLive = meeting.status == "live",
                        modifier = Modifier.weight(1f)
                    )
                }
                if (meeting.status == "scheduled") {
                    OutlinedButton(
                        onClick = { onStatusUpdate("cancelled") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}
