package com.orbits.domain.chat

/**
 * Participant roles in a conversation.
 */
enum class ParticipantRole(val displayName: String) {
    ADMIN("Admin"),
    MEMBER("Member"),
    VIEWER("Viewer");

    companion object {
        fun fromString(value: String): ParticipantRole? {
            return values().find { it.name.equals(value, ignoreCase = true) }
        }

        fun fromDisplayName(value: String): ParticipantRole? {
            return values().find { it.displayName.equals(value, ignoreCase = true) }
        }
    }
}
