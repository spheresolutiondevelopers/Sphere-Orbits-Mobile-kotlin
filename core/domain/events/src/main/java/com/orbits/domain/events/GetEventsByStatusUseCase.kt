package com.orbits.domain.events

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get events by status.
 */
class GetEventsByStatusUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {

    /**
     * Execute the use case.
     * @param status The status to filter by (planned, ongoing, completed, cancelled)
     * @return Flow emitting the filtered list of events
     */
    operator fun invoke(status: String): Flow<List<Event>> {
        require(status.isNotBlank()) { "Status cannot be empty" }
        require(status in listOf("planned", "ongoing", "completed", "cancelled")) {
            "Invalid status. Must be: planned, ongoing, completed, or cancelled"
        }
        return eventRepository.getEventsByStatus(status)
    }
}
