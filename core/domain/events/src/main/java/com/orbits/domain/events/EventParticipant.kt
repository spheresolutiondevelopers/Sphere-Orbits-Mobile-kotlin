package com.orbits.domain.events

/**
 * Pure domain model for an event participant.
 */
data class EventParticipant(
    val id: String,
    val eventId: String,
    val userId: String? = null,
    val email: String,
    val fullName: String? = null,
    val invitationStatus: String = "pending", // pending, sent, accepted, declined, tentative
    val participantRole: String = "attendee", // organizer, attendee, optional, speaker, vendor
    val plusOnes: Int = 0,
    val dietaryRestrictions: String? = null,
    val specialRequests: String? = null,
    val checkedIn: Boolean = false,
    val checkInTime: String? = null,
    val createdAt: String,
    val updatedAt: String
) {

    /**
     * Check if the participant has confirmed attendance.
     */
    fun isConfirmed(): Boolean = invitationStatus == "accepted"

    /**
     * Check if the participant has declined.
     */
    fun isDeclined(): Boolean = invitationStatus == "declined"

    /**
     * Check if the participant is pending.
     */
    fun isPending(): Boolean = invitationStatus in listOf("pending", "sent", "tentative")

    /**
     * Get the participant's role display name.
     */
    fun getRoleDisplayName(): String {
        return when (participantRole) {
            "organizer" -> "Organizer"
            "attendee" -> "Attendee"
            "optional" -> "Optional"
            "speaker" -> "Speaker"
            "vendor" -> "Vendor"
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

    /**
     * Get the total number of people this participant is bringing.
     */
    fun getTotalCount(): Int = 1 + plusOnes

    /**
     * Check if the participant has checked in.
     */
    fun isCheckedIn(): Boolean = checkedIn
}
