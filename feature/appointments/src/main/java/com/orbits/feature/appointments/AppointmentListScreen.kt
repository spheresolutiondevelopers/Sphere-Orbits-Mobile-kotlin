package com.orbits.feature.appointments

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
import com.orbits.feature.appointments.components.*

@Composable
fun AppointmentListScreen(
    viewModel: AppointmentViewModel = hiltViewModel(),
    onNavigateToAppointmentDetail: (String) -> Unit,
    onNavigateToCreate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // ─── Filter Bar ────────────────────────────────────
            AppointmentFilterBar(
                selectedFilter = state.selectedFilter,
                selectedType = state.selectedType,
                onFilterSelected = { viewModel.handleEvent(AppointmentEvent.SelectFilter(it)) },
                onTypeSelected = { viewModel.handleEvent(AppointmentEvent.SelectType(it)) },
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
                            onClick = { viewModel.handleEvent(AppointmentEvent.DismissError) }
                        ) {
                            Text("Dismiss")
                        }
                    }
                }
            }

            // ─── Appointment List ──────────────────────────────
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.filteredAppointments.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "📅",
                            style = MaterialTheme.typography.displayLarge
                        )
                        Text(
                            text = "No appointments found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(state.filteredAppointments) { appointment ->
                        AppointmentCard(
                            appointment = appointment,
                            onAppointmentClick = {
                                viewModel.handleEvent(AppointmentEvent.NavigateToAppointmentDetail(appointment.id))
                                onNavigateToAppointmentDetail(appointment.id)
                            },
                            onStatusUpdate = { status ->
                                when (status) {
                                    "cancelled" -> viewModel.handleEvent(AppointmentEvent.CancelAppointment(appointment.id))
                                    "confirmed" -> viewModel.handleEvent(AppointmentEvent.UpdateAppointment(appointment))
                                    "completed" -> viewModel.handleEvent(AppointmentEvent.CompleteAppointment(appointment.id))
                                }
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
                contentDescription = "Create appointment"
            )
        }
    }
}
