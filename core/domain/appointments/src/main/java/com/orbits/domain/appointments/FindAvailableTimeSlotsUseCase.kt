package com.orbits.domain.appointments

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to find available time slots for a given date.
 */
class FindAvailableTimeSlotsUseCase @Inject constructor(
    private val appointmentRepository: AppointmentRepository
) {

    /**
     * Execute the use case.
     * @param date The date to check (YYYY-MM-DD)
     * @param durationMinutes Duration of the appointment in minutes
     * @param startTime Start time (HH:MM) — defaults to 09:00
     * @param endTime End time (HH:MM) — defaults to 17:00
     * @return Result containing a list of available time slots
     */
    suspend operator fun invoke(
        date: String,
        durationMinutes: Int,
        startTime: String = "09:00",
        endTime: String = "17:00"
    ): Result<List<String>> {
        // Validate
        require(date.isNotBlank()) { "Date cannot be empty" }
        require(date.matches(Regex("^\\d{4}-\\d{2}-\\d{2}$"))) {
            "Invalid date format. Must be YYYY-MM-DD"
        }
        require(durationMinutes in 15..480) {
            "Duration must be between 15 and 480 minutes"
        }
        require(startTime.isNotBlank()) { "Start time cannot be empty" }
        require(endTime.isNotBlank()) { "End time cannot be empty" }

        try {
            java.time.LocalTime.parse(startTime)
            java.time.LocalTime.parse(endTime)
            require(java.time.LocalTime.parse(startTime) < java.time.LocalTime.parse(endTime)) {
                "Start time must be before end time"
            }
        } catch (e: Exception) {
            return Result.Error(IllegalArgumentException("Invalid time format: ${e.message}"))
        }

        return appointmentRepository.findAvailableTimeSlots(date, durationMinutes, startTime, endTime)
    }

    /**
     * Execute the use case with default working hours.
     * Working hours: 9:00 AM - 5:00 PM.
     */
    suspend fun withDefaultHours(
        date: String,
        durationMinutes: Int
    ): Result<List<String>> {
        return invoke(date, durationMinutes, "09:00", "17:00")
    }

    /**
     * Execute the use case with extended hours.
     * Extended hours: 8:00 AM - 8:00 PM.
     */
    suspend fun withExtendedHours(
        date: String,
        durationMinutes: Int
    ): Result<List<String>> {
        return invoke(date, durationMinutes, "08:00", "20:00")
    }
}
