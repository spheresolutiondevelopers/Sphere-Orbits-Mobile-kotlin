package com.orbits.domain.appointments

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to check availability for a time slot.
 */
class CheckAvailabilityUseCase @Inject constructor(
    private val appointmentRepository: AppointmentRepository
) {

    /**
     * Execute the use case.
     * @param startDateTime Start date and time (ISO 8601)
     * @param endDateTime End date and time (ISO 8601)
     * @param ignoreAppointmentId Optional appointment ID to ignore (for update scenarios)
     * @return Result containing true if available, false if busy
     */
    suspend operator fun invoke(
        startDateTime: String,
        endDateTime: String,
        ignoreAppointmentId: String? = null
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

        return appointmentRepository.checkAvailability(startDateTime, endDateTime, ignoreAppointmentId)
    }
}
