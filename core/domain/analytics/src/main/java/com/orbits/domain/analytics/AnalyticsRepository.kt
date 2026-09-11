package com.orbits.domain.analytics

import com.orbits.core.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Analytics repository interface.
 * Defines all analytics operations.
 * Implemented by :core:data:analytics.
 */
interface AnalyticsRepository {

    // ─── Track Events ─────────────────────────────────────────────

    /**
     * Track a single analytics event.
     */
    suspend fun trackEvent(event: AnalyticsEvent): Result<Unit>

    /**
     * Track multiple analytics events.
     */
    suspend fun trackEvents(events: List<AnalyticsEvent>): Result<Unit>

    /**
     * Track a task creation event.
     */
    suspend fun trackTaskCreated(
        userId: String,
        taskId: String,
        priority: String,
        category: String?
    ): Result<Unit>

    /**
     * Track a task completion event.
     */
    suspend fun trackTaskCompleted(
        userId: String,
        taskId: String,
        completionTimeHours: Double,
        priority: String
    ): Result<Unit>

    /**
     * Track an appointment creation event.
     */
    suspend fun trackAppointmentCreated(
        userId: String,
        appointmentId: String,
        durationMinutes: Int
    ): Result<Unit>

    /**
     * Track a meeting joined event.
     */
    suspend fun trackMeetingJoined(
        userId: String,
        meetingId: String,
        platform: String
    ): Result<Unit>

    /**
     * Track focus time.
     */
    suspend fun trackFocusTime(
        userId: String,
        durationMinutes: Double,
        taskId: String?
    ): Result<Unit>

    // ─── Read Stats ───────────────────────────────────────────────

    /**
     * Get productivity statistics for a date range.
     */
    suspend fun getProductivityStats(
        startDate: String,
        endDate: String
    ): Result<ProductivityStats>

    /**
     * Get task completion trend for a date range.
     */
    suspend fun getTaskCompletionTrend(
        startDate: String,
        endDate: String
    ): Result<TaskCompletionTrend>

    /**
     * Get category breakdown for a date range.
     */
    suspend fun getCategoryBreakdown(
        startDate: String,
        endDate: String
    ): Result<List<CategoryBreakdown>>

    /**
     * Get time allocation for a date range.
     */
    suspend fun getTimeAllocation(
        startDate: String,
        endDate: String
    ): Result<List<TimeAllocation>>

    /**
     * Get complete dashboard stats.
     */
    suspend fun getDashboardStats(): Result<DashboardStats>

    /**
     * Get focus time data for a date range.
     */
    suspend fun getFocusTime(
        startDate: String,
        endDate: String
    ): Result<List<FocusTime>>

    // ─── Reports ──────────────────────────────────────────────────

    /**
     * Generate a report in the specified format.
     */
    suspend fun generateReport(
        startDate: String,
        endDate: String,
        format: String
    ): Result<String>

    /**
     * Generate a CSV report.
     */
    suspend fun generateCsvReport(
        startDate: String,
        endDate: String
    ): Result<String>

    /**
     * Generate a PDF report.
     */
    suspend fun generatePdfReport(
        startDate: String,
        endDate: String
    ): Result<String>

    /**
     * Generate a JSON report.
     */
    suspend fun generateJsonReport(
        startDate: String,
        endDate: String
    ): Result<String>

    // ─── Sync ──────────────────────────────────────────────────────

    /**
     * Sync analytics events with the server.
     */
    suspend fun syncAnalytics(): Result<Unit>

    /**
     * Clean up old analytics events.
     */
    suspend fun cleanupOldEvents(daysToKeep: Int): Result<Unit>
}
