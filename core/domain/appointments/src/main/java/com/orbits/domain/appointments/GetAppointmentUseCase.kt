package com.orbits.domain.appointments

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to get a single appointment by ID.
 */
class GetAppointmentUseCase @Inject constructor(
    private val appointmentRepository: AppointmentRepository
) {

    /**
     * Execute the use case.
     * @param appointmentId The ID of the appointment to retrieve
     * @return Result containing the appointment, or error
     */
    suspend operator fun invoke(appointmentId: String): Result<Appointment> {
        if (appointmentId.isBlank()) {
            return Result.Error(IllegalArgumentException("Appointment ID cannot be empty"))
        }
        return appointmentRepository.getAppointment(appointmentId)
    }

    /**
     * Get an appointment by external calendar ID.
     */
    suspend fun byExternalId(externalId: String): Result<Appointment> {
        if (externalId.isBlank()) {
            return Result.Error(IllegalArgumentException("External ID cannot be empty"))
        }
        return appointmentRepository.getAppointmentByExternalId(externalId)
    }
}
