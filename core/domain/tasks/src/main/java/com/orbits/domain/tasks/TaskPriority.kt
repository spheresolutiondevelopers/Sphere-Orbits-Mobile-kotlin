package com.orbits.domain.tasks

/**
 * Task priority levels.
 */
enum class TaskPriority(val displayName: String, val value: Int) {
    LOW("Low", 1),
    MEDIUM("Medium", 2),
    HIGH("High", 3),
    CRITICAL("Critical", 4);

    companion object {
        fun fromString(value: String): TaskPriority? {
            return values().find { it.name.equals(value, ignoreCase = true) }
        }

        fun fromValue(value: Int): TaskPriority? {
            return values().find { it.value == value }
        }
    }
}