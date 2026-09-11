package com.orbits.feature.appointments

import com.orbits.domain.appointments.Appointment

/**
 * UI events for the appointments feature.
 */
sealed class AppointmentEvent {
    // ─── List Events ─────────────────────────────────────────────

    data object LoadAppointments : AppointmentEvent()
    data object Refresh : AppointmentEvent()
    data class SelectFilter(val filter: AppointmentFilter) : AppointmentEvent()
    data class SelectType(val type: AppointmentTypeFilter) : AppointmentEvent()
    data class SelectAppointment(val appointmentId: String) : AppointmentEvent()
    data class NavigateToAppointmentDetail(val appointmentId: String) : AppointmentEvent()
    data object NavigateToCreate : AppointmentEvent()
    data object DismissError : AppointmentEvent()

    // ─── CRUD Events ─────────────────────────────────────────────

    data class CreateAppointment(val appointment: Appointment) : AppointmentEvent()
    data class UpdateAppointment(val appointment: Appointment) : AppointmentEvent()
    data class CancelAppointment(val appointmentId: String) : AppointmentEvent()
    data class DeleteAppointment(val appointmentId: String) : AppointmentEvent()
    data class CompleteAppointment(val appointmentId: String) : AppointmentEvent()

    // ─── Form Events ─────────────────────────────────────────────

    data class FormTitleChanged(val title: String) : AppointmentEvent()
    data class FormDescriptionChanged(val description: String) : AppointmentEvent()
    data class FormTypeChanged(val type: String) : AppointmentEvent()
    data class FormStartDateTimeChanged(val dateTime: String) : AppointmentEvent()
    data class FormEndDateTimeChanged(val dateTime: String) : AppointmentEvent()
    data class FormAllDayEventToggled(val allDay: Boolean) : AppointmentEvent()
    data class FormLocationChanged(val location: String) : AppointmentEvent()
    data class FormIsVirtualToggled(val isVirtual: Boolean) : AppointmentEvent()
    data class FormMeetingLinkChanged(val link: String) : AppointmentEvent()
    data class FormMeetingPlatformChanged(val platform: String?) : AppointmentEvent()
    data class FormReminderChanged(val minutes: Int) : AppointmentEvent()
    data class FormIsRecurringToggled(val isRecurring: Boolean) : AppointmentEvent()
    data class FormRecurrencePatternChanged(val pattern: String?) : AppointmentEvent()
    data class FormCalendarColorChanged(val color: String) : AppointmentEvent()
    data class FormNotesChanged(val notes: String) : AppointmentEvent()
    data class FormParticipantEmailsChanged(val emails: String) : AppointmentEvent()
    data object FormSubmit : AppointmentEvent()
    data object FormCancel : AppointmentEvent()
    data object FormDismissError : AppointmentEvent()

    // ─── Availability ─────────────────────────────────────────────

    data class CheckAvailability(val startDateTime: String, val endDateTime: String) : AppointmentEvent()
}
