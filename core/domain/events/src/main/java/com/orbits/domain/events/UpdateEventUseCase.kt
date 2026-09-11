package com.orbits.domain.events

import com.orbits.core.common.Result
import com.orbits.core.common.extensions.nowUtc
import javax.inject.Inject

/**
 * Use case to update an existing event.
 */
class UpdateEventUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {

    /**
     * Execute the use case.
     * @param event The updated event
     * @return Result containing the updated event, or error
     */
    suspend operator fun invoke(event: Event): Result<Event> {
        // Validate event
        val validationResult = EventValidation.validate(
            name = event.name,
            description = event.description,
            format = event.format,
            startDateTime = event.startDateTime,
            endDateTime = event.endDateTime,
            location = event.location,
            status = event.status,
            isRecurring = event.isRecurring,
            recurrencePattern = event.recurrencePattern,
            budget = event.budget,
            currency = event.currency,
            maxAttendees = event.maxAttendees,
            isPublic = event.isPublic
        )

        if (!validationResult.isValid) {
            return Result.Error(
                IllegalArgumentException(
                    validationResult.errors.joinToString { it.message }
                )
            )
        }

        return eventRepository.updateEvent(event)
    }
}
