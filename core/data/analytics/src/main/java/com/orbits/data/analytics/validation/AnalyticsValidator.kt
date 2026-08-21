package com.orbits.data.analytics.validation

import com.orbits.domain.analytics.AnalyticsEvent

object AnalyticsValidator {

    fun validate(event: AnalyticsEvent): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        if (event.eventType.isBlank()) {
            errors.add(ValidationError("eventType", "Event type cannot be empty"))
        }

        if (event.eventType.length > 100) {
            errors.add(ValidationError("eventType", "Event type must be 100 characters or less"))
        }

        if (event.eventCategory !in listOf("user_action", "system", "performance")) {
            errors.add(ValidationError("eventCategory", "Invalid event category"))
        }

        if (event.eventTimestamp.isBlank()) {
            errors.add(ValidationError("eventTimestamp", "Event timestamp cannot be empty"))
        } else {
            try {
                java.time.Instant.parse(event.eventTimestamp)
            } catch (e: Exception) {
                errors.add(ValidationError("eventTimestamp", "Invalid timestamp format"))
            }
        }

        if (event.metadata.isNotEmpty()) {
            event.metadata.forEach { (key, value) ->
                if (key.length > 50) {
                    errors.add(ValidationError("metadata", "Metadata key '$key' exceeds 50 characters"))
                }
                if (value.length > 500) {
                    errors.add(ValidationError("metadata", "Metadata value for '$key' exceeds 500 characters"))
                }
            }
        }

        return ValidationResult(errors.isEmpty(), errors)
    }

    fun validateEventType(eventType: String): Boolean {
        val validEventTypes = setOf(
            "task_created", "task_completed", "task_deleted", "task_updated",
            "appointment_created", "appointment_completed", "appointment_cancelled",
            "meeting_joined", "meeting_created", "meeting_ended",
            "note_created", "note_updated", "note_deleted",
            "focus_time", "break_time",
            "user_login", "user_logout", "user_registered",
            "app_launch", "app_foreground", "app_background",
            "sync_started", "sync_completed", "sync_failed"
        )
        return eventType in validEventTypes || eventType.startsWith("custom_")
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