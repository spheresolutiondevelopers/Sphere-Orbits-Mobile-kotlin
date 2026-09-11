package com.orbits.domain.chat

/**
 * Conversation types.
 */
enum class ConversationType(val displayName: String) {
    DIRECT("Direct Message"),
    GROUP("Group Chat");

    companion object {
        fun fromString(value: String): ConversationType? {
            return values().find { it.name.equals(value, ignoreCase = true) }
        }

        fun fromDisplayName(value: String): ConversationType? {
            return values().find { it.displayName.equals(value, ignoreCase = true) }
        }
    }
}
