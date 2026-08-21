package com.orbits.domain.tasks

/**
 * Task status values.
 */
enum class TaskStatus(val displayName: String) {
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled"),
    DEFERRED("Deferred");

    companion object {
        fun fromString(value: String): TaskStatus? {
            return values().find { it.name.equals(value, ignoreCase = true) }
        }
    }
}