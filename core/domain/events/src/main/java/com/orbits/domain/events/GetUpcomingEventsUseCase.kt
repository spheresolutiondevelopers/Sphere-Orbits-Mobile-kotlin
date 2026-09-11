package com.orbits.domain.events

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get upcoming events.
 */
class GetUpcomingEventsUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {

    /**
     * Execute the use case.
     * @return Flow emitting the list of upcoming events
     */
    operator fun invoke(): Flow<List<Event>> {
        return eventRepository.getUpcomingEvents()
    }
}
