package com.orbits.domain.meetings

/**
 * Meeting platforms.
 */
enum class MeetingPlatform(val displayName: String) {
    ZOOM("Zoom"),
    TEAMS("Microsoft Teams"),
    GOOGLE_MEET("Google Meet"),
    CUSTOM("Custom");

    companion object {
        fun fromString(value: String): MeetingPlatform? {
            return values().find { it.name.equals(value, ignoreCase = true) }
        }

        fun fromDisplayName(value: String): MeetingPlatform? {
            return values().find { it.displayName.equals(value, ignoreCase = true) }
        }
    }
}
