package com.orbits.domain.analytics

import com.orbits.core.common.Result
import com.orbits.core.common.map
import javax.inject.Inject

/**
 * Use case to get complete dashboard statistics.
 */
class GetDashboardStatsUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {

    /**
     * Execute the use case.
     * @return Result containing the dashboard stats, or error
     */
    suspend operator fun invoke(): Result<DashboardStats> {
        return analyticsRepository.getDashboardStats()
    }

    /**
     * Execute with specific date range for custom dashboard.
     */
    suspend fun withDateRange(
        startDate: String,
        endDate: String
    ): Result<DashboardStats> {
        require(startDate.isNotBlank()) { "Start date cannot be empty" }
        require(endDate.isNotBlank()) { "End date cannot be empty" }
        require(startDate <= endDate) { "Start date must be before or equal to end date" }

        // This would call a custom version of getDashboardStats
        // For now, we just return the default
        return analyticsRepository.getDashboardStats()
    }

    /**
     * Get dashboard stats with custom insight generation.
     */
    suspend fun withCustomInsights(
        insights: List<ProductivityInsight>
    ): Result<DashboardStats> {
        val result = analyticsRepository.getDashboardStats()
        return result.map { stats ->
            stats.copy(insights = insights)
        }
    }
}
