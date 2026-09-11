package com.orbits.domain.meetings

/**
 * Pure domain model for a meeting participant.
 */
data class MeetingParticipant(
    val id: String,
    val meetingId: String,
    val userId: String? = null,
    val email: String,
    val fullName: String? = null,
    val invitationStatus: String = "pending", // pending, sent, accepted, declined, tentative
    val participantRole: String = "attendee", // organizer, presenter, attendee
    val joinedAt: String? = null,
    val leftAt: String? = null,
    val durationSeconds: Int? = null,
    val createdAt: String,
    val updatedAt: String
) {

    /**
     * Check if the participant has accepted the invitation.
     */
    fun isAccepted(): Boolean = invitationStatus == "accepted"

    /**
     * Check if the participant has declined.
     */
    fun isDeclined(): Boolean = invitationStatus == "declined"

    /**
     * Check if the participant is pending.
     */
    fun isPending(): Boolean = invitationStatus in listOf("pending", "sent", "tentative")

    /**
     * Check if the participant has joined the meeting.
     */
    fun hasJoined(): Boolean = joinedAt != null

    /**
     * Get the participant's role display name.
     */
    fun getRoleDisplayName(): String {
        return when (participantRole) {
            "organizer" -> "Organizer"
            "presenter" -> "Presenter"
            "attendee" -> "Attendee"
            else -> participantRole.replaceFirstChar { it.uppercase() }
        }
    }

    /**
     * Get the invitation status display name.
     */
    fun getStatusDisplayName(): String {
        return when (invitationStatus) {
            "pending" -> "Pending"
            "sent" -> "Invited"
            "accepted" -> "Accepted"
            "declined" -> "Declined"
            "tentative" -> "Tentative"
            else -> invitationStatus.replaceFirstChar { it.uppercase() }
        }
    }
}
