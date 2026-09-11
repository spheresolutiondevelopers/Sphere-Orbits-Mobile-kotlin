package com.orbits.feature.appointments

import com.orbits.domain.appointments.Appointment

/**
 * UI state for the appointments feature.
 */
data class AppointmentsUiState(
    val isLoading: Boolean = true,
    val appointments: List<Appointment> = emptyList(),
    val filteredAppointments: List<Appointment> = emptyList(),
    val selectedFilter: AppointmentFilter = AppointmentFilter.ALL,
    val selectedType: AppointmentTypeFilter = AppointmentTypeFilter.ALL,
    val selectedAppointmentId: String? = null,
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false
)

/**
 * Appointment filter options.
 */
enum class AppointmentFilter(val displayName: String) {
    ALL("All"),
    SCHEDULED("Scheduled"),
    CONFIRMED("Confirmed"),
    CANCELLED("Cancelled"),
    COMPLETED("Completed"),
    RESCHEDULED("Rescheduled"),
    TODAY("Today"),
    UPCOMING("Upcoming")
}

/**
 * Appointment type filter options.
 */
enum class AppointmentTypeFilter(val displayName: String) {
    ALL("All"),
    GENERAL("General"),
    DOCTOR("Doctor"),
    BUSINESS("Business"),
    PERSONAL("Personal"),
    MEETING("Meeting"),
    CONSULTATION("Consultation"),
    INTERVIEW("Interview"),
    DENTIST("Dentist"),
    THERAPY("Therapy"),
    LEGAL("Legal")
}

/**
 * Appointment form UI state.
 */
data class AppointmentFormUiState(
    val id: String? = null,
    val title: String = "",
    val description: String = "",
    val appointmentType: String = "general",
    val startDateTime: String = "",
    val endDateTime: String = "",
    val allDayEvent: Boolean = false,
    val location: String = "",
    val isVirtual: Boolean = false,
    val meetingLink: String = "",
    val meetingPlatform: String? = null,
    val reminderMinutesBefore: Int = 15,
    val isRecurring: Boolean = false,
    val recurrencePattern: String? = null,
    val calendarColor: String = "#7C6CF8",
    val notes: String = "",
    val participantEmails: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val isEditMode: Boolean = false,
    val titleError: String? = null,
    val dateTimeError: String? = null
) {
    val isFormValid: Boolean
        get() = title.isNotBlank() &&
                startDateTime.isNotBlank() &&
                endDateTime.isNotBlank() &&
                titleError == null &&
                dateTimeError == null
}
