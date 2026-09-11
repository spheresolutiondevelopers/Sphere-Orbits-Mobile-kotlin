package com.orbits.domain.chat

/**
 * Pure domain model for a conversation participant.
 */
data class ConversationParticipant(
    val id: String,
    val conversationId: String,
    val userId: String,
    val role: String = "member", // admin, member, viewer
    val joinedAt: String,
    val leftAt: String? = null,
    val lastReadMessageId: String? = null,
    val createdAt: String,
    val updatedAt: String
) {

    /**
     * Check if the participant is an admin.
     */
    fun isAdmin(): Boolean = role == "admin"

    /**
     * Check if the participant is a member.
     */
    fun isMember(): Boolean = role == "member"

    /**
     * Check if the participant is a viewer.
     */
    fun isViewer(): Boolean = role == "viewer"

    /**
     * Check if the participant has left the conversation.
     */
    fun hasLeft(): Boolean = leftAt != null

    /**
     * Check if the participant is active (has not left).
     */
    fun isActive(): Boolean = leftAt == null

    /**
     * Get the participant's role display name.
     */
    fun getRoleDisplayName(): String {
        return when (role) {
            "admin" -> "Admin"
            "member" -> "Member"
            "viewer" -> "Viewer"
            else -> role.replaceFirstChar { it.uppercase() }
        }
    }

    /**
     * Get the formatted join date.
     */
    fun getFormattedJoinDate(): String {
        return try {
            val instant = java.time.Instant.parse(joinedAt)
            val date = instant.atZone(java.time.ZoneId.systemDefault())
            java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")
                .format(date)
        } catch (e: Exception) {
            joinedAt
        }
    }

    /**
     * Check if the participant has read a message.
     */
    fun hasReadMessage(messageId: String): Boolean {
        return lastReadMessageId != null && lastReadMessageId >= messageId
    }
}
