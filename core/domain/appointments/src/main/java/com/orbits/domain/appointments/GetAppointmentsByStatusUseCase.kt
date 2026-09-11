package com.orbits.domain.appointments

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get appointments by status.
 */
class GetAppointmentsByStatusUseCase @Inject constructor(
    private val appointmentRepository: AppointmentRepository
) {

    /**
     * Execute the use case.
     * @param status The status to filter by (scheduled, confirmed, cancelled, completed, rescheduled)
     * @return Flow emitting the filtered list of appointments
     */
    operator fun invoke(status: String): Flow<List<Appointment>> {
        require(status.isNotBlank()) { "Status cannot be empty" }
        require(status in listOf("scheduled", "confirmed", "cancelled", "completed", "rescheduled")) {
            "Invalid status. Must be: scheduled, confirmed, cancelled, completed, or rescheduled"
        }
        return appointmentRepository.getAppointmentsByStatus(status)
    }
}
