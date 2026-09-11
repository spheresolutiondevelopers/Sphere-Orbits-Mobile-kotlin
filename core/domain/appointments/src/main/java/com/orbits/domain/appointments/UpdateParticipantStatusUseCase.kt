package com.orbits.domain.appointments

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to update a participant's invitation status.
 */
class UpdateParticipantStatusUseCase @Inject constructor(
    private val appointmentRepository: AppointmentRepository
) {

    /**
     * Execute the use case.
     * @param appointmentId The ID of the appointment
     * @param email The email of the participant
     * @param status New status (pending, sent, accepted, declined, tentative)
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(
        appointmentId: String,
        email: String,
        status: String
    ): Result<Unit> {
        // Validate
        if (appointmentId.isBlank()) {
            return Result.Error(IllegalArgumentException("Appointment ID cannot be empty"))
        }
        if (email.isBlank()) {
            return Result.Error(IllegalArgumentException("Email cannot be empty"))
        }
        if (status !in listOf("pending", "sent", "accepted", "declined", "tentative")) {
            return Result.Error(
                IllegalArgumentException(
                    "Invalid status. Must be: pending, sent, accepted, declined, or tentative"
                )
            )
        }

        return appointmentRepository.updateParticipantStatus(appointmentId, email, status)
    }

    /**
     * Convenience method to accept an invitation.
     */
    suspend fun accept(appointmentId: String, email: String): Result<Unit> {
        return invoke(appointmentId, email, "accepted")
    }

    /**
     * Convenience method to decline an invitation.
     */
    suspend fun decline(appointmentId: String, email: String): Result<Unit> {
        return invoke(appointmentId, email, "declined")
    }

    /**
     * Convenience method to mark as tentative.
     */
    suspend fun tentative(appointmentId: String, email: String): Result<Unit> {
        return invoke(appointmentId, email, "tentative")
    }
}
