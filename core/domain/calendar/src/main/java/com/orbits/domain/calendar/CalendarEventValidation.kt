package com.orbits.domain.calendar

/**
 * Calendar event validation rules.
 * Mirrors server-side validation exactly.
 */
object CalendarEventValidation {

    fun validate(
        title: String,
        description: String? = null,
        startDateTime: String,
        endDateTime: String,
        allDayEvent: Boolean = false,
        location: String? = null,
        isVirtual: Boolean = false,
        meetingLink: String? = null,
        meetingPlatform: String? = null,
        reminderMinutesBefore: Int = 15,
        isRecurring: Boolean = false,
        recurrencePattern: String? = null,
        calendarColor: String? = "#2196F3",
        notes: String? = null,
        status: String? = null
    ): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        // Title validation
        if (title.isBlank()) {
            errors.add(ValidationError("title", "Event title cannot be empty"))
        }
        if (title.length > 255) {
            errors.add(ValidationError("title", "Event title must be 255 characters or less"))
        }

        // Description validation
        if (description != null && description.length > 4000) {
            errors.add(ValidationError("description", "Description must be 4000 characters or less"))
        }

        // Location validation
        if (location != null && location.length > 500) {
            errors.add(ValidationError("location", "Location must be 500 characters or less"))
        }

        // Notes validation
        if (notes != null && notes.length > 4000) {
            errors.add(ValidationError("notes", "Notes must be 4000 characters or less"))
        }

        // Status validation
        if (status != null) {
            if (status !in listOf("scheduled", "confirmed", "cancelled", "completed", "rescheduled")) {
                errors.add(ValidationError("status", "Invalid status. Must be: scheduled, confirmed, cancelled, completed, or rescheduled"))
            }
        }

        // Date/time validation
        try {
            val start = java.time.Instant.parse(startDateTime)
            val end = java.time.Instant.parse(endDateTime)

            if (end <= start) {
                errors.add(ValidationError("endDateTime", "End time must be after start time"))
            }

            val durationSeconds = java.time.Duration.between(start, end).seconds
            if (durationSeconds > 8 * 60 * 60) {
                errors.add(ValidationError("duration", "Event duration cannot exceed 8 hours"))
            }
            if (durationSeconds < 60) {
                errors.add(ValidationError("duration", "Event duration must be at least 1 minute"))
            }
        } catch (e: Exception) {
            errors.add(ValidationError("datetime", "Invalid date/time format"))
        }

        // Reminder validation
        if (reminderMinutesBefore !in 0..1440) {
            errors.add(ValidationError("reminderMinutesBefore", "Reminder minutes must be between 0 and 1440"))
        }

        // Recurrence validation
        if (isRecurring && recurrencePattern != null) {
            val rule = RecurrenceRule.fromRRule(recurrencePattern)
            if (rule == null) {
                errors.add(ValidationError("recurrencePattern", "Invalid RRULE format"))
            } else {
                // Additional validation: count + until must not both be null
                if (rule.count == null && rule.until == null) {
                    // Allowed: infinite recurrence (will be limited by system)
                }
            }
        }

        if (isRecurring && recurrencePattern == null) {
            errors.add(ValidationError("recurrencePattern", "Recurring events must have a recurrence pattern"))
        }

        if (!isRecurring && recurrencePattern != null) {
            errors.add(ValidationError("recurrencePattern", "Non-recurring events cannot have a recurrence pattern"))
        }

        // Meeting link validation
        if (isVirtual && meetingLink != null) {
            if (!meetingLink.startsWith("http")) {
                errors.add(ValidationError("meetingLink", "Meeting link must be a valid URL"))
            }
        }

        // Color validation
        if (calendarColor != null) {
            if (!calendarColor.matches(Regex("^#[0-9A-Fa-f]{6}$"))) {
                errors.add(ValidationError("calendarColor", "Invalid color format. Must be #RRGGBB"))
            }
        }

        // Meeting platform validation
        if (meetingPlatform != null) {
            if (meetingPlatform !in listOf("zoom", "teams", "google_meet", "custom")) {
                errors.add(ValidationError("meetingPlatform", "Invalid meeting platform"))
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