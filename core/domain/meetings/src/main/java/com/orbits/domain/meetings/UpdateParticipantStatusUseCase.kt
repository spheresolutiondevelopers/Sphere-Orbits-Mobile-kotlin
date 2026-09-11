package com.orbits.domain.meetings

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to update a participant's invitation status.
 */
class UpdateParticipantStatusUseCase @Inject constructor(
    private val meetingRepository: MeetingRepository
) {

    /**
     * Execute the use case.
     * @param meetingId The ID of the meeting
     * @param email The email of the participant
     * @param status New status (pending, sent, accepted, declined, tentative)
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(
        meetingId: String,
        email: String,
        status: String
    ): Result<Unit> {
        // Validate
        if (meetingId.isBlank()) {
            return Result.Error(IllegalArgumentException("Meeting ID cannot be empty"))
        }
        if (email.isBlank()) {
            return Result.Error(IllegalArgumentException("Email cannot be empty"))
        }
        if (status !in listOf("pending", "sent", "accepted", "declined", "tentative")) {
            return Result.Error(
                IllegalArgumentException(
                    "Invalid status. Must be: pending, sent, accepted, declined, or tentative"
                )
            )
        }

        return meetingRepository.updateParticipantStatus(meetingId, email, status)
    }

    /**
     * Convenience method to accept an invitation.
     */
    suspend fun accept(meetingId: String, email: String): Result<Unit> {
        return invoke(meetingId, email, "accepted")
    }

    /**
     * Convenience method to decline an invitation.
     */
    suspend fun decline(meetingId: String, email: String): Result<Unit> {
        return invoke(meetingId, email, "declined")
    }

    /**
     * Convenience method to mark as tentative.
     */
    suspend fun tentative(meetingId: String, email: String): Result<Unit> {
        return invoke(meetingId, email, "tentative")
    }
}
