package com.orbits.domain.calendar

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to get a single calendar event by ID.
 */
class GetEventUseCase @Inject constructor(
    private val calendarRepository: CalendarRepository
) {

    /**
     * Execute the use case.
     * @param eventId The ID of the event to retrieve
     * @return Result containing the event, or error
     */
    suspend operator fun invoke(eventId: String): Result<CalendarEvent> {
        if (eventId.isBlank()) {
            return Result.Error(IllegalArgumentException("Event ID cannot be empty"))
        }
        return calendarRepository.getEvent(eventId)
    }
}