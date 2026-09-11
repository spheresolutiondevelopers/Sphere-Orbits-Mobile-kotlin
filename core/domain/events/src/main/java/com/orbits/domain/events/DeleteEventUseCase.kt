package com.orbits.domain.events

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to delete an event.
 */
class DeleteEventUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {

    /**
     * Execute the use case.
     * @param eventId The ID of the event to delete
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(eventId: String): Result<Unit> {
        if (eventId.isBlank()) {
            return Result.Error(IllegalArgumentException("Event ID cannot be empty"))
        }
        return eventRepository.deleteEvent(eventId)
    }
}
