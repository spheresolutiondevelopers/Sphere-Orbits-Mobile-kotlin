package com.orbits.domain.events

/**
 * Event status values.
 */
enum class EventStatus(val displayName: String) {
    PLANNED("Planned"),
    ONGOING("Ongoing"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    companion object {
        fun fromString(value: String): EventStatus? {
            return values().find { it.name.equals(value, ignoreCase = true) }
        }
    }
}
