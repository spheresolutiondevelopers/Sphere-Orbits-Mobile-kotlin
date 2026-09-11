package com.orbits.domain.events

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get events by category.
 */
class GetEventsByCategoryUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {

    /**
     * Execute the use case.
     * @param categoryId The category ID to filter by
     * @return Flow emitting the filtered list of events
     */
    operator fun invoke(categoryId: String): Flow<List<Event>> {
        require(categoryId.isNotBlank()) { "Category ID cannot be empty" }
        return eventRepository.getEventsByCategory(categoryId)
    }
}
