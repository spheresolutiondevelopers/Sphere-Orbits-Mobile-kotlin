package com.orbits.domain.calendar

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to delete a calendar event.
 */
class DeleteEventUseCase @Inject constructor(
    private val calendarRepository: CalendarRepository
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
        return calendarRepository.deleteEvent(eventId)
    }
}