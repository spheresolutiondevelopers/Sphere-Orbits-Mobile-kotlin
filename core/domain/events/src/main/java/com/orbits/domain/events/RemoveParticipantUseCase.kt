package com.orbits.domain.events

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to remove a participant from an event.
 */
class RemoveParticipantUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {

    /**
     * Execute the use case.
     * @param eventId The ID of the event
     * @param participantId The ID of the participant to remove
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(eventId: String, participantId: String): Result<Unit> {
        if (eventId.isBlank()) {
            return Result.Error(IllegalArgumentException("Event ID cannot be empty"))
        }
        if (participantId.isBlank()) {
            return Result.Error(IllegalArgumentException("Participant ID cannot be empty"))
        }
        return eventRepository.removeParticipant(eventId, participantId)
    }
}
