package com.orbits.domain.appointments

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to delete an appointment (soft delete).
 */
class DeleteAppointmentUseCase @Inject constructor(
    private val appointmentRepository: AppointmentRepository
) {

    /**
     * Execute the use case.
     * @param appointmentId The ID of the appointment to delete
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(appointmentId: String): Result<Unit> {
        if (appointmentId.isBlank()) {
            return Result.Error(IllegalArgumentException("Appointment ID cannot be empty"))
        }
        return appointmentRepository.deleteAppointment(appointmentId)
    }
}
