package com.orbits.feature.analytics

import com.orbits.domain.analytics.*

/**
 * UI state for the analytics feature.
 */
data class AnalyticsUiState(
    val isLoading: Boolean = true,
    val stats: ProductivityStats? = null,
    val trend: TaskCompletionTrend? = null,
    val categoryBreakdown: List<CategoryBreakdown> = emptyList(),
    val timeAllocation: List<TimeAllocation> = emptyList(),
    val insights: List<ProductivityInsight> = emptyList(),
    val focusTimeData: List<FocusTime> = emptyList(),
    val selectedPeriod: AnalyticsPeriod = AnalyticsPeriod.WEEK,
    val startDate: String = getDefaultStartDate(AnalyticsPeriod.WEEK),
    val endDate: String = java.time.LocalDate.now().toString(),
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false,
    val isReportGenerating: Boolean = false
)

/**
 * Analytics period options.
 */
enum class AnalyticsPeriod(val displayName: String, val days: Int) {
    TODAY("Today", 1),
    WEEK("This Week", 7),
    TWO_WEEKS("2 Weeks", 14),
    MONTH("This Month", 30),
    QUARTER("This Quarter", 90),
    YEAR("This Year", 365),
    CUSTOM("Custom", 0)
}

/**
 * Report format options.
 */
enum class ReportFormat(val displayName: String, val extension: String) {
    JSON("JSON", "json"),
    CSV("CSV", "csv"),
    PDF("PDF", "pdf")
}

/**
 * Get default start date for a period.
 */
fun getDefaultStartDate(period: AnalyticsPeriod): String {
    val now = java.time.LocalDate.now()
    return when (period) {
        AnalyticsPeriod.TODAY -> now.toString()
        AnalyticsPeriod.WEEK -> now.minusDays(6).toString()
        AnalyticsPeriod.TWO_WEEKS -> now.minusDays(13).toString()
        AnalyticsPeriod.MONTH -> now.minusDays(29).toString()
        AnalyticsPeriod.QUARTER -> now.minusDays(89).toString()
        AnalyticsPeriod.YEAR -> now.minusDays(364).toString()
        AnalyticsPeriod.CUSTOM -> now.minusDays(29).toString()
    }
}
