package com.orbits.domain.appointments

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to remove a participant from an appointment.
 */
class RemoveParticipantUseCase @Inject constructor(
    private val appointmentRepository: AppointmentRepository
) {

    /**
     * Execute the use case.
     * @param appointmentId The ID of the appointment
     * @param participantId The ID of the participant to remove
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(appointmentId: String, participantId: String): Result<Unit> {
        if (appointmentId.isBlank()) {
            return Result.Error(IllegalArgumentException("Appointment ID cannot be empty"))
        }
        if (participantId.isBlank()) {
            return Result.Error(IllegalArgumentException("Participant ID cannot be empty"))
        }
        return appointmentRepository.removeParticipant(appointmentId, participantId)
    }
}
