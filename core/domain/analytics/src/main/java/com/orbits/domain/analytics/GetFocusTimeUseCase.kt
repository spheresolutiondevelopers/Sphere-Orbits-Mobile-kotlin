package com.orbits.domain.analytics

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to get focus time data.
 */
class GetFocusTimeUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {

    /**
     * Execute the use case.
     * @param startDate Start date in ISO format (YYYY-MM-DD)
     * @param endDate End date in ISO format (YYYY-MM-DD)
     * @return Result containing the focus time list, or error
     */
    suspend operator fun invoke(startDate: String, endDate: String): Result<List<FocusTime>> {
        require(startDate.isNotBlank()) { "Start date cannot be empty" }
        require(endDate.isNotBlank()) { "End date cannot be empty" }
        require(startDate <= endDate) { "Start date must be before or equal to end date" }

        return analyticsRepository.getFocusTime(startDate, endDate)
    }

    /**
     * Get focus time for this week.
     */
    suspend fun thisWeek(): Result<List<FocusTime>> {
        val today = java.time.LocalDate.now()
        val startOfWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1)
        return invoke(startOfWeek.toString(), today.toString())
    }

    /**
     * Get focus time for this month.
     */
    suspend fun thisMonth(): Result<List<FocusTime>> {
        val today = java.time.LocalDate.now()
        val startOfMonth = today.withDayOfMonth(1)
        return invoke(startOfMonth.toString(), today.toString())
    }

    /**
     * Get today's focus time.
     */
    suspend fun today(): Result<List<FocusTime>> {
        val today = java.time.LocalDate.now().toString()
        return invoke(today, today)
    }
}
