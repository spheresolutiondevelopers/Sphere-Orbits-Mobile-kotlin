package com.orbits.domain.events

import com.orbits.core.common.Result
import com.orbits.core.common.extensions.nowUtc
import java.util.UUID
import javax.inject.Inject

/**
 * Use case to create a new event.
 */
class CreateEventUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {

    /**
     * Execute the use case.
     * @param name Event name
     * @param description Optional description
     * @param format Event format (In-Person, Virtual, Hybrid)
     * @param startDateTime Optional start date and time
     * @param endDateTime Optional end date and time
     * @param location Optional location
     * @param status Initial status (planned by default)
     * @param isRecurring Whether this event recurs
     * @param recurrencePattern Recurrence rule (RRULE)
     * @param budget Optional budget
     * @param currency Currency (USD by default)
     * @param maxAttendees Optional maximum attendees
     * @param isPublic Whether the event is public
     * @param categoryId Optional category ID
     * @param userId User ID (if null, uses current user)
     * @return Result containing the created event, or error
     */
    suspend operator fun invoke(
        name: String,
        description: String? = null,
        format: String? = null,
        startDateTime: String? = null,
        endDateTime: String? = null,
        location: String? = null,
        status: String = "planned",
        isRecurring: Boolean = false,
        recurrencePattern: String? = null,
        budget: Double? = null,
        currency: String = "USD",
        maxAttendees: Int? = null,
        isPublic: Boolean = false,
        categoryId: String? = null,
        userId: String? = null
    ): Result<Event> {
        // Validate input
        val validationResult = EventValidation.validate(
            name = name,
            description = description,
            format = format,
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            location = location,
            status = status,
            isRecurring = isRecurring,
            recurrencePattern = recurrencePattern,
            budget = budget,
            currency = currency,
            maxAttendees = maxAttendees,
            isPublic = isPublic
        )

        if (!validationResult.isValid) {
            return Result.Error(
                IllegalArgumentException(
                    validationResult.errors.joinToString { it.message }
                )
            )
        }

        // Create event
        val now = nowUtc()
        val event = Event(
            id = UUID.randomUUID().toString(),
            userId = userId ?: getCurrentUserId(),
            categoryId = categoryId,
            taskId = null,
            name = name,
            description = description,
            format = format,
            planningNotes = null,
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            location = location,
            status = status,
            isRecurring = isRecurring,
            recurrencePattern = recurrencePattern,
            budget = budget,
            currency = currency,
            maxAttendees = maxAttendees,
            isPublic = isPublic,
            imageUrl = null,
            coverPhotoUrl = null,
            isDeleted = false,
            createdAt = now,
            updatedAt = now
        )

        return eventRepository.createEvent(event)
    }

    // This should come from auth state
    private fun getCurrentUserId(): String {
        return "test_user_id"
    }
}
