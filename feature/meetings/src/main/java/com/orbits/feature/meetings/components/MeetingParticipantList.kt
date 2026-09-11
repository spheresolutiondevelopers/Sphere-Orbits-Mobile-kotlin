package com.orbits.feature.meetings.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.orbits.domain.meetings.MeetingParticipant

@Composable
fun MeetingParticipantList(
    participants: List<MeetingParticipant>,
    modifier: Modifier = Modifier
) {
    if (participants.isEmpty()) {
        Text(
            text = "No participants",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier.padding(16.dp)
        )
    } else {
        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(participants) { participant ->
                ParticipantItem(participant = participant)
            }
        }
    }
}

@Composable
private fun ParticipantItem(
    participant: MeetingParticipant
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = participant.fullName ?: participant.email,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = participant.getRoleDisplayName(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = participant.getStatusDisplayName(),
            style = MaterialTheme.typography.labelSmall,
            color = when (participant.invitationStatus) {
                "accepted" -> MaterialTheme.colorScheme.primary
                "declined" -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}
