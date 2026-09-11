package com.orbits.domain.events

/**
 * Event validation rules.
 * Mirrors server-side validation exactly.
 */
object EventValidation {

    fun validate(
        name: String,
        description: String? = null,
        format: String? = null,
        startDateTime: String? = null,
        endDateTime: String? = null,
        location: String? = null,
        status: String = "planned",
        isRecurring: Boolean = false,
        recurrencePattern: String? = null,
        budget: Double? = null,
        currency: String = "USD",
        maxAttendees: Int? = null,
        isPublic: Boolean = false
    ): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        // Name validation
        if (name.isBlank()) {
            errors.add(ValidationError("name", "Event name cannot be empty"))
        }
        if (name.length > 255) {
            errors.add(ValidationError("name", "Event name must be 255 characters or less"))
        }

        // Description validation
        if (description != null && description.length > 4000) {
            errors.add(ValidationError("description", "Description must be 4000 characters or less"))
        }

        // Format validation
        if (format != null) {
            if (format !in listOf("In-Person", "Virtual", "Hybrid")) {
                errors.add(ValidationError("format", "Invalid format. Must be: In-Person, Virtual, or Hybrid"))
            }
        }

        // Location validation
        if (location != null && location.length > 500) {
            errors.add(ValidationError("location", "Location must be 500 characters or less"))
        }

        // Status validation
        if (status !in listOf("planned", "ongoing", "completed", "cancelled")) {
            errors.add(ValidationError("status", "Invalid status. Must be: planned, ongoing, completed, or cancelled"))
        }

        // Currency validation
        if (currency.isBlank()) {
            errors.add(ValidationError("currency", "Currency cannot be empty"))
        }
        if (currency.length > 3) {
            errors.add(ValidationError("currency", "Currency must be a 3-letter ISO code"))
        }

        // Budget validation
        if (budget != null && budget < 0) {
            errors.add(ValidationError("budget", "Budget cannot be negative"))
        }

        // Max attendees validation
        if (maxAttendees != null && maxAttendees < 0) {
            errors.add(ValidationError("maxAttendees", "Max attendees cannot be negative"))
        }

        // Date/time validation
        if (startDateTime != null && endDateTime != null) {
            try {
                val start = java.time.Instant.parse(startDateTime)
                val end = java.time.Instant.parse(endDateTime)
                if (end <= start) {
                    errors.add(ValidationError("endDateTime", "End time must be after start time"))
                }
            } catch (e: Exception) {
                errors.add(ValidationError("datetime", "Invalid date/time format"))
            }
        }

        // Recurrence validation
        if (isRecurring && recurrencePattern != null) {
            // Simple RRULE validation
            if (!recurrencePattern.startsWith("FREQ=")) {
                errors.add(ValidationError("recurrencePattern", "Invalid RRULE format"))
            }
        }
        if (isRecurring && recurrencePattern == null) {
            errors.add(ValidationError("recurrencePattern", "Recurring events must have a recurrence pattern"))
        }
        if (!isRecurring && recurrencePattern != null) {
            errors.add(ValidationError("recurrencePattern", "Non-recurring events cannot have a recurrence pattern"))
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
