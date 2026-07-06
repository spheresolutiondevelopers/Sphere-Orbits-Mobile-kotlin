/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.data.tasks.validation

import com.orbits.domain.tasks.Task

/**
 * Validation rules for tasks.
 * Mirrors server-side validation exactly.
 */
object TaskValidator {

    fun validate(task: Task): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        if (task.title.isBlank()) {
            errors.add(ValidationError("title", "Task title cannot be empty"))
        }

        if (task.title.length > 255) {
            errors.add(ValidationError("title", "Task title must be 255 characters or less"))
        }

        if (task.description != null && task.description.length > 4000) {
            errors.add(ValidationError("description", "Description must be 4000 characters or less"))
        }

        if (task.taskType !in listOf("general", "meeting", "reminder", "deadline", "event")) {
            errors.add(ValidationError("taskType", "Invalid task type"))
        }

        if (task.priorityLevel !in listOf("low", "medium", "high", "critical")) {
            errors.add(ValidationError("priorityLevel", "Invalid priority level"))
        }

        if (task.status !in listOf("pending", "in_progress", "completed", "cancelled", "deferred")) {
            errors.add(ValidationError("status", "Invalid status"))
        }

        if (task.completionPercentage !in 0..100) {
            errors.add(ValidationError("completionPercentage", "Completion percentage must be 0-100"))
        }

        if (task.locationName != null && task.locationName.length > 255) {
            errors.add(ValidationError("locationName", "Location name must be 255 characters or less"))
        }

        if (task.locationAddress != null && task.locationAddress.length > 500) {
            errors.add(ValidationError("locationAddress", "Location address must be 500 characters or less"))
        }

        if (task.notes != null && task.notes.length > 2000) {
            errors.add(ValidationError("notes", "Notes must be 2000 characters or less"))
        }

        // Check due date not in past for incomplete tasks
        // This is a business rule, not validation constraint

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