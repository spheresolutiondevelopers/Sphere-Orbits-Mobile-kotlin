package com.orbits.data.events.validation

import com.orbits.domain.events.Event

object EventValidator {

    fun validate(event: Event): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        if (event.name.isBlank()) {
            errors.add(ValidationError("name", "Event name cannot be empty"))
        }

        if (event.name.length > 255) {
            errors.add(ValidationError("name", "Event name must be 255 characters or less"))
        }

        if (event.description != null && event.description.length > 4000) {
            errors.add(ValidationError("description", "Description must be 4000 characters or less"))
        }

        if (event.format != null && event.format !in listOf("In-Person", "Virtual", "Hybrid")) {
            errors.add(ValidationError("format", "Invalid format. Must be: In-Person, Virtual, or Hybrid"))
        }

        if (event.status !in listOf("planned", "ongoing", "completed", "cancelled")) {
            errors.add(ValidationError("status", "Invalid status"))
        }

        if (event.currency.isBlank()) {
            errors.add(ValidationError("currency", "Currency cannot be empty"))
        }

        if (event.budget != null && event.budget < 0) {
            errors.add(ValidationError("budget", "Budget cannot be negative"))
        }

        if (event.maxAttendees != null && event.maxAttendees < 0) {
            errors.add(ValidationError("maxAttendees", "Max attendees cannot be negative"))
        }

        if (event.startDateTime != null && event.endDateTime != null) {
            try {
                val start = java.time.Instant.parse(event.startDateTime)
                val end = java.time.Instant.parse(event.endDateTime)
                if (end <= start) {
                    errors.add(ValidationError("endDateTime", "End time must be after start time"))
                }
            } catch (e: Exception) {
                errors.add(ValidationError("datetime", "Invalid date/time format"))
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