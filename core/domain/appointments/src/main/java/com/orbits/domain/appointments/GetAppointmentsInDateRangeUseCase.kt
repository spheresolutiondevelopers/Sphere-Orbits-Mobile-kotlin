package com.orbits.domain.appointments

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get appointments in a date range.
 */
class GetAppointmentsInDateRangeUseCase @Inject constructor(
    private val appointmentRepository: AppointmentRepository
) {

    /**
     * Execute the use case.
     * @param startDate Start date in ISO format (YYYY-MM-DD)
     * @param endDate End date in ISO format (YYYY-MM-DD)
     * @return Flow emitting the list of appointments
     */
    operator fun invoke(startDate: String, endDate: String): Flow<List<Appointment>> {
        require(startDate.isNotBlank()) { "Start date cannot be empty" }
        require(endDate.isNotBlank()) { "End date cannot be empty" }
        require(startDate <= endDate) { "Start date must be before or equal to end date" }
        return appointmentRepository.getAppointmentsInDateRange(startDate, endDate)
    }
}
