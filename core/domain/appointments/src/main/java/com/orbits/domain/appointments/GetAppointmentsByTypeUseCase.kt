package com.orbits.domain.appointments

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get appointments by type.
 */
class GetAppointmentsByTypeUseCase @Inject constructor(
    private val appointmentRepository: AppointmentRepository
) {

    /**
     * Execute the use case.
     * @param type The appointment type to filter by (general, doctor, business, personal, meeting)
     * @return Flow emitting the filtered list of appointments
     */
    operator fun invoke(type: String): Flow<List<Appointment>> {
        require(type.isNotBlank()) { "Type cannot be empty" }
        require(type in listOf("general", "doctor", "business", "personal", "meeting")) {
            "Invalid type. Must be: general, doctor, business, personal, or meeting"
        }
        return appointmentRepository.getAppointmentsByType(type)
    }
}
