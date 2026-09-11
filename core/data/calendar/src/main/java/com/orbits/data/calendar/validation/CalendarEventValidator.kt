package com.orbits.data.calendar.validation

import com.orbits.domain.calendar.CalendarEvent

object CalendarEventValidator {

    fun validate(event: CalendarEvent): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        if (event.title.isBlank()) {
            errors.add(ValidationError("title", "Event title cannot be empty"))
        }

        if (event.title.length > 255) {
            errors.add(ValidationError("title", "Event title must be 255 characters or less"))
        }

        val description = event.description
        if (description != null && description.length > 4000) {
            errors.add(ValidationError("description", "Description must be 4000 characters or less"))
        }

        val location = event.location
        if (location != null && location.length > 500) {
            errors.add(ValidationError("location", "Location must be 500 characters or less"))
        }

        if (event.status !in listOf("scheduled", "confirmed", "cancelled", "completed", "rescheduled")) {
            errors.add(ValidationError("status", "Invalid status"))
        }

        if (event.reminderMinutesBefore !in 0..1440) {
            errors.add(ValidationError("reminderMinutesBefore", "Reminder minutes must be 0-1440"))
        }

        // Validate start < end
        try {
            val start = java.time.Instant.parse(event.startDateTime)
            val end = java.time.Instant.parse(event.endDateTime)
            if (end <= start) {
                errors.add(ValidationError("endDateTime", "End time must be after start time"))
            }
        } catch (e: Exception) {
            errors.add(ValidationError("datetime", "Invalid date/time format"))
        }

        // Validate recurrence pattern if recurring
        val recurrencePattern = event.recurrencePattern
        if (event.isRecurring && recurrencePattern != null) {
            // Basic RRULE validation
            if (!recurrencePattern.startsWith("FREQ=")) {
                errors.add(ValidationError("recurrencePattern", "Invalid RRULE format"))
            }
        }

        // Validate meeting link if virtual
        val meetingLink = event.meetingLink
        if (event.isVirtual && meetingLink != null) {
            if (!meetingLink.startsWith("http")) {
                errors.add(ValidationError("meetingLink", "Meeting link must be a valid URL"))
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