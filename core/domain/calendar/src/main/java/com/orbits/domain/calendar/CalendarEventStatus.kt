package com.orbits.domain.calendar

/**
 * Calendar event status values.
 */
enum class CalendarEventStatus(val displayName: String) {
    SCHEDULED("Scheduled"),
    CONFIRMED("Confirmed"),
    CANCELLED("Cancelled"),
    COMPLETED("Completed"),
    RESCHEDULED("Rescheduled");

    companion object {
        fun fromString(value: String): CalendarEventStatus? {
            return values().find { it.name.equals(value, ignoreCase = true) }
        }
    }
}