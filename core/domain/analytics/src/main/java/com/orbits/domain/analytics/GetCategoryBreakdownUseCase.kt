package com.orbits.domain.analytics

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to get category breakdown data.
 */
class GetCategoryBreakdownUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {

    /**
     * Execute the use case.
     * @param startDate Start date in ISO format (YYYY-MM-DD)
     * @param endDate End date in ISO format (YYYY-MM-DD)
     * @return Result containing the category breakdown list, or error
     */
    suspend operator fun invoke(startDate: String, endDate: String): Result<List<CategoryBreakdown>> {
        require(startDate.isNotBlank()) { "Start date cannot be empty" }
        require(endDate.isNotBlank()) { "End date cannot be empty" }
        require(startDate <= endDate) { "Start date must be before or equal to end date" }

        return analyticsRepository.getCategoryBreakdown(startDate, endDate)
    }

    /**
     * Get breakdown for this week.
     */
    suspend fun thisWeek(): Result<List<CategoryBreakdown>> {
        val today = java.time.LocalDate.now()
        val startOfWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1)
        return invoke(startOfWeek.toString(), today.toString())
    }

    /**
     * Get breakdown for this month.
     */
    suspend fun thisMonth(): Result<List<CategoryBreakdown>> {
        val today = java.time.LocalDate.now()
        val startOfMonth = today.withDayOfMonth(1)
        return invoke(startOfMonth.toString(), today.toString())
    }
}
