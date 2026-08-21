package com.orbits.domain.tasks

/**
 * Task validation rules.
 * Mirrors server-side validation exactly.
 */
object TaskValidation {

    fun validate(
        title: String,
        description: String? = null,
        taskType: String = "general",
        priorityLevel: String = "medium",
        dueDate: String? = null,
        dueTime: String? = null,
        startDate: String? = null,
        startTime: String? = null,
        endDate: String? = null,
        endTime: String? = null,
        locationName: String? = null,
        locationAddress: String? = null,
        tags: List<String> = emptyList(),
        notes: String? = null,
        categoryId: String? = null
    ): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        // Title validation
        if (title.isBlank()) {
            errors.add(ValidationError("title", "Task title cannot be empty"))
        }
        if (title.length > 255) {
            errors.add(ValidationError("title", "Task title must be 255 characters or less"))
        }

        // Description validation
        if (description != null && description.length > 4000) {
            errors.add(ValidationError("description", "Description must be 4000 characters or less"))
        }

        // Task type validation
        if (taskType !in listOf("general", "meeting", "reminder", "deadline", "event")) {
            errors.add(ValidationError("taskType", "Invalid task type. Must be: general, meeting, reminder, deadline, or event"))
        }

        // Priority validation
        if (priorityLevel !in listOf("low", "medium", "high", "critical")) {
            errors.add(ValidationError("priorityLevel", "Invalid priority level. Must be: low, medium, high, or critical"))
        }

        // Date/time validation
        if (dueDate != null && !isValidDate(dueDate)) {
            errors.add(ValidationError("dueDate", "Invalid due date format. Must be YYYY-MM-DD"))
        }
        if (startDate != null && !isValidDate(startDate)) {
            errors.add(ValidationError("startDate", "Invalid start date format. Must be YYYY-MM-DD"))
        }
        if (endDate != null && !isValidDate(endDate)) {
            errors.add(ValidationError("endDate", "Invalid end date format. Must be YYYY-MM-DD"))
        }

        if (dueTime != null && !isValidTime(dueTime)) {
            errors.add(ValidationError("dueTime", "Invalid due time format. Must be HH:MM"))
        }
        if (startTime != null && !isValidTime(startTime)) {
            errors.add(ValidationError("startTime", "Invalid start time format. Must be HH:MM"))
        }
        if (endTime != null && !isValidTime(endTime)) {
            errors.add(ValidationError("endTime", "Invalid end time format. Must be HH:MM"))
        }

        // Location validation
        if (locationName != null && locationName.length > 255) {
            errors.add(ValidationError("locationName", "Location name must be 255 characters or less"))
        }
        if (locationAddress != null && locationAddress.length > 500) {
            errors.add(ValidationError("locationAddress", "Location address must be 500 characters or less"))
        }

        // Notes validation
        if (notes != null && notes.length > 2000) {
            errors.add(ValidationError("notes", "Notes must be 2000 characters or less"))
        }

        // Tags validation
        tags.forEach { tag ->
            if (tag.length > 50) {
                errors.add(ValidationError("tags", "Tag '$tag' exceeds 50 characters"))
            }
            if (!tag.matches(Regex("^[A-Za-z0-9_\\-]+$"))) {
                errors.add(ValidationError("tags", "Tag '$tag' contains invalid characters"))
            }
        }

        // Date range validation
        if (startDate != null && endDate != null && startDate > endDate) {
            errors.add(ValidationError("dateRange", "Start date must be before or equal to end date"))
        }

        // Category ID validation (just check it's not empty if provided)
        if (categoryId != null && categoryId.isBlank()) {
            errors.add(ValidationError("categoryId", "Category ID cannot be empty if provided"))
        }

        return ValidationResult(errors.isEmpty(), errors)
    }

    private fun isValidDate(date: String): Boolean {
        return try {
            java.time.LocalDate.parse(date)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun isValidTime(time: String): Boolean {
        return try {
            java.time.LocalTime.parse(time)
            true
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