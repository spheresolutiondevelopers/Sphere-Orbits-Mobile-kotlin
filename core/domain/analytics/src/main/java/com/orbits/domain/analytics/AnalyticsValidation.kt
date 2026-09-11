package com.orbits.domain.analytics

/**
 * Analytics validation rules.
 * Mirrors server-side validation exactly.
 */
object AnalyticsValidation {

    fun validateEvent(
        userId: String,
        eventType: String,
        eventCategory: String = "user_action",
        eventSubtype: String? = null,
        entityId: String? = null,
        entityType: String? = null,
        value: Double? = null,
        metadata: Map<String, String> = emptyMap(),
        sessionId: String? = null,
        eventTimestamp: String? = null
    ): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        // User ID validation
        if (userId.isBlank()) {
            errors.add(ValidationError("userId", "User ID cannot be empty"))
        }

        // Event type validation
        if (eventType.isBlank()) {
            errors.add(ValidationError("eventType", "Event type cannot be empty"))
        }
        if (eventType.length > 100) {
            errors.add(ValidationError("eventType", "Event type must be 100 characters or less"))
        }

        // Event category validation
        if (eventCategory !in listOf("user_action", "system", "performance")) {
            errors.add(ValidationError("eventCategory", "Invalid event category"))
        }

        // Event subtype validation
        if (eventSubtype != null && eventSubtype.length > 50) {
            errors.add(ValidationError("eventSubtype", "Event subtype must be 50 characters or less"))
        }

        // Entity type validation
        if (entityType != null) {
            if (entityType !in listOf("task", "appointment", "meeting", "note", "user", "system")) {
                errors.add(ValidationError("entityType", "Invalid entity type"))
            }
        }

        // Entity ID validation
        if (entityId != null && entityId.isBlank()) {
            errors.add(ValidationError("entityId", "Entity ID cannot be empty if provided"))
        }

        // If entity ID is provided, entity type must also be provided
        if (entityId != null && entityType == null) {
            errors.add(ValidationError("entityType", "Entity type is required when entity ID is provided"))
        }

        // Value validation
        if (value != null && value < 0) {
            errors.add(ValidationError("value", "Value cannot be negative"))
        }

        // Metadata validation
        metadata.forEach { (key, value) ->
            if (key.length > 50) {
                errors.add(ValidationError("metadata", "Metadata key '$key' exceeds 50 characters"))
            }
            if (value.length > 500) {
                errors.add(ValidationError("metadata", "Metadata value for '$key' exceeds 500 characters"))
            }
        }

        // Session ID validation
        if (sessionId != null && sessionId.length > 100) {
            errors.add(ValidationError("sessionId", "Session ID must be 100 characters or less"))
        }

        // Timestamp validation
        if (eventTimestamp != null) {
            try {
                java.time.Instant.parse(eventTimestamp)
            } catch (e: Exception) {
                errors.add(ValidationError("eventTimestamp", "Invalid timestamp format"))
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

    fun validateDateRange(startDate: String, endDate: String): Boolean {
        return try {
            val start = java.time.LocalDate.parse(startDate)
            val end = java.time.LocalDate.parse(endDate)
            start <= end
        } catch (e: Exception) {
            false
        }
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
