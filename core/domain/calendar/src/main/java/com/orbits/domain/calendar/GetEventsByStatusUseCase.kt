package com.orbits.domain.calendar

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get events by status.
 */
class GetEventsByStatusUseCase @Inject constructor(
    private val calendarRepository: CalendarRepository
) {

    /**
     * Execute the use case.
     * @param status The status to filter by (scheduled, confirmed, cancelled, completed, rescheduled)
     * @return Flow emitting the filtered list of events
     */
    operator fun invoke(status: String): Flow<List<CalendarEvent>> {
        require(status.isNotBlank()) { "Status cannot be empty" }
        require(status in listOf("scheduled", "confirmed", "cancelled", "completed", "rescheduled")) {
            "Invalid status. Must be: scheduled, confirmed, cancelled, completed, or rescheduled"
        }
        return calendarRepository.getEventsByStatus(status)
    }
}