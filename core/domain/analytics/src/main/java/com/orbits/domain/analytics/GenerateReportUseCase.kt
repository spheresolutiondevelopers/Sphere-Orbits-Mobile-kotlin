package com.orbits.domain.analytics

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to generate a report.
 */
class GenerateReportUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {

    /**
     * Execute the use case.
     * @param startDate Start date in ISO format (YYYY-MM-DD)
     * @param endDate End date in ISO format (YYYY-MM-DD)
     * @param format Report format (json, pdf, csv)
     * @return Result containing the report as a string, or error
     */
    suspend operator fun invoke(
        startDate: String,
        endDate: String,
        format: String = "json"
    ): Result<String> {
        require(startDate.isNotBlank()) { "Start date cannot be empty" }
        require(endDate.isNotBlank()) { "End date cannot be empty" }
        require(startDate <= endDate) { "Start date must be before or equal to end date" }
        require(format in listOf("json", "pdf", "csv")) {
            "Invalid format. Must be: json, pdf, or csv"
        }

        return analyticsRepository.generateReport(startDate, endDate, format)
    }

    /**
     * Generate a weekly report.
     */
    suspend fun weekly(format: String = "json"): Result<String> {
        val today = java.time.LocalDate.now()
        val startOfWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1)
        return invoke(startOfWeek.toString(), today.toString(), format)
    }

    /**
     * Generate a monthly report.
     */
    suspend fun monthly(format: String = "json"): Result<String> {
        val today = java.time.LocalDate.now()
        val startOfMonth = today.withDayOfMonth(1)
        return invoke(startOfMonth.toString(), today.toString(), format)
    }

    /**
     * Generate a custom report with specified data.
     */
    suspend fun custom(
        startDate: String,
        endDate: String,
        data: Map<String, Any>,
        format: String = "json"
    ): Result<String> {
        // This would use the report generator with custom data
        // For now, we use the repository
        return analyticsRepository.generateReport(startDate, endDate, format)
    }
}
