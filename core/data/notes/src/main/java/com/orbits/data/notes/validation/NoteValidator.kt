package com.orbits.data.notes.validation

import com.orbits.domain.notes.Note

object NoteValidator {

    fun validate(note: Note): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        if (note.content.isBlank()) {
            errors.add(ValidationError("content", "Note content cannot be empty"))
        }

        if (note.content.length > 100000) {
            errors.add(ValidationError("content", "Note content must be 100,000 characters or less"))
        }

        if (note.contentFormat !in listOf("markdown", "plain", "html")) {
            errors.add(ValidationError("contentFormat", "Invalid content format"))
        }

        val title = note.title
        if (title != null) {
            if (title.isBlank()) {
                errors.add(ValidationError("title", "Title cannot be empty if provided"))
            }
            if (title.length > 255) {
                errors.add(ValidationError("title", "Title must be 255 characters or less"))
            }
        }

        val color = note.color
        if (color != null) {
            if (!color.matches(Regex("^#[0-9A-Fa-f]{6}$"))) {
                errors.add(ValidationError("color", "Invalid color format. Must be #RRGGBB"))
            }
        }

        // Validate tags
        note.tags.forEach { tag ->
            if (tag.length > 50) {
                errors.add(ValidationError("tags", "Tag '$tag' exceeds 50 characters"))
            }
            if (!tag.matches(Regex("^[A-Za-z0-9_\\-]+$"))) {
                errors.add(ValidationError("tags", "Tag '$tag' contains invalid characters"))
            }
        }

        // Ensure at most one linked entity
        val linkedCount = listOf(
            note.taskId != null,
            note.eventId != null,
            note.appointmentId != null,
            note.meetingId != null
        ).count { it }

        if (linkedCount > 1) {
            errors.add(ValidationError("entity", "Note can only be linked to one entity at a time"))
        }

        // Validate reminder if set
        if (note.reminderAt != null) {
            try {
                val reminderTime = java.time.Instant.parse(note.reminderAt)
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
}

data class ValidationError(
    val field: String,
    val message: String
)

data class ValidationResult(
    val isValid: Boolean,
    val errors: List<ValidationError>
)