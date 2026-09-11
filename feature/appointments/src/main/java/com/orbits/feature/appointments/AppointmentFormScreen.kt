package com.orbits.feature.appointments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.orbits.feature.appointments.components.AvailabilityIndicator
import com.orbits.feature.appointments.components.AppointmentTimePicker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentFormScreen(
    appointmentId: String?,
    viewModel: AppointmentViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formState by viewModel.formState.collectAsState()
    val isEditMode = formState.isEditMode

    LaunchedEffect(formState.isSuccess) {
        if (formState.isSuccess) {
            onSaveSuccess()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
            // Title
            OutlinedTextField(
                value = formState.title,
                onValueChange = { viewModel.handleEvent(AppointmentEvent.FormTitleChanged(it)) },
                label = { Text("Title *") },
                modifier = Modifier.fillMaxWidth(),
                isError = formState.titleError != null,
                supportingText = { if (formState.titleError != null) Text(formState.titleError ?: "") },
                singleLine = true
            )

            // Description
            OutlinedTextField(
                value = formState.description,
                onValueChange = { viewModel.handleEvent(AppointmentEvent.FormDescriptionChanged(it)) },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            // Type
            OutlinedTextField(
                value = formState.appointmentType,
                onValueChange = { viewModel.handleEvent(AppointmentEvent.FormTypeChanged(it)) },
                label = { Text("Type") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("general, doctor, business, personal, meeting...") }
            )

            // Time picker
            AppointmentTimePicker(
                startDateTime = formState.startDateTime,
                endDateTime = formState.endDateTime,
                onStartDateTimeChange = { viewModel.handleEvent(AppointmentEvent.FormStartDateTimeChanged(it)) },
                onEndDateTimeChange = { viewModel.handleEvent(AppointmentEvent.FormEndDateTimeChanged(it)) },
                allDayEvent = formState.allDayEvent,
                onAllDayToggle = { viewModel.handleEvent(AppointmentEvent.FormAllDayEventToggled(it)) }
            )

            if (formState.dateTimeError != null) {
                Text(
                    text = formState.dateTimeError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Availability check
            if (formState.startDateTime.isNotBlank() && formState.endDateTime.isNotBlank()) {
                AvailabilityIndicator(
                    isAvailable = null,
                    isLoading = formState.isLoading
                )
            }

            // Location
            OutlinedTextField(
                value = formState.location,
                onValueChange = { viewModel.handleEvent(AppointmentEvent.FormLocationChanged(it)) },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Virtual toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Virtual meeting", style = MaterialTheme.typography.bodyMedium)
                Switch(
                    checked = formState.isVirtual,
                    onCheckedChange = { viewModel.handleEvent(AppointmentEvent.FormIsVirtualToggled(it)) }
                )
            }

            // Meeting link
            if (formState.isVirtual) {
                OutlinedTextField(
                    value = formState.meetingLink,
                    onValueChange = { viewModel.handleEvent(AppointmentEvent.FormMeetingLinkChanged(it)) },
                    label = { Text("Meeting link") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            // Reminder
            OutlinedTextField(
                value = formState.reminderMinutesBefore.toString(),
                onValueChange = {
                    it.toIntOrNull()?.let { minutes ->
                        viewModel.handleEvent(AppointmentEvent.FormReminderChanged(minutes))
                    }
                },
                label = { Text("Reminder (minutes before)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("15") }
            )

            // Notes
            OutlinedTextField(
                value = formState.notes,
                onValueChange = { viewModel.handleEvent(AppointmentEvent.FormNotesChanged(it)) },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 6
            )

            // Participants
            OutlinedTextField(
                value = formState.participantEmails,
                onValueChange = { viewModel.handleEvent(AppointmentEvent.FormParticipantEmailsChanged(it)) },
                label = { Text("Participant emails (comma separated)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("example@email.com, other@email.com") }
            )

            // Error message
            if (formState.errorMessage != null) {
                Text(
                    text = formState.errorMessage!!,
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (formState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.handleEvent(AppointmentEvent.FormSubmit)
                },
                enabled = formState.isFormValid && !formState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEditMode) "Save Appointment" else "Create Appointment")
            }
        }
    }
