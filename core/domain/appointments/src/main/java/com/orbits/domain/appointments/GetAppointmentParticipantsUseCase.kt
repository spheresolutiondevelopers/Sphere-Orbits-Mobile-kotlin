package com.orbits.domain.appointments

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to get participants for an appointment.
 */
class GetAppointmentParticipantsUseCase @Inject constructor(
    private val appointmentRepository: AppointmentRepository
) {

    /**
     * Execute the use case.
     * @param appointmentId The ID of the appointment
     * @return Result containing the list of participants, or error
     */
    suspend operator fun invoke(appointmentId: String): Result<List<AppointmentParticipant>> {
        if (appointmentId.isBlank()) {
            return Result.Error(IllegalArgumentException("Appointment ID cannot be empty"))
        }
        return appointmentRepository.getParticipants(appointmentId)
    }

    /**
     * Execute the use case for confirmed participants only.
     * @param appointmentId The ID of the appointment
     * @return Result containing the list of confirmed participants, or error
     */
    suspend fun getConfirmed(appointmentId: String): Result<List<AppointmentParticipant>> {
        if (appointmentId.isBlank()) {
            return Result.Error(IllegalArgumentException("Appointment ID cannot be empty"))
        }
        return appointmentRepository.getAcceptedParticipants(appointmentId)
    }
}
