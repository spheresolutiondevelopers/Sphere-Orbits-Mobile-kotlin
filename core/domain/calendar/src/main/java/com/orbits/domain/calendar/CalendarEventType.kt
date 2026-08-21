package com.orbits.domain.calendar

/**
 * Calendar event types.
 */
enum class CalendarEventType(val displayName: String) {
    GENERAL("General"),
    DOCTOR("Doctor"),
    BUSINESS("Business"),
    PERSONAL("Personal"),
    MEETING("Meeting"),
    WORKSHOP("Workshop"),
    CONFERENCE("Conference");

    companion object {
        fun fromString(value: String): CalendarEventType? {
            return values().find { it.name.equals(value, ignoreCase = true) }
        }
    }
}