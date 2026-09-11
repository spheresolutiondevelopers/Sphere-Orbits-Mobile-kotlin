package com.orbits.domain.appointments

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get appointments for a specific date.
 */
class GetAppointmentsForDateUseCase @Inject constructor(
    private val appointmentRepository: AppointmentRepository
) {

    /**
     * Execute the use case.
     * @param date The date in ISO format (YYYY-MM-DD)
     * @return Flow emitting the list of appointments
     */
    operator fun invoke(date: String): Flow<List<Appointment>> {
        require(date.isNotBlank()) { "Date cannot be empty" }
        require(date.matches(Regex("^\\d{4}-\\d{2}-\\d{2}$"))) {
            "Invalid date format. Must be YYYY-MM-DD"
        }
        return appointmentRepository.getAppointmentsForDate(date)
    }
}
