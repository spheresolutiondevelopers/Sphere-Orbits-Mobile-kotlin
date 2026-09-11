package com.orbits.domain.notes

/**
 * Note validation rules.
 * Mirrors server-side validation exactly.
 */
object NoteValidation {

    fun validate(
        content: String,
        title: String? = null,
        contentFormat: String = "markdown",
        tags: List<String> = emptyList(),
        color: String? = null,
        taskId: String? = null,
        eventId: String? = null,
        appointmentId: String? = null,
        meetingId: String? = null,
        reminderAt: String? = null,
        status: String? = null
    ): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        // Content validation
        if (content.isBlank()) {
            errors.add(ValidationError("content", "Note content cannot be empty"))
        }
        if (content.length > 100000) {
            errors.add(ValidationError("content", "Note content must be 100,000 characters or less"))
        }

        // Title validation
        if (title != null) {
            if (title.isBlank()) {
                errors.add(ValidationError("title", "Title cannot be empty if provided"))
            }
            if (title.length > 255) {
                errors.add(ValidationError("title", "Title must be 255 characters or less"))
            }
        }

        // Content format validation
        if (contentFormat !in listOf("markdown", "plain", "html")) {
            errors.add(ValidationError("contentFormat", "Invalid content format"))
        }

        // Tag validation
        tags.forEach { tag ->
            if (tag.length > 50) {
                errors.add(ValidationError("tags", "Tag '$tag' exceeds 50 characters"))
            }
            if (!tag.matches(Regex("^[A-Za-z0-9_\\-]+$"))) {
                errors.add(ValidationError("tags", "Tag '$tag' contains invalid characters"))
            }
        }

        // Color validation
        if (color != null) {
            if (!color.matches(Regex("^#[0-9A-Fa-f]{6}$"))) {
                errors.add(ValidationError("color", "Invalid color format. Must be #RRGGBB"))
            }
        }

        // Entity linking validation (at most one)
        val linkedCount = listOf(
            taskId != null,
            eventId != null,
            appointmentId != null,
            meetingId != null
        ).count { it }

        if (linkedCount > 1) {
            errors.add(
                ValidationError(
                    "entity",
                    "Note can only be linked to one entity at a time"
                )
            )
        }

        // Reminder validation
        if (reminderAt != null) {
            try {
                val reminderTime = java.time.Instant.parse(reminderAt)
                val now = java.time.Instant.now()
                if (reminderTime.isBefore(now)) {
                    errors.add(ValidationError("reminderAt", "Reminder time cannot be in the past"))
                }
            } catch (e: Exception) {
                errors.add(ValidationError("reminderAt", "Invalid date/time format"))
            }
        }

        return ValidationResult(errors.isEmpty(), errors)
    }

    fun validateTag(tag: String): Boolean {
        return tag.isNotBlank() && tag.length <= 50 && tag.matches(Regex("^[A-Za-z0-9_\\-]+$"))
    }

    fun validateColor(color: String): Boolean {
        return color.matches(Regex("^#[0-9A-Fa-f]{6}$"))
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
