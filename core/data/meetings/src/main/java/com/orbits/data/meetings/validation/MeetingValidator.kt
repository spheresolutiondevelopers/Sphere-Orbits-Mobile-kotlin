package com.orbits.data.meetings.validation

import com.orbits.domain.meetings.Meeting

object MeetingValidator {

    fun validate(meeting: Meeting): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        if (meeting.title.isBlank()) {
            errors.add(ValidationError("title", "Meeting title cannot be empty"))
        }

        if (meeting.title.length > 255) {
            errors.add(ValidationError("title", "Meeting title must be 255 characters or less"))
        }

        val description = meeting.description
        if (description != null && description.length > 4000) {
            errors.add(ValidationError("description", "Description must be 4000 characters or less"))
        }

        val agenda = meeting.agenda
        if (agenda != null && agenda.length > 4000) {
            errors.add(ValidationError("agenda", "Agenda must be 4000 characters or less"))
        }

        val minutes = meeting.minutes
        if (minutes != null && minutes.length > 4000) {
            errors.add(ValidationError("minutes", "Minutes must be 4000 characters or less"))
        }

        if (meeting.status !in listOf("scheduled", "live", "ended", "cancelled")) {
            errors.add(ValidationError("status", "Invalid status"))
        }

        val meetingPlatform = meeting.meetingPlatform
        if (meetingPlatform != null) {
            if (meetingPlatform !in listOf("zoom", "teams", "google_meet", "custom")) {
                errors.add(ValidationError("meetingPlatform", "Invalid meeting platform"))
            }
        }

        // Validate start < end
        try {
            val start = java.time.Instant.parse(meeting.startDateTime)
            val end = java.time.Instant.parse(meeting.endDateTime)
            if (end <= start) {
                errors.add(ValidationError("endDateTime", "End time must be after start time"))
            }
            // Check duration (max 8 hours)
            val durationSeconds = java.time.Duration.between(start, end).seconds
            if (durationSeconds > 8 * 60 * 60) {
                errors.add(ValidationError("duration", "Meeting duration cannot exceed 8 hours"))
            }
        } catch (e: Exception) {
            errors.add(ValidationError("datetime", "Invalid date/time format"))
        }

        // Validate recurrence pattern if recurring
        val recurrencePattern = meeting.recurrencePattern
        if (meeting.isRecurring && recurrencePattern != null) {
            if (!recurrencePattern.startsWith("FREQ=")) {
                errors.add(ValidationError("recurrencePattern", "Invalid RRULE format"))
            }
        }

        // Validate meeting link if platform is custom
        val meetingLink = meeting.meetingLink
        if (meetingPlatform == "custom" && meetingLink != null) {
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