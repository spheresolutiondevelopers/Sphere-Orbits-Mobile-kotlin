package com.orbits.domain.appointments

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get all appointments for the current user.
 */
class GetAppointmentsUseCase @Inject constructor(
    private val appointmentRepository: AppointmentRepository
) {

    /**
     * Execute the use case.
     * @return Flow emitting the list of appointments
     */
    operator fun invoke(): Flow<List<Appointment>> {
        return appointmentRepository.getAppointments()
    }
}
