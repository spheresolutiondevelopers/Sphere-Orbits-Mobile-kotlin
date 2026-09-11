package com.orbits.feature.meetings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.orbits.domain.meetings.Meeting
import com.orbits.feature.meetings.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetingDetailScreen(
    meetingId: String,
    viewModel: MeetingViewModel = hiltViewModel(),
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val meeting = state.meetings.find { it.id == meetingId }
    val isLoading = state.isLoading

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (meeting == null) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("Meeting not found")
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = meeting.title,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.weight(1f)
                )

                if (meeting.isEditable()) {
                    IconButton(onClick = { /* Navigate to edit */ }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit"
                        )
                    }
                }
            }

            // ─── Status ────────────────────────────────────
            MeetingStatusChip(status = meeting.status)

            // ─── Date & Time ──────────────────────────────
            InfoRow(
                label = "Start",
                value = meeting.getFormattedDateTime()
            )
            InfoRow(
                label = "Duration",
                value = "${meeting.getDurationMinutes()} minutes"
            )

            // ─── Platform & Link ──────────────────────────
            if (meeting.meetingPlatform != null) {
                InfoRow(
                    label = "Platform",
                    value = meeting.getPlatformDisplayName() ?: meeting.meetingPlatform ?: ""
                )
            }
            if (meeting.meetingLink != null) {
                InfoRow(
                    label = "Link",
                    value = meeting.meetingLink ?: ""
                )
            }

            // ─── Description ──────────────────────────────
            if (meeting.description != null) {
                HorizontalDivider()
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = meeting.description ?: "",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // ─── Agenda ──────────────────────────────────
            if (meeting.agenda != null) {
                HorizontalDivider()
                Text(
                    text = "Agenda",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = meeting.agenda ?: "",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // ─── Action Items ─────────────────────────────
            if (meeting.actionItems.isNotEmpty()) {
                HorizontalDivider()
                Text(
                    text = "Action Items (${meeting.actionItems.size})",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                meeting.actionItems.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = item.getStatusLabel(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ─── Join Button ─────────────────────────────
            if (meeting.status in listOf("scheduled", "live")) {
                Button(
                    onClick = { viewModel.handleEvent(MeetingEvent.JoinMeeting(meeting.id)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (meeting.status == "live") {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                ) {
                    Text(if (meeting.status == "live") "Join Live" else "Join")
                }
            }

            // ─── Metadata ──────────────────────────────────
            HorizontalDivider()
            Text(
                text = "Created: ${meeting.createdAt}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Last Updated: ${meeting.updatedAt}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // ─── Delete Button ─────────────────────────────
            if (meeting.isCancellable()) {
                OutlinedButton(
                    onClick = {
                        viewModel.handleEvent(MeetingEvent.UpdateMeetingStatus(meeting.id, "cancelled"))
                        onBack()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Cancel Meeting")
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
