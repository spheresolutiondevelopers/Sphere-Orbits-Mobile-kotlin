package com.orbits.feature.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbits.core.common.Result
import com.orbits.core.common.Logger
import com.orbits.domain.analytics.AnalyticsRepository
import com.orbits.domain.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val analyticsRepository: AnalyticsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AnalyticsUiState())
    val state: StateFlow<AnalyticsUiState> = _state.asStateFlow()

    init {
        loadAnalytics()
    }

    fun handleEvent(event: AnalyticsEvent) {
        when (event) {
            AnalyticsEvent.LoadData -> loadAnalytics()
            AnalyticsEvent.Refresh -> refresh()
            is AnalyticsEvent.SelectPeriod -> selectPeriod(event.period)
            is AnalyticsEvent.SetDateRange -> setDateRange(event.startDate, event.endDate)
            AnalyticsEvent.DismissError -> dismissError()
            is AnalyticsEvent.GenerateReport -> generateReport(event.format)
            AnalyticsEvent.ExportData -> exportData()
        }
    }

    private fun loadAnalytics() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val startDate = _state.value.startDate
                val endDate = _state.value.endDate

                // Load all analytics data in parallel
                val statsDeferred = async { analyticsRepository.getProductivityStats(startDate, endDate) }
                val trendDeferred = async { analyticsRepository.getTaskCompletionTrend(startDate, endDate) }
                val categoryDeferred = async { analyticsRepository.getCategoryBreakdown(startDate, endDate) }
                val allocationDeferred = async { analyticsRepository.getTimeAllocation(startDate, endDate) }
                val focusDeferred = async { analyticsRepository.getFocusTime(startDate, endDate) }
                val dashboardDeferred = async { analyticsRepository.getDashboardStats() }

                val statsResult = statsDeferred.await()
                val trendResult = trendDeferred.await()
                val categoryResult = categoryDeferred.await()
                val allocationResult = allocationDeferred.await()
                val focusResult = focusDeferred.await()
                val dashboardResult = dashboardDeferred.await()

                val stats = when (statsResult) {
                    is Result.Success -> statsResult.data
                    else -> null
                }

                val trend = when (trendResult) {
                    is Result.Success -> trendResult.data
                    else -> null
                }

                val categories = when (categoryResult) {
                    is Result.Success -> categoryResult.data
                    else -> emptyList()
                }

                val allocation = when (allocationResult) {
                    is Result.Success -> allocationResult.data
                    else -> emptyList()
                }

                val focusData = when (focusResult) {
                    is Result.Success -> focusResult.data
                    else -> emptyList()
                }

                val insights = when (dashboardResult) {
                    is Result.Success -> dashboardResult.data.insights
                    else -> emptyList()
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        stats = stats,
                        trend = trend,
                        categoryBreakdown = categories,
                        timeAllocation = allocation,
                        insights = insights,
                        focusTimeData = focusData,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                Logger.e("Analytics", "Failed to load analytics data", e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load analytics data"
                    )
                }
            }
        }
    }

    private fun refresh() {
        _state.update { it.copy(isRefreshing = true) }
        loadAnalytics()
        _state.update { it.copy(isRefreshing = false) }
    }

    private fun selectPeriod(period: AnalyticsPeriod) {
        val startDate = if (period == AnalyticsPeriod.CUSTOM) {
            _state.value.startDate
        } else {
            getDefaultStartDate(period)
        }
        val endDate = LocalDate.now().toString()

        _state.update {
            it.copy(
                selectedPeriod = period,
                startDate = startDate,
                endDate = endDate
            )
        }
        loadAnalytics()
    }

    private fun setDateRange(startDate: String, endDate: String) {
        _state.update {
            it.copy(
                selectedPeriod = AnalyticsPeriod.CUSTOM,
                startDate = startDate,
                endDate = endDate
            )
        }
        loadAnalytics()
    }

    private fun dismissError() {
        _state.update { it.copy(errorMessage = null) }
    }

    private fun generateReport(format: ReportFormat) {
        _state.update { it.copy(isReportGenerating = true) }

        viewModelScope.launch {
            try {
                val result = analyticsRepository.generateReport(
                    startDate = _state.value.startDate,
                    endDate = _state.value.endDate,
                    format = format.name.lowercase()
                )

                when (result) {
                    is Result.Success -> {
                        Logger.d("Analytics", "Report generated: ${result.data}")
                        // In production, share/save the report
                    }
                    is Result.Error -> {
                        _state.update {
                            it.copy(
                                errorMessage = result.exception.message ?: "Failed to generate report"
                            )
                        }
                    }
                    Result.Loading -> { /* Ignore */ }
                }

                _state.update { it.copy(isReportGenerating = false) }
            } catch (e: Exception) {
                Logger.e("Analytics", "Failed to generate report", e)
                _state.update {
                    it.copy(
                        isReportGenerating = false,
                        errorMessage = e.message ?: "Failed to generate report"
                    )
                }
            }
        }
    }

    private fun exportData() {
        // In production, implement data export
        Logger.d("Analytics", "Export data requested")
    }
}
