package com.orbits.feature.analytics

import com.orbits.domain.analytics.*

/**
 * UI events for the analytics feature.
 */
sealed class AnalyticsEvent {
    data object LoadData : AnalyticsEvent()
    data object Refresh : AnalyticsEvent()
    data class SelectPeriod(val period: AnalyticsPeriod) : AnalyticsEvent()
    data class SetDateRange(val startDate: String, val endDate: String) : AnalyticsEvent()
    data object DismissError : AnalyticsEvent()
    data class GenerateReport(val format: ReportFormat) : AnalyticsEvent()
    data object ExportData : AnalyticsEvent()
}
