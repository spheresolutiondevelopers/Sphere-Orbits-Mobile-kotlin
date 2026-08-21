package com.orbits.domain.calendar

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get all calendar events for the current user.
 */
class GetEventsUseCase @Inject constructor(
    private val calendarRepository: CalendarRepository
) {

    /**
     * Execute the use case.
     * @param startDate Start date in ISO format (YYYY-MM-DD)
     * @param endDate End date in ISO format (YYYY-MM-DD)
     * @return Flow emitting the list of events in the date range
     */
    operator fun invoke(startDate: String, endDate: String): Flow<List<CalendarEvent>> {
        require(startDate.isNotBlank()) { "Start date cannot be empty" }
        require(endDate.isNotBlank()) { "End date cannot be empty" }
        require(startDate <= endDate) { "Start date must be before or equal to end date" }

        return calendarRepository.getEventsInDateRange(startDate, endDate)
    }
}