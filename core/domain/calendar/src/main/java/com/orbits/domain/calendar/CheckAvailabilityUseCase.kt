package com.orbits.domain.calendar

import com.orbits.core.common.Result
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case to check availability for a time slot.
 */
class CheckAvailabilityUseCase @Inject constructor(
    private val calendarRepository: CalendarRepository
) {

    /**
     * Check if a time slot is available.
     * @param startDateTime Start date and time (ISO 8601)
     * @param endDateTime End date and time (ISO 8601)
     * @param ignoreEventId Optional event ID to ignore (for update scenarios)
     * @return Result containing true if available, false if busy
     */
    suspend operator fun invoke(
        startDateTime: String,
        endDateTime: String,
        ignoreEventId: String? = null
    ): Result<Boolean> {
        // Validate
        require(startDateTime.isNotBlank()) { "Start date/time cannot be empty" }
        require(endDateTime.isNotBlank()) { "End date/time cannot be empty" }

        try {
            val start = java.time.Instant.parse(startDateTime)
            val end = java.time.Instant.parse(endDateTime)
            require(end > start) { "End time must be after start time" }
        } catch (e: Exception) {
            return Result.Error(IllegalArgumentException("Invalid date/time format: ${e.message}"))
        }

        // Check availability
        val result = calendarRepository.getEventsInDateRange(
            startDateTime.substring(0, 10),
            endDateTime.substring(0, 10)
        ).first()

        val hasConflict = result.any { event ->
            event.id != ignoreEventId &&
            event.status != "cancelled" &&
            event.status != "completed" &&
            event.startDateTime < endDateTime &&
            event.endDateTime > startDateTime
        }

        return Result.Success(!hasConflict)
    }

    /**
     * Find available time slots for a given date range.
     * @param date The date to check (YYYY-MM-DD)
     * @param durationMinutes Duration of the meeting in minutes
     * @param startTime Start time (HH:MM)
     * @param endTime End time (HH:MM)
     * @return Result containing a list of available time slots
     */
    suspend fun findAvailableSlots(
        date: String,
        durationMinutes: Int,
        startTime: String = "09:00",
        endTime: String = "17:00"
    ): Result<List<String>> {
        // Validate
        require(date.isNotBlank()) { "Date cannot be empty" }
        require(durationMinutes in 15..480) { "Duration must be between 15 and 480 minutes" }
        require(startTime.isNotBlank()) { "Start time cannot be empty" }
        require(endTime.isNotBlank()) { "End time cannot be empty" }

        try {
            java.time.LocalDate.parse(date)
            java.time.LocalTime.parse(startTime)
            java.time.LocalTime.parse(endTime)
            require(java.time.LocalTime.parse(startTime) < java.time.LocalTime.parse(endTime)) {
                "Start time must be before end time"
            }
        } catch (e: Exception) {
            return Result.Error(IllegalArgumentException("Invalid date/time format: ${e.message}"))
        }

        return Result.Error(UnsupportedOperationException("Not implemented"))
    }
}