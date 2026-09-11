package com.orbits.domain.meetings

/**
 * Meeting status values.
 */
enum class MeetingStatus(val displayName: String) {
    SCHEDULED("Scheduled"),
    LIVE("Live"),
    ENDED("Ended"),
    CANCELLED("Cancelled");

    companion object {
        fun fromString(value: String): MeetingStatus? {
            return values().find { it.name.equals(value, ignoreCase = true) }
        }
    }
}
