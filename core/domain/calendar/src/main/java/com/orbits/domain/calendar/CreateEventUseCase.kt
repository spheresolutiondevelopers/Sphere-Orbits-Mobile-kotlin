package com.orbits.domain.calendar

import com.orbits.core.common.Result
import com.orbits.core.common.extensions.nowUtc
import java.util.UUID
import javax.inject.Inject

/**
 * Use case to create a new calendar event.
 */
class CreateEventUseCase @Inject constructor(
    private val calendarRepository: CalendarRepository
) {

    /**
     * Execute the use case.
     * @param title Event title
     * @param description Optional description
     * @param startDateTime Start date and time (ISO 8601)
     * @param endDateTime End date and time (ISO 8601)
     * @param allDayEvent Whether this is an all-day event
     * @param location Optional location
     * @param isVirtual Whether this is a virtual event
     * @param meetingLink Optional meeting link
     * @param meetingPlatform Optional meeting platform
     * @param reminderMinutesBefore Reminder minutes before
     * @param isRecurring Whether this event recurs
     * @param recurrencePattern Recurrence rule (RRULE)
     * @param calendarColor Optional calendar color
     * @param notes Optional notes
     * @param userId User ID (if null, uses current user)
     * @return Result containing the created event, or error
     */
    suspend operator fun invoke(
        title: String,
        description: String? = null,
        startDateTime: String,
        endDateTime: String,
        allDayEvent: Boolean = false,
        location: String? = null,
        isVirtual: Boolean = false,
        meetingLink: String? = null,
        meetingPlatform: String? = null,
        reminderMinutesBefore: Int = 15,
        isRecurring: Boolean = false,
        recurrencePattern: String? = null,
        calendarColor: String? = "#2196F3",
        notes: String? = null,
        userId: String? = null
    ): Result<CalendarEvent> {
        // Validate input
        val validationResult = CalendarEventValidation.validate(
            title = title,
            description = description,
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            allDayEvent = allDayEvent,
            location = location,
            isVirtual = isVirtual,
            meetingLink = meetingLink,
            meetingPlatform = meetingPlatform,
            reminderMinutesBefore = reminderMinutesBefore,
            isRecurring = isRecurring,
            recurrencePattern = recurrencePattern,
            calendarColor = calendarColor,
            notes = notes
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
        val event = CalendarEvent(
            id = UUID.randomUUID().toString(),
            userId = userId ?: getCurrentUserId(),
            title = title,
            description = description,
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            allDayEvent = allDayEvent,
            location = location,
            isVirtual = isVirtual,
            meetingLink = meetingLink,
            meetingPlatform = meetingPlatform,
            status = "scheduled",
            reminderMinutesBefore = reminderMinutesBefore,
            isRecurring = isRecurring,
            recurrencePattern = recurrencePattern,
            calendarColor = calendarColor,
            notes = notes,
            externalEventId = null,
            externalSyncStatus = "not_synced",
            isDeleted = false,
            createdAt = now,
            updatedAt = now
        )

        return calendarRepository.createEvent(event)
    }

    // This should come from auth state
    private fun getCurrentUserId(): String {
        return "test_user_id"
    }
}