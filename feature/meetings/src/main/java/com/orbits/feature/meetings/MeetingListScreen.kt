package com.orbits.feature.meetings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.orbits.feature.meetings.components.*

@Composable
fun MeetingListScreen(
    viewModel: MeetingViewModel = hiltViewModel(),
    onNavigateToMeetingDetail: (String) -> Unit,
    onNavigateToCreate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // ─── Filter Bar ────────────────────────────────────
            MeetingFilterBar(
                selectedFilter = state.selectedFilter,
                onFilterSelected = { viewModel.handleEvent(MeetingEvent.SelectFilter(it)) },
                modifier = Modifier.padding(top = 8.dp)
            )

            // ─── Error Message ──────────────────────────────────
            if (state.errorMessage != null) {
                Card(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = state.errorMessage!!,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(
                            onClick = { viewModel.handleEvent(MeetingEvent.DismissError) }
                        ) {
                            Text("Dismiss")
                        }
                    }
                }
            }

            // ─── Meeting List ────────────────────────────────────
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.filteredMeetings.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No meetings found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(state.filteredMeetings) { meeting ->
                        MeetingCard(
                            meeting = meeting,
                            onMeetingClick = {
                                viewModel.handleEvent(MeetingEvent.NavigateToMeetingDetail(meeting.id))
                                onNavigateToMeetingDetail(meeting.id)
                            },
                            onJoinClick = {
                                viewModel.handleEvent(MeetingEvent.JoinMeeting(meeting.id))
                            },
                            onStatusUpdate = { status ->
                                viewModel.handleEvent(MeetingEvent.UpdateMeetingStatus(meeting.id, status))
                            }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onNavigateToCreate,
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Create meeting"
            )
        }
    }
}
