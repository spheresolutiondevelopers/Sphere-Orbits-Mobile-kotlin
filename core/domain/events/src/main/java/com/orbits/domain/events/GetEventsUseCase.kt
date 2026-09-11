package com.orbits.domain.events

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get all events for the current user.
 */
class GetEventsUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {

    /**
     * Execute the use case.
     * @return Flow emitting the list of events
     */
    operator fun invoke(): Flow<List<Event>> {
        return eventRepository.getEvents()
    }
}
