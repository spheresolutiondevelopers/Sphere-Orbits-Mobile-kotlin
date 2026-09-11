package com.orbits.domain.analytics

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to get task completion trend data.
 */
class GetTaskCompletionTrendUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {

    /**
     * Execute the use case.
     * @param startDate Start date in ISO format (YYYY-MM-DD)
     * @param endDate End date in ISO format (YYYY-MM-DD)
     * @return Result containing the task completion trend, or error
     */
    suspend operator fun invoke(startDate: String, endDate: String): Result<TaskCompletionTrend> {
        require(startDate.isNotBlank()) { "Start date cannot be empty" }
        require(endDate.isNotBlank()) { "End date cannot be empty" }
        require(startDate <= endDate) { "Start date must be before or equal to end date" }

        return analyticsRepository.getTaskCompletionTrend(startDate, endDate)
    }

    /**
     * Get trend for this week.
     */
    suspend fun thisWeek(): Result<TaskCompletionTrend> {
        val today = java.time.LocalDate.now()
        val startOfWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1)
        return invoke(startOfWeek.toString(), today.toString())
    }

    /**
     * Get trend for this month.
     */
    suspend fun thisMonth(): Result<TaskCompletionTrend> {
        val today = java.time.LocalDate.now()
        val startOfMonth = today.withDayOfMonth(1)
        return invoke(startOfMonth.toString(), today.toString())
    }

    /**
     * Get trend for a custom number of days.
     */
    suspend fun lastDays(days: Int): Result<TaskCompletionTrend> {
        require(days in 1..90) { "Days must be between 1 and 90" }

        val today = java.time.LocalDate.now()
        val startDate = today.minusDays(days.toLong() - 1)
        return invoke(startDate.toString(), today.toString())
    }
}
