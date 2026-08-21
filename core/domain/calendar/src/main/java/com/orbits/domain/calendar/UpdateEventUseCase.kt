package com.orbits.domain.calendar

import com.orbits.core.common.Result
import com.orbits.core.common.extensions.nowUtc
import javax.inject.Inject

/**
 * Use case to update an existing calendar event.
 */
class UpdateEventUseCase @Inject constructor(
    private val calendarRepository: CalendarRepository
) {

    /**
     * Execute the use case.
     * @param event The updated event
     * @return Result containing the updated event, or error
     */
    suspend operator fun invoke(event: CalendarEvent): Result<CalendarEvent> {
        // Validate event
        val validationResult = CalendarEventValidation.validate(
            title = event.title,
            description = event.description,
            startDateTime = event.startDateTime,
            endDateTime = event.endDateTime,
            allDayEvent = event.allDayEvent,
            location = event.location,
            isVirtual = event.isVirtual,
            meetingLink = event.meetingLink,
            meetingPlatform = event.meetingPlatform,
            reminderMinutesBefore = event.reminderMinutesBefore,
            isRecurring = event.isRecurring,
            recurrencePattern = event.recurrencePattern,
            calendarColor = event.calendarColor,
            notes = event.notes,
            status = event.status
        )

        if (!validationResult.isValid) {
            return Result.Error(
                IllegalArgumentException(
                    validationResult.errors.joinToString { it.message }
                )
            )
        }

        return calendarRepository.updateEvent(event)
    }
}