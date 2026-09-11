package com.orbits.domain.appointments

/**
 * Pure domain model for an appointment participant.
 */
data class AppointmentParticipant(
    val id: String,
    val appointmentId: String,
    val userId: String? = null,
    val email: String,
    val fullName: String? = null,
    val invitationStatus: String = "pending", // pending, sent, accepted, declined, tentative
    val participantRole: String = "attendee", // organizer, attendee, optional
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
