package com.orbits.domain.chat

/**
 * Message delivery status.
 */
enum class MessageStatus(val displayName: String) {
    SENT("Sent"),
    DELIVERED("Delivered"),
    READ("Read"),
    FAILED("Failed"),
    DELETED("Deleted");

    companion object {
        fun fromString(value: String): MessageStatus? {
            return values().find { it.name.equals(value, ignoreCase = true) }
        }
    }
}
