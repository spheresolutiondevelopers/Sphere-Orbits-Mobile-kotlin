package com.orbits.domain.tasks

/**
 * Task types.
 */
enum class TaskType(val displayName: String) {
    GENERAL("General"),
    MEETING("Meeting"),
    REMINDER("Reminder"),
    DEADLINE("Deadline"),
    EVENT("Event");

    companion object {
        fun fromString(value: String): TaskType? {
            return values().find { it.name.equals(value, ignoreCase = true) }
        }
    }
}