package com.orbits.domain.events

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to update a participant's RSVP status.
 */
class UpdateParticipantStatusUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {

    /**
     * Execute the use case.
     * @param eventId The ID of the event
     * @param participantId The ID of the participant
     * @param status New status (pending, sent, accepted, declined, tentative)
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(
        eventId: String,
        participantId: String,
        status: String
    ): Result<Unit> {
        // Validate
        if (eventId.isBlank()) {
            return Result.Error(IllegalArgumentException("Event ID cannot be empty"))
        }
        if (participantId.isBlank()) {
            return Result.Error(IllegalArgumentException("Participant ID cannot be empty"))
        }
        if (status !in listOf("pending", "sent", "accepted", "declined", "tentative")) {
            return Result.Error(
                IllegalArgumentException(
                    "Invalid status. Must be: pending, sent, accepted, declined, or tentative"
                )
            )
        }

        return eventRepository.updateParticipantStatus(eventId, participantId, status)
    }

    /**
     * Convenience method to accept an invitation.
     */
    suspend fun accept(eventId: String, participantId: String): Result<Unit> {
        return invoke(eventId, participantId, "accepted")
    }

    /**
     * Convenience method to decline an invitation.
     */
    suspend fun decline(eventId: String, participantId: String): Result<Unit> {
        return invoke(eventId, participantId, "declined")
    }

    /**
     * Convenience method to mark as tentative.
     */
    suspend fun tentative(eventId: String, participantId: String): Result<Unit> {
        return invoke(eventId, participantId, "tentative")
    }
}
