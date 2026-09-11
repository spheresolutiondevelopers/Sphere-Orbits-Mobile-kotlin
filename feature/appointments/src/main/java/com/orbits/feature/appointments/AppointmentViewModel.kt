package com.orbits.feature.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbits.core.common.Result
import com.orbits.core.common.extensions.nowUtc
import com.orbits.domain.appointments.Appointment
import com.orbits.domain.appointments.AppointmentRepository
import com.orbits.domain.appointments.AppointmentParticipant
import com.orbits.domain.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AppointmentViewModel @Inject constructor(
    private val appointmentRepository: AppointmentRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AppointmentsUiState())
    val state: StateFlow<AppointmentsUiState> = _state.asStateFlow()

    private val _formState = MutableStateFlow(AppointmentFormUiState())
    val formState: StateFlow<AppointmentFormUiState> = _formState.asStateFlow()

    private var allAppointments: List<Appointment> = emptyList()
    private var currentFilter = AppointmentFilter.ALL
    private var currentType = AppointmentTypeFilter.ALL

    init {
        loadAppointments()
    }

    fun handleEvent(event: AppointmentEvent) {
        when (event) {
            AppointmentEvent.LoadAppointments -> loadAppointments()
            AppointmentEvent.Refresh -> refresh()
            is AppointmentEvent.SelectFilter -> applyFilter(event.filter)
            is AppointmentEvent.SelectType -> applyType(event.type)
            is AppointmentEvent.SelectAppointment -> selectAppointment(event.appointmentId)
            is AppointmentEvent.NavigateToAppointmentDetail -> { /* Navigation handled by NavGraph */ }
            AppointmentEvent.NavigateToCreate -> navigateToCreate()
            AppointmentEvent.DismissError -> dismissError()
            is AppointmentEvent.CreateAppointment -> createAppointment(event.appointment)
            is AppointmentEvent.UpdateAppointment -> updateAppointment(event.appointment)
            is AppointmentEvent.CancelAppointment -> cancelAppointment(event.appointmentId)
            is AppointmentEvent.DeleteAppointment -> deleteAppointment(event.appointmentId)
            is AppointmentEvent.CompleteAppointment -> completeAppointment(event.appointmentId)
            // Form events
            is AppointmentEvent.FormTitleChanged -> updateFormTitle(event.title)
            is AppointmentEvent.FormDescriptionChanged -> updateFormDescription(event.description)
            is AppointmentEvent.FormTypeChanged -> updateFormType(event.type)
            is AppointmentEvent.FormStartDateTimeChanged -> updateFormStartDateTime(event.dateTime)
            is AppointmentEvent.FormEndDateTimeChanged -> updateFormEndDateTime(event.dateTime)
            is AppointmentEvent.FormAllDayEventToggled -> updateFormAllDayEvent(event.allDay)
            is AppointmentEvent.FormLocationChanged -> updateFormLocation(event.location)
            is AppointmentEvent.FormIsVirtualToggled -> updateFormIsVirtual(event.isVirtual)
            is AppointmentEvent.FormMeetingLinkChanged -> updateFormMeetingLink(event.link)
            is AppointmentEvent.FormMeetingPlatformChanged -> updateFormMeetingPlatform(event.platform)
            is AppointmentEvent.FormReminderChanged -> updateFormReminder(event.minutes)
            is AppointmentEvent.FormIsRecurringToggled -> updateFormIsRecurring(event.isRecurring)
            is AppointmentEvent.FormRecurrencePatternChanged -> updateFormRecurrencePattern(event.pattern)
            is AppointmentEvent.FormCalendarColorChanged -> updateFormCalendarColor(event.color)
            is AppointmentEvent.FormNotesChanged -> updateFormNotes(event.notes)
            is AppointmentEvent.FormParticipantEmailsChanged -> updateFormParticipantEmails(event.emails)
            AppointmentEvent.FormSubmit -> submitForm()
            AppointmentEvent.FormCancel -> cancelForm()
            AppointmentEvent.FormDismissError -> dismissFormError()
            is AppointmentEvent.CheckAvailability -> checkAvailability(event.startDateTime, event.endDateTime)
        }
    }

    // ─── Load Operations ─────────────────────────────────────────

    private fun loadAppointments() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                appointmentRepository.getAppointments().collect { appointments ->
                    allAppointments = appointments
                    applyFiltersToState()
                    _state.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load appointments"
                    )
                }
            }
        }
    }

    private fun refresh() {
        _state.update { it.copy(isRefreshing = true) }
        loadAppointments()
        _state.update { it.copy(isRefreshing = false) }
    }

    // ─── Filter & Type ────────────────────────────────────────────

    private fun applyFilter(filter: AppointmentFilter) {
        currentFilter = filter
        _state.update { it.copy(selectedFilter = filter) }
        applyFiltersToState()
    }

    private fun applyType(type: AppointmentTypeFilter) {
        currentType = type
        _state.update { it.copy(selectedType = type) }
        applyFiltersToState()
    }

    private fun applyFiltersToState() {
        val filtered = allAppointments
            .filter { appointment ->
                when (currentFilter) {
                    AppointmentFilter.ALL -> true
                    AppointmentFilter.SCHEDULED -> appointment.status == "scheduled"
                    AppointmentFilter.CONFIRMED -> appointment.status == "confirmed"
                    AppointmentFilter.CANCELLED -> appointment.status == "cancelled"
                    AppointmentFilter.COMPLETED -> appointment.status == "completed"
                    AppointmentFilter.RESCHEDULED -> appointment.status == "rescheduled"
                    AppointmentFilter.TODAY -> appointment.startDateTime.startsWith(LocalDate.now().toString())
                    AppointmentFilter.UPCOMING -> appointment.isFuture() && appointment.status != "cancelled"
                }
            }
            .filter { appointment ->
                when (currentType) {
                    AppointmentTypeFilter.ALL -> true
                    AppointmentTypeFilter.GENERAL -> appointment.appointmentType == "general"
                    AppointmentTypeFilter.DOCTOR -> appointment.appointmentType == "doctor"
                    AppointmentTypeFilter.BUSINESS -> appointment.appointmentType == "business"
                    AppointmentTypeFilter.PERSONAL -> appointment.appointmentType == "personal"
                    AppointmentTypeFilter.MEETING -> appointment.appointmentType == "meeting"
                    AppointmentTypeFilter.CONSULTATION -> appointment.appointmentType == "consultation"
                    AppointmentTypeFilter.INTERVIEW -> appointment.appointmentType == "interview"
                    AppointmentTypeFilter.DENTIST -> appointment.appointmentType == "dentist"
                    AppointmentTypeFilter.THERAPY -> appointment.appointmentType == "therapy"
                    AppointmentTypeFilter.LEGAL -> appointment.appointmentType == "legal"
                }
            }

        _state.update { it.copy(filteredAppointments = filtered) }
    }

    private fun selectAppointment(appointmentId: String) {
        _state.update { it.copy(selectedAppointmentId = appointmentId) }
    }

    private fun dismissError() {
        _state.update { it.copy(errorMessage = null) }
    }

    // ─── Navigation ──────────────────────────────────────────────

    private fun navigateToCreate() {
        _formState.value = AppointmentFormUiState()
    }

    private fun cancelForm() {
        _formState.value = AppointmentFormUiState()
    }

    // ─── CRUD Operations ──────────────────────────────────────────

    private fun createAppointment(appointment: Appointment) {
        _state.update { it.copy(errorMessage = null) }

        viewModelScope.launch {
            val result = appointmentRepository.createAppointment(appointment)
            when (result) {
                is Result.Success -> {
                    loadAppointments()
                    _formState.value = AppointmentFormUiState(isSuccess = true)
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Failed to create appointment"
                        )
                    }
                    _formState.update { it.copy(errorMessage = result.exception.message) }
                }
                Result.Loading -> { /* Handled by form state */ }
            }
        }
    }

    private fun updateAppointment(appointment: Appointment) {
        _state.update { it.copy(errorMessage = null) }

        viewModelScope.launch {
            val result = appointmentRepository.updateAppointment(appointment)
            when (result) {
                is Result.Success -> {
                    loadAppointments()
                    _formState.value = AppointmentFormUiState(isSuccess = true)
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Failed to update appointment"
                        )
                    }
                    _formState.update { it.copy(errorMessage = result.exception.message) }
                }
                Result.Loading -> { /* Handled by form state */ }
            }
        }
    }

    private fun cancelAppointment(appointmentId: String) {
        viewModelScope.launch {
            val result = appointmentRepository.cancelAppointment(appointmentId)
            when (result) {
                is Result.Success -> loadAppointments()
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Failed to cancel appointment"
                        )
                    }
                }
                Result.Loading -> { /* Ignore */ }
            }
        }
    }

    private fun deleteAppointment(appointmentId: String) {
        viewModelScope.launch {
            val result = appointmentRepository.deleteAppointment(appointmentId)
            when (result) {
                is Result.Success -> loadAppointments()
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Failed to delete appointment"
                        )
                    }
                }
                Result.Loading -> { /* Ignore */ }
            }
        }
    }

    private fun completeAppointment(appointmentId: String) {
        viewModelScope.launch {
            val result = appointmentRepository.updateAppointmentStatus(appointmentId, "completed")
            when (result) {
                is Result.Success -> loadAppointments()
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Failed to complete appointment"
                        )
                    }
                }
                Result.Loading -> { /* Ignore */ }
            }
        }
    }

    // ─── Form Operations ──────────────────────────────────────────

    private fun updateFormTitle(title: String) {
        _formState.update { state ->
            state.copy(
                title = title,
                titleError = if (title.isNotBlank() && title.length > 255) {
                    "Title must be 255 characters or less"
                } else null
            )
        }
    }

    private fun updateFormDescription(description: String) {
        _formState.update { it.copy(description = description) }
    }

    private fun updateFormType(type: String) {
        _formState.update { it.copy(appointmentType = type) }
    }

    private fun updateFormStartDateTime(dateTime: String) {
        _formState.update { state ->
            state.copy(
                startDateTime = dateTime,
                dateTimeError = if (state.endDateTime.isNotBlank() && dateTime >= state.endDateTime) {
                    "Start time must be before end time"
                } else null
            )
        }
    }

    private fun updateFormEndDateTime(dateTime: String) {
        _formState.update { state ->
            state.copy(
                endDateTime = dateTime,
                dateTimeError = if (state.startDateTime.isNotBlank() && state.startDateTime >= dateTime) {
                    "End time must be after start time"
                } else null
            )
        }
    }

    private fun updateFormAllDayEvent(allDay: Boolean) {
        _formState.update { it.copy(allDayEvent = allDay) }
    }

    private fun updateFormLocation(location: String) {
        _formState.update { it.copy(location = location) }
    }

    private fun updateFormIsVirtual(isVirtual: Boolean) {
        _formState.update { it.copy(isVirtual = isVirtual) }
    }

    private fun updateFormMeetingLink(link: String) {
        _formState.update { it.copy(meetingLink = link) }
    }

    private fun updateFormMeetingPlatform(platform: String?) {
        _formState.update { it.copy(meetingPlatform = platform) }
    }

    private fun updateFormReminder(minutes: Int) {
        _formState.update { it.copy(reminderMinutesBefore = minutes) }
    }

    private fun updateFormIsRecurring(isRecurring: Boolean) {
        _formState.update { it.copy(isRecurring = isRecurring) }
    }

    private fun updateFormRecurrencePattern(pattern: String?) {
        _formState.update { it.copy(recurrencePattern = pattern) }
    }

    private fun updateFormCalendarColor(color: String) {
        _formState.update { it.copy(calendarColor = color) }
    }

    private fun updateFormNotes(notes: String) {
        _formState.update { it.copy(notes = notes) }
    }

    private fun updateFormParticipantEmails(emails: String) {
        _formState.update { it.copy(participantEmails = emails) }
    }

    private fun submitForm() {
        val state = _formState.value

        if (!state.isFormValid) {
            _formState.update { it.copy(errorMessage = "Please fix the errors above") }
            return
        }

        _formState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            val userId = currentUser?.id ?: "unknown"

            val appointment = Appointment(
                id = state.id ?: UUID.randomUUID().toString(),
                userId = userId,
                title = state.title,
                description = state.description.takeIf { it.isNotBlank() },
                appointmentType = state.appointmentType,
                startDateTime = state.startDateTime,
                endDateTime = state.endDateTime,
                allDayEvent = state.allDayEvent,
                location = state.location.takeIf { it.isNotBlank() },
                isVirtual = state.isVirtual,
                meetingLink = state.meetingLink.takeIf { it.isNotBlank() },
                meetingPlatform = state.meetingPlatform,
                status = "scheduled",
                reminderMinutesBefore = state.reminderMinutesBefore,
                isRecurring = state.isRecurring,
                recurrencePattern = state.recurrencePattern,
                calendarColor = state.calendarColor,
                notes = state.notes.takeIf { it.isNotBlank() },
                externalEventId = null,
                externalSyncStatus = "not_synced",
                isDeleted = false,
                createdAt = nowUtc(),
                updatedAt = nowUtc()
            )

            if (state.isEditMode) {
                handleEvent(AppointmentEvent.UpdateAppointment(appointment))
            } else {
                handleEvent(AppointmentEvent.CreateAppointment(appointment))
            }
        }
    }

    private fun dismissFormError() {
        _formState.update { it.copy(errorMessage = null) }
    }

    private fun checkAvailability(startDateTime: String, endDateTime: String) {
        viewModelScope.launch {
            val result = appointmentRepository.checkAvailability(startDateTime, endDateTime)
            when (result) {
                is Result.Success -> {
                    if (!result.data) {
                        _formState.update {
                            it.copy(
                                errorMessage = "This time slot is not available. Please choose another time."
                            )
                        }
                    }
                }
                is Result.Error -> {
                    _formState.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Failed to check availability"
                        )
                    }
                }
                Result.Loading -> { /* Ignore */ }
            }
        }
    }
}
