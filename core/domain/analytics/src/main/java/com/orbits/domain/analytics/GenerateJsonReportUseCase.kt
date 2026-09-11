package com.orbits.domain.analytics

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to generate a JSON report.
 */
class GenerateJsonReportUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {

    /**
     * Execute the use case.
     * @param startDate Start date in ISO format (YYYY-MM-DD)
     * @param endDate End date in ISO format (YYYY-MM-DD)
     * @return Result containing the JSON report as a string, or error
     */
    suspend operator fun invoke(startDate: String, endDate: String): Result<String> {
        require(startDate.isNotBlank()) { "Start date cannot be empty" }
        require(endDate.isNotBlank()) { "End date cannot be empty" }
        require(startDate <= endDate) { "Start date must be before or equal to end date" }

        return analyticsRepository.generateJsonReport(startDate, endDate)
    }

    /**
     * Generate weekly JSON report.
     */
    suspend fun weekly(): Result<String> {
        val today = java.time.LocalDate.now()
        val startOfWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1)
        return invoke(startOfWeek.toString(), today.toString())
    }

    /**
     * Generate monthly JSON report.
     */
    suspend fun monthly(): Result<String> {
        val today = java.time.LocalDate.now()
        val startOfMonth = today.withDayOfMonth(1)
        return invoke(startOfMonth.toString(), today.toString())
    }
}
