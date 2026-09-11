package com.orbits.data.appointments.validation

import com.orbits.domain.appointments.Appointment

object AppointmentValidator {

    fun validate(appointment: Appointment): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        if (appointment.title.isBlank()) {
            errors.add(ValidationError("title", "Appointment title cannot be empty"))
        }

        if (appointment.title.length > 255) {
            errors.add(ValidationError("title", "Appointment title must be 255 characters or less"))
        }

        val description = appointment.description
        if (description != null && description.length > 4000) {
            errors.add(ValidationError("description", "Description must be 4000 characters or less"))
        }

        if (appointment.appointmentType !in listOf("general", "doctor", "business", "personal", "meeting")) {
            errors.add(ValidationError("appointmentType", "Invalid appointment type"))
        }

        if (appointment.status !in listOf("scheduled", "confirmed", "cancelled", "completed", "rescheduled")) {
            errors.add(ValidationError("status", "Invalid status"))
        }

        if (appointment.reminderMinutesBefore !in 0..1440) {
            errors.add(ValidationError("reminderMinutesBefore", "Reminder minutes must be 0-1440"))
        }

        val location = appointment.location
        if (location != null && location.length > 500) {
            errors.add(ValidationError("location", "Location must be 500 characters or less"))
        }

        val notes = appointment.notes
        if (notes != null && notes.length > 4000) {
            errors.add(ValidationError("notes", "Notes must be 4000 characters or less"))
        }

        // Validate start < end
        try {
            val start = java.time.Instant.parse(appointment.startDateTime)
            val end = java.time.Instant.parse(appointment.endDateTime)
            if (end <= start) {
                errors.add(ValidationError("endDateTime", "End time must be after start time"))
            }
            // Check duration (max 8 hours for appointments)
            val durationSeconds = java.time.Duration.between(start, end).seconds
            if (durationSeconds > 8 * 60 * 60) {
                errors.add(ValidationError("duration", "Appointment duration cannot exceed 8 hours"))
            }
            if (durationSeconds < 60) {
                errors.add(ValidationError("duration", "Appointment duration must be at least 1 minute"))
            }
        } catch (e: Exception) {
            errors.add(ValidationError("datetime", "Invalid date/time format"))
        }

        // Validate recurrence pattern if recurring
        val recurrencePattern = appointment.recurrencePattern
        if (appointment.isRecurring && recurrencePattern != null) {
            if (!recurrencePattern.startsWith("FREQ=")) {
                errors.add(ValidationError("recurrencePattern", "Invalid RRULE format"))
            }
        }

        // Validate meeting link if virtual
        val meetingLink = appointment.meetingLink
        if (appointment.isVirtual && meetingLink != null) {
            if (!meetingLink.startsWith("http")) {
                errors.add(ValidationError("meetingLink", "Meeting link must be a valid URL"))
            }
        }

        // Validate calendar color
        val calendarColor = appointment.calendarColor
        if (calendarColor != null) {
            if (!calendarColor.matches(Regex("^#[0-9A-Fa-f]{6}$"))) {
                errors.add(ValidationError("calendarColor", "Invalid color format. Must be #RRGGBB"))
            }
        }

        return ValidationResult(errors.isEmpty(), errors)
    }
}

data class ValidationError(
    val field: String,
    val message: String
)

data class ValidationResult(
    val isValid: Boolean,
    val errors: List<ValidationError>
)