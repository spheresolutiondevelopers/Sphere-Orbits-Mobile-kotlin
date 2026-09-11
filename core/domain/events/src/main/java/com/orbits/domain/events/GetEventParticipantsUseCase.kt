package com.orbits.domain.events

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to get participants for an event.
 */
class GetEventParticipantsUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {

    /**
     * Execute the use case.
     * @param eventId The ID of the event
     * @return Result containing the list of participants, or error
     */
    suspend operator fun invoke(eventId: String): Result<List<EventParticipant>> {
        if (eventId.isBlank()) {
            return Result.Error(IllegalArgumentException("Event ID cannot be empty"))
        }
        return eventRepository.getParticipants(eventId)
    }

    /**
     * Execute the use case for confirmed participants only.
     * @param eventId The ID of the event
     * @return Result containing the list of confirmed participants, or error
     */
    suspend fun getConfirmed(eventId: String): Result<List<EventParticipant>> {
        if (eventId.isBlank()) {
            return Result.Error(IllegalArgumentException("Event ID cannot be empty"))
        }
        return eventRepository.getConfirmedParticipants(eventId)
    }
}
