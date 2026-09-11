package com.orbits.domain.appointments

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to cancel an appointment.
 */
class CancelAppointmentUseCase @Inject constructor(
    private val appointmentRepository: AppointmentRepository
) {

    /**
     * Execute the use case.
     * @param appointmentId The ID of the appointment to cancel
     * @return Result containing the cancelled appointment, or error
     */
    suspend operator fun invoke(appointmentId: String): Result<Appointment> {
        if (appointmentId.isBlank()) {
            return Result.Error(IllegalArgumentException("Appointment ID cannot be empty"))
        }
        return appointmentRepository.cancelAppointment(appointmentId)
    }
}
