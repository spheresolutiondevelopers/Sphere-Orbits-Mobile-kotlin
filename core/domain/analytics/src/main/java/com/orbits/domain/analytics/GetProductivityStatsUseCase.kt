package com.orbits.domain.analytics

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to get productivity statistics.
 */
class GetProductivityStatsUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {

    /**
     * Execute the use case.
     * @param startDate Start date in ISO format (YYYY-MM-DD)
     * @param endDate End date in ISO format (YYYY-MM-DD)
     * @return Result containing the productivity stats, or error
     */
    suspend operator fun invoke(startDate: String, endDate: String): Result<ProductivityStats> {
        require(startDate.isNotBlank()) { "Start date cannot be empty" }
        require(endDate.isNotBlank()) { "End date cannot be empty" }
        require(startDate <= endDate) { "Start date must be before or equal to end date" }

        return analyticsRepository.getProductivityStats(startDate, endDate)
    }

    /**
     * Get productivity stats for today.
     */
    suspend fun today(): Result<ProductivityStats> {
        val today = java.time.LocalDate.now().toString()
        return invoke(today, today)
    }

    /**
     * Get productivity stats for this week.
     */
    suspend fun thisWeek(): Result<ProductivityStats> {
        val today = java.time.LocalDate.now()
        val startOfWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1)
        return invoke(startOfWeek.toString(), today.toString())
    }

    /**
     * Get productivity stats for this month.
     */
    suspend fun thisMonth(): Result<ProductivityStats> {
        val today = java.time.LocalDate.now()
        val startOfMonth = today.withDayOfMonth(1)
        return invoke(startOfMonth.toString(), today.toString())
    }
}
