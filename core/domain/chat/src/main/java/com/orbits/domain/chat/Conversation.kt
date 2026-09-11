package com.orbits.domain.chat

/**
 * Pure domain model for a conversation.
 * Contains NO Android dependencies — safe for KMP.
 */
data class Conversation(
    val id: String,
    val type: String = "direct", // direct, group
    val name: String? = null,
    val avatarUrl: String? = null,
    val isArchived: Boolean = false,
    val isMuted: Boolean = false,
    val mutedUntil: String? = null,
    val createdAt: String,
    val updatedAt: String
) {

    /**
     * Check if the conversation is a direct message.
     */
    fun isDirect(): Boolean = type == "direct"

    /**
     * Check if the conversation is a group chat.
     */
    fun isGroup(): Boolean = type == "group"

    /**
     * Get the conversation's display name.
     * For groups, returns the name. For direct messages, returns the other participant's name.
     */
    fun getDisplayName(currentUserId: String, participants: List<ConversationParticipant>): String {
        if (name != null && name.isNotBlank()) return name

        if (isDirect()) {
            val otherParticipant = participants.find { it.userId != currentUserId }
            return otherParticipant?.userId ?: "Unknown"
        }

        return "Group Chat"
    }

    /**
     * Get the conversation type display name.
     */
    fun getTypeDisplayName(): String {
        return when (type) {
            "direct" -> "Direct Message"
            "group" -> "Group Chat"
            else -> type.replaceFirstChar { it.uppercase() }
        }
    }

    /**
     * Check if the conversation has a name.
     */
    fun hasName(): Boolean = !name.isNullOrBlank()

    /**
     * Get the muted status display.
     */
    fun getMutedStatusDisplay(): String {
        if (!isMuted) return "Not Muted"
        if (mutedUntil == null) return "Muted Indefinitely"
        return try {
            val until = java.time.Instant.parse(mutedUntil)
            val now = java.time.Instant.now()
            if (until.isBefore(now)) {
                "Not Muted"
            } else {
                "Muted Until ${java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy").format(until.atZone(java.time.ZoneId.systemDefault()))}"
            }
        } catch (e: Exception) {
            "Muted"
        }
    }
}
