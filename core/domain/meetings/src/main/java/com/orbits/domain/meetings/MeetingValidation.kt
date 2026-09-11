package com.orbits.domain.meetings

/**
 * Meeting validation rules.
 * Mirrors server-side validation exactly.
 */
object MeetingValidation {

    fun validate(
        taskId: String,
        title: String,
        description: String? = null,
        startDateTime: String,
        endDateTime: String,
        meetingPlatform: String? = null,
        agenda: String? = null,
        isRecurring: Boolean = false,
        recurrencePattern: String? = null,
        status: String? = null
    ): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        // Task ID validation
        if (taskId.isBlank()) {
            errors.add(ValidationError("taskId", "Task ID cannot be empty"))
        }

        // Title validation
        if (title.isBlank()) {
            errors.add(ValidationError("title", "Meeting title cannot be empty"))
        }
        if (title.length > 255) {
            errors.add(ValidationError("title", "Meeting title must be 255 characters or less"))
        }

        // Description validation
        if (description != null && description.length > 4000) {
            errors.add(ValidationError("description", "Description must be 4000 characters or less"))
        }

        // Agenda validation
        if (agenda != null && agenda.length > 4000) {
            errors.add(ValidationError("agenda", "Agenda must be 4000 characters or less"))
        }

        // Status validation
        if (status != null) {
            if (status !in listOf("scheduled", "live", "ended", "cancelled")) {
                errors.add(ValidationError("status", "Invalid status. Must be: scheduled, live, ended, or cancelled"))
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
                errors.add(ValidationError("duration", "Meeting duration cannot exceed 8 hours"))
            }
            if (durationSeconds < 60) {
                errors.add(ValidationError("duration", "Meeting duration must be at least 1 minute"))
            }
        } catch (e: Exception) {
            errors.add(ValidationError("datetime", "Invalid date/time format"))
        }

        // Meeting platform validation
        if (meetingPlatform != null) {
            if (meetingPlatform !in listOf("zoom", "teams", "google_meet", "custom")) {
                errors.add(ValidationError("meetingPlatform", "Invalid meeting platform"))
            }
        }

        // Recurrence validation
        if (isRecurring && recurrencePattern != null) {
            if (!recurrencePattern.startsWith("FREQ=")) {
                errors.add(ValidationError("recurrencePattern", "Invalid RRULE format"))
            }
        }
        if (isRecurring && recurrencePattern == null) {
            errors.add(ValidationError("recurrencePattern", "Recurring meetings must have a recurrence pattern"))
        }
        if (!isRecurring && recurrencePattern != null) {
            errors.add(ValidationError("recurrencePattern", "Non-recurring meetings cannot have a recurrence pattern"))
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
