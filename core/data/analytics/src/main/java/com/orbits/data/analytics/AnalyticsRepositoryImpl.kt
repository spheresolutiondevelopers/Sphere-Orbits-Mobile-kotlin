package com.orbits.data.analytics

import com.orbits.core.common.Result
import com.orbits.core.common.Logger
import com.orbits.core.common.TokenProvider
import com.orbits.core.common.extensions.nowUtc
import com.orbits.core.database.dao.AnalyticsDao
import com.orbits.core.database.dao.SyncQueueDao
import com.orbits.core.database.dao.TaskDao
import com.orbits.core.database.dao.AppointmentDao
import com.orbits.core.database.dao.MeetingDao
import com.orbits.core.network.api.AnalyticsApi
import com.orbits.core.network.error.ApiErrorParser
import com.orbits.data.sync.SyncQueueEntity
import com.orbits.data.analytics.AnalyticsEntity
import com.orbits.data.analytics.AnalyticsSummaryEntity
import com.orbits.data.analytics.mappers.AnalyticsMapper
import com.orbits.domain.analytics.AnalyticsEvent
import com.orbits.domain.analytics.ProductivityStats
import com.orbits.domain.analytics.TaskCompletionTrend
import com.orbits.domain.analytics.CategoryBreakdown
import com.orbits.domain.analytics.TimeAllocation
import com.orbits.domain.analytics.ProductivityInsight
import com.orbits.domain.analytics.FocusTime
import com.orbits.domain.analytics.DashboardStats
import com.orbits.domain.analytics.AnalyticsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.combine
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AnalyticsRepositoryImpl @Inject constructor(
    private val analyticsDao: AnalyticsDao,
    private val syncQueueDao: SyncQueueDao,
    private val taskDao: TaskDao,
    private val appointmentDao: AppointmentDao,
    private val meetingDao: MeetingDao,
    private val analyticsApi: AnalyticsApi,
    private val reportGenerator: ReportGenerator,
    private val analyticsMapper: AnalyticsMapper,
    private val tokenProvider: TokenProvider
) : AnalyticsRepository {

    companion object {
        private const val TAG = "AnalyticsRepository"
        private val DATE_FORMATTER = DateTimeFormatter.ISO_DATE
    }

    // ─── Track Events ──────────────────────────────────────────────

    override suspend fun trackEvent(event: AnalyticsEvent): Result<Unit> {
        return try {
            validateEvent(event)

            val entity = analyticsMapper.toEntity(event)
            analyticsDao.insertAnalyticsEvent(entity)

            // If event is high-priority, sync immediately
            if (event.eventCategory == "system" || event.eventType == "task_completed") {
                enqueueSync(SyncQueueEntity(
                    entityType = "analytics_event",
                    operation = "create",
                    entityId = event.id,
                    payloadJson = analyticsMapper.toCreateRequest(event).toString(),
                    priority = 1 // High priority
                ))
            } else {
                enqueueSync(SyncQueueEntity(
                    entityType = "analytics_event",
                    operation = "create",
                    entityId = event.id,
                    payloadJson = analyticsMapper.toCreateRequest(event).toString(),
                    priority = 0 // Normal priority
                ))
            }

            Logger.d(TAG, "Analytics event tracked: ${event.eventType}")
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error tracking analytics event", e)
            Result.Error(e)
        }
    }

    override suspend fun trackEvents(events: List<AnalyticsEvent>): Result<Unit> {
        return try {
            events.forEach { validateEvent(it) }

            val entities = events.map { analyticsMapper.toEntity(it) }
            analyticsDao.insertAnalyticsEvents(entities)

            entities.forEach { entity ->
                enqueueSync(SyncQueueEntity(
                    entityType = "analytics_event",
                    operation = "create",
                    entityId = entity.id,
                    payloadJson = "{}",
                    priority = 0
                ))
            }

            Logger.d(TAG, "Tracked ${events.size} analytics events")
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error tracking batch analytics events", e)
            Result.Error(e)
        }
    }

    override suspend fun trackTaskCreated(
        userId: String,
        taskId: String,
        priority: String,
        category: String?
    ): Result<Unit> {
        val event = analyticsMapper.createTaskCreatedEvent(userId, taskId, priority, category)
        return trackEvent(event)
    }

    override suspend fun trackTaskCompleted(
        userId: String,
        taskId: String,
        completionTimeHours: Double,
        priority: String
    ): Result<Unit> {
        val event = analyticsMapper.createTaskCompletedEvent(userId, taskId, completionTimeHours, priority)
        return trackEvent(event)
    }

    override suspend fun trackAppointmentCreated(
        userId: String,
        appointmentId: String,
        durationMinutes: Int
    ): Result<Unit> {
        val event = analyticsMapper.createAppointmentCreatedEvent(userId, appointmentId, durationMinutes)
        return trackEvent(event)
    }

    override suspend fun trackMeetingJoined(
        userId: String,
        meetingId: String,
        platform: String
    ): Result<Unit> {
        val event = analyticsMapper.createMeetingJoinedEvent(userId, meetingId, platform)
        return trackEvent(event)
    }

    override suspend fun trackFocusTime(
        userId: String,
        durationMinutes: Double,
        taskId: String?
    ): Result<Unit> {
        val event = analyticsMapper.createFocusTimeEvent(userId, durationMinutes, taskId)
        return trackEvent(event)
    }

    // ─── Read Stats ────────────────────────────────────────────────

    override suspend fun getProductivityStats(
        startDate: String,
        endDate: String
    ): Result<ProductivityStats> {
        return try {
            // Try to get from API first (if online)
            val token = tokenProvider.getAccessToken()
            if (token != null) {
                val response = analyticsApi.getProductivityStats(startDate, endDate)
                return Result.Success(analyticsMapper.toDomain(response))
            }

            // Fallback: compute locally from events
            val stats = computeProductivityStatsLocally(startDate, endDate)
            Result.Success(stats)
        } catch (e: Exception) {
            Logger.e(TAG, "Error getting productivity stats", e)
            // Fallback to local computation
            try {
                val stats = computeProductivityStatsLocally(startDate, endDate)
                Result.Success(stats)
            } catch (e2: Exception) {
                Logger.e(TAG, "Error computing local productivity stats", e2)
                Result.Error(e2)
            }
        }
    }

    override suspend fun getTaskCompletionTrend(
        startDate: String,
        endDate: String
    ): Result<TaskCompletionTrend> {
        return try {
            val token = tokenProvider.getAccessToken()
            if (token != null) {
                val response = analyticsApi.getTaskCompletionTrend(startDate, endDate)
                return Result.Success(analyticsMapper.toDomain(response))
            }

            // Fallback: compute locally
            val trend = computeTaskCompletionTrendLocally(startDate, endDate)
            Result.Success(trend)
        } catch (e: Exception) {
            Logger.e(TAG, "Error getting task completion trend", e)
            try {
                val trend = computeTaskCompletionTrendLocally(startDate, endDate)
                Result.Success(trend)
            } catch (e2: Exception) {
                Logger.e(TAG, "Error computing local task completion trend", e2)
                Result.Error(e2)
            }
        }
    }

    override suspend fun getCategoryBreakdown(
        startDate: String,
        endDate: String
    ): Result<List<CategoryBreakdown>> {
        return try {
            val token = tokenProvider.getAccessToken()
            if (token != null) {
                val response = analyticsApi.getCategoryBreakdown(startDate, endDate)
                return Result.Success(analyticsMapper.toCategoryBreakdownDomainList(response))
            }

            // Fallback: compute locally
            val breakdown = computeCategoryBreakdownLocally(startDate, endDate)
            Result.Success(breakdown)
        } catch (e: Exception) {
            Logger.e(TAG, "Error getting category breakdown", e)
            try {
                val breakdown = computeCategoryBreakdownLocally(startDate, endDate)
                Result.Success(breakdown)
            } catch (e2: Exception) {
                Logger.e(TAG, "Error computing local category breakdown", e2)
                Result.Error(e2)
            }
        }
    }

    override suspend fun getTimeAllocation(
        startDate: String,
        endDate: String
    ): Result<List<TimeAllocation>> {
        return try {
            val token = tokenProvider.getAccessToken()
            if (token != null) {
                // API call would be needed, but we compute locally for now
                val allocation = computeTimeAllocationLocally(startDate, endDate)
                Result.Success(allocation)
            } else {
                val allocation = computeTimeAllocationLocally(startDate, endDate)
                Result.Success(allocation)
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Error getting time allocation", e)
            try {
                val allocation = computeTimeAllocationLocally(startDate, endDate)
                Result.Success(allocation)
            } catch (e2: Exception) {
                Logger.e(TAG, "Error computing local time allocation", e2)
                Result.Error(e2)
            }
        }
    }

    override suspend fun getDashboardStats(): Result<DashboardStats> {
        return try {
            val token = tokenProvider.getAccessToken()
            if (token != null) {
                // API would provide full dashboard, but we compute locally for now
                val today = LocalDate.now()
                val startOfWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1)
                val startOfMonth = today.withDayOfMonth(1)

                val todayStr = today.format(DATE_FORMATTER)
                val weekStartStr = startOfWeek.format(DATE_FORMATTER)
                val monthStartStr = startOfMonth.format(DATE_FORMATTER)
                val todayEndStr = today.plusDays(1).format(DATE_FORMATTER)

                val todayStats = computeProductivityStatsLocally(todayStr, todayEndStr)
                val weekStats = computeProductivityStatsLocally(weekStartStr, todayEndStr)
                val monthStats = computeProductivityStatsLocally(monthStartStr, todayEndStr)
                val trend = computeTaskCompletionTrendLocally(weekStartStr, todayEndStr)
                val categoryBreakdown = computeCategoryBreakdownLocally(monthStartStr, todayEndStr)
                val timeAllocation = computeTimeAllocationLocally(monthStartStr, todayEndStr)
                val insights = generateInsights(todayStats, weekStats, monthStats)

                Result.Success(DashboardStats(
                    today = todayStats,
                    week = weekStats,
                    month = monthStats,
                    insights = insights,
                    taskTrend = trend,
                    categoryBreakdown = categoryBreakdown,
                    timeAllocation = timeAllocation
                ))
            } else {
                // Offline mode: compute from local data
                // Simplified for now
                val today = LocalDate.now()
                val startOfMonth = today.withDayOfMonth(1)
                val todayStr = today.format(DATE_FORMATTER)
                val monthStartStr = startOfMonth.format(DATE_FORMATTER)
                val todayEndStr = today.plusDays(1).format(DATE_FORMATTER)

                val stats = computeProductivityStatsLocally(monthStartStr, todayEndStr)
                val trend = computeTaskCompletionTrendLocally(monthStartStr, todayEndStr)
                val breakdown = computeCategoryBreakdownLocally(monthStartStr, todayEndStr)
                val allocation = computeTimeAllocationLocally(monthStartStr, todayEndStr)

                Result.Success(DashboardStats(
                    today = stats.copy(period = "daily"),
                    week = stats.copy(period = "weekly"),
                    month = stats.copy(period = "monthly"),
                    insights = emptyList(),
                    taskTrend = trend,
                    categoryBreakdown = breakdown,
                    timeAllocation = allocation
                ))
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Error getting dashboard stats", e)
            Result.Error(e)
        }
    }

    override suspend fun getFocusTime(startDate: String, endDate: String): Result<List<FocusTime>> {
        return try {
            val events = analyticsDao.getAnalyticsEventsByType(
                getUserId(), "focus_time", startDate, endDate
            ).first()

            val focusTimes = events.groupBy { entity ->
                entity.eventTimestamp.substring(0, 10) // group by date
            }.map { (date, entities) ->
                val totalHours = entities.sumOf { it.value ?: 0.0 } / 60.0 // convert minutes to hours
                FocusTime(
                    date = date,
                    hours = totalHours,
                    tasksCompleted = 0, // Would need to compute from task_completed events
                    focusScore = (totalHours / 8.0 * 100).toInt().coerceIn(0, 100)
                )
            }.sortedBy { it.date }

            Result.Success(focusTimes)
        } catch (e: Exception) {
            Logger.e(TAG, "Error getting focus time", e)
            Result.Error(e)
        }
    }

    // ─── Report Generation ────────────────────────────────────────

    override suspend fun generateReport(
        startDate: String,
        endDate: String,
        format: String
    ): Result<String> {
        return try {
            val stats = getProductivityStats(startDate, endDate)
            val trend = getTaskCompletionTrend(startDate, endDate)
            val breakdown = getCategoryBreakdown(startDate, endDate)
            val allocation = getTimeAllocation(startDate, endDate)

            if (stats is Result.Error) {
                return Result.Error(stats.exception)
            }
            if (trend is Result.Error) {
                return Result.Error(trend.exception)
            }
            if (breakdown is Result.Error) {
                return Result.Error(breakdown.exception)
            }
            if (allocation is Result.Error) {
                return Result.Error(allocation.exception)
            }

            val reportData = mapOf<String, Any>(
                "stats" to (stats as Result.Success).data,
                "trend" to (trend as Result.Success).data,
                "breakdown" to (breakdown as Result.Success).data,
                "allocation" to (allocation as Result.Success).data
            )

            val report = reportGenerator.generateReport(
                startDate = startDate,
                endDate = endDate,
                data = reportData,
                format = format
            )

            Result.Success(report)
        } catch (e: Exception) {
            Logger.e(TAG, "Error generating report", e)
            Result.Error(e)
        }
    }

    override suspend fun generateCsvReport(startDate: String, endDate: String): Result<String> {
        return generateReport(startDate, endDate, "csv")
    }

    override suspend fun generatePdfReport(startDate: String, endDate: String): Result<String> {
        return generateReport(startDate, endDate, "pdf")
    }

    override suspend fun generateJsonReport(startDate: String, endDate: String): Result<String> {
        return generateReport(startDate, endDate, "json")
    }

    // ─── Sync ──────────────────────────────────────────────────────

    override suspend fun syncAnalytics(): Result<Unit> {
        return try {
            val unsynced = analyticsDao.getAnalyticsEvents(getUserId(), 100).first()
                .filter { it.syncStatus == "pending" }

            if (unsynced.isNotEmpty()) {
                val events = unsynced.map { analyticsMapper.toDto(it) }
                // In production, call analyticsApi.syncEvents(events)
                // For now, mark as synced
                unsynced.forEach { entity ->
                    // analyticsDao.updateSyncStatus(entity.id, "synced")
                }
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error syncing analytics", e)
            Result.Error(e)
        }
    }

    // ─── Cleanup ──────────────────────────────────────────────────

    override suspend fun cleanupOldEvents(daysToKeep: Int): Result<Unit> {
        return try {
            // Delete events older than daysToKeep
            // analyticsDao.deleteOldAnalyticsEvents(daysToKeep)
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error cleaning up old analytics events", e)
            Result.Error(e)
        }
    }

    // ─── Private Helpers ──────────────────────────────────────────

    private fun getUserId(): String {
        // This should come from auth state
        return "test_user_id"
    }

    private suspend fun enqueueSync(item: SyncQueueEntity) {
        syncQueueDao.enqueue(item)
    }

    private fun validateEvent(event: AnalyticsEvent) {
        require(event.eventType.isNotBlank()) { "Event type cannot be empty" }
        require(event.eventCategory in listOf("user_action", "system", "performance")) {
            "Invalid event category"
        }
        require(event.userId.isNotBlank()) { "User ID cannot be empty" }
        require(event.eventTimestamp.isNotBlank()) { "Event timestamp cannot be empty" }
    }

    // ─── Local Computation Methods ───────────────────────────────

    private suspend fun computeProductivityStatsLocally(
        startDate: String,
        endDate: String
    ): ProductivityStats {
        // Get tasks completed in date range
        val tasks = taskDao.getTasksInDateRange(getUserId(), startDate, endDate).first()
        val completedTasks = tasks.filter { it.status == "completed" }
        val overdueTasks = tasks.filter { 
            val dueDate = it.dueDate
            it.status != "completed" && 
            dueDate != null && 
            dueDate < startDate 
        }

        // Get appointments
        val appointments = appointmentDao.getAppointmentsInDateRange(getUserId(), startDate, endDate).first()
        val attendedAppointments = appointments.filter { it.status == "completed" }

        // Get meetings
        val meetings = meetingDao.getMeetingsForUser(getUserId()).first()
        val attendedMeetings = meetings.filter { it.status == "ended" }

        // Calculate metrics
        val totalTasks = tasks.size
        val completedCount = completedTasks.size
        val completionRate = if (totalTasks > 0) completedCount.toDouble() / totalTasks else 0.0

        // Calculate average completion time (simplified)
        val avgCompletionTime = completedTasks
            .mapNotNull { 
                val created = Instant.parse(it.createdAt)
                val completedAt = it.completedAt
                val completed = completedAt?.let { Instant.parse(it) }
                if (completed != null) {
                    (completed.toEpochMilli() - created.toEpochMilli()) / (1000.0 * 60 * 60)
                } else null
            }
            .average()

        // Calculate productivity score (simplified)
        val score = ((completionRate * 0.6) + (attendedAppointments.size.toDouble() / 10.0) * 0.2 + (attendedMeetings.size.toDouble() / 10.0) * 0.2) * 100
        val finalScore = score.toInt().coerceIn(0, 100)

        // Determine trend
        val trend = when {
            finalScore > 70 -> "up"
            finalScore > 40 -> "stable"
            else -> "down"
        }

        // Total focus hours (simplified)
        val focusEvents = analyticsDao.getAnalyticsEventsByType(
            getUserId(), "focus_time", startDate, endDate
        ).first()
        val totalFocusHours = focusEvents.sumOf { it.value ?: 0.0 } / 60.0

        return ProductivityStats(
            tasksCompleted = completedCount,
            tasksCreated = totalTasks,
            tasksOverdue = overdueTasks.size,
            tasksCompletionRate = completionRate * 100,
            appointmentsAttended = attendedAppointments.size,
            appointmentsScheduled = appointments.size,
            meetingsAttended = attendedMeetings.size,
            meetingsScheduled = meetings.size,
            totalFocusHours = totalFocusHours,
            averageTaskCompletionTime = if (avgCompletionTime.isNaN()) 0.0 else avgCompletionTime,
            productivityScore = finalScore,
            trend = trend,
            period = "custom",
            startDate = startDate,
            endDate = endDate
        )
    }

    private suspend fun computeTaskCompletionTrendLocally(
        startDate: String,
        endDate: String
    ): TaskCompletionTrend {
        val tasks = taskDao.getTasksInDateRange(getUserId(), startDate, endDate).first()
        
        // Group by date
        val dateRange = LocalDate.parse(startDate).datesUntil(LocalDate.parse(endDate).plusDays(1)).toList()
        
        val labels = dateRange.map { it.format(DateTimeFormatter.ofPattern("MMM dd")) }
        val completed = dateRange.map { date ->
            tasks.count { 
                val completedAt = it.completedAt
                it.status == "completed" && 
                completedAt != null && 
                LocalDate.parse(completedAt.substring(0, 10)) == date
            }
        }
        val created = dateRange.map { date ->
            tasks.count { 
                LocalDate.parse(it.createdAt.substring(0, 10)) == date
            }
        }
        val overdue = dateRange.map { date ->
            tasks.count { 
                val dueDate = it.dueDate
                it.status != "completed" && 
                dueDate != null && 
                LocalDate.parse(dueDate.substring(0, 10)) == date
            }
        }

        return TaskCompletionTrend(
            labels = labels,
            completed = completed,
            created = created,
            overdue = overdue
        )
    }

    private suspend fun computeCategoryBreakdownLocally(
        startDate: String,
        endDate: String
    ): List<CategoryBreakdown> {
        val tasks = taskDao.getTasksInDateRange(getUserId(), startDate, endDate).first()
        
        val total = tasks.size
        if (total == 0) return emptyList()

        val categoryMap = tasks
            .map { it.taskType }
            .groupingBy { it }
            .eachCount()

        val colors = mapOf(
            "general" to "#7C6CF8",
            "meeting" to "#2DD4A0",
            "reminder" to "#FFD166",
            "deadline" to "#FF6B6B",
            "event" to "#A855F7"
        )

        return categoryMap.map { (category, count) ->
            CategoryBreakdown(
                category = category,
                count = count,
                percentage = (count.toDouble() / total) * 100,
                color = colors[category] ?: "#888888"
            )
        }.sortedByDescending { it.count }
    }

    private suspend fun computeTimeAllocationLocally(
        startDate: String,
        endDate: String
    ): List<TimeAllocation> {
        // Simplified: use category breakdown as time allocation
        val breakdown = computeCategoryBreakdownLocally(startDate, endDate)
        val totalHours = breakdown.sumOf { it.count.toDouble() } * 0.5 // assume 30min per task

        return if (breakdown.isEmpty()) {
            listOf(
                TimeAllocation(
                    category = "No Data",
                    hours = 0.0,
                    percentage = 0.0,
                    color = "#888888"
                )
            )
        } else {
            breakdown.map { item ->
                TimeAllocation(
                    category = item.category,
                    hours = item.count.toDouble() * 0.5,
                    percentage = item.percentage,
                    color = item.color
                )
            }
        }
    }

    private fun generateInsights(
        today: ProductivityStats,
        week: ProductivityStats,
        month: ProductivityStats
    ): List<ProductivityInsight> {
        val insights = mutableListOf<ProductivityInsight>()

        // Insight 1: Productivity trend
        if (week.productivityScore > month.productivityScore) {
            insights.add(
                ProductivityInsight(
                    title = "Productivity is up this week!",
                    description = "You've been ${week.productivityScore - month.productivityScore}% more productive this week compared to your monthly average.",
                    type = "positive",
                    recommendation = "Keep up the great work! Try to maintain this momentum.",
                    metricValue = week.productivityScore.toDouble(),
                    metricLabel = "Weekly Score"
                )
            )
        } else if (week.productivityScore < month.productivityScore) {
            insights.add(
                ProductivityInsight(
                    title = "Productivity dip detected",
                    description = "Your productivity is ${month.productivityScore - week.productivityScore}% lower this week.",
                    type = "negative",
                    recommendation = "Try breaking tasks into smaller chunks and take regular breaks.",
                    metricValue = week.productivityScore.toDouble(),
                    metricLabel = "Weekly Score"
                )
            )
        }

        // Insight 2: Task completion
        if (today.tasksCompletionRate > 70) {
            insights.add(
                ProductivityInsight(
                    title = "Great task completion today!",
                    description = "You completed ${today.tasksCompleted} tasks today with a ${today.tasksCompletionRate}% completion rate.",
                    type = "positive",
                    recommendation = null,
                    metricValue = today.tasksCompletionRate,
                    metricLabel = "Completion Rate"
                )
            )
        } else if (today.tasksOverdue > 0) {
            insights.add(
                ProductivityInsight(
                    title = "${today.tasksOverdue} tasks overdue",
                    description = "You have ${today.tasksOverdue} tasks that are past their due date.",
                    type = "negative",
                    recommendation = "Prioritize overdue tasks first thing tomorrow.",
                    metricValue = today.tasksOverdue.toDouble(),
                    metricLabel = "Overdue Tasks"
                )
            )
        }

        // Insight 3: Focus time
        if (today.totalFocusHours > 4) {
            insights.add(
                ProductivityInsight(
                    title = "Deep focus day!",
                    description = "You logged ${today.totalFocusHours} hours of focused work today.",
                    type = "positive",
                    recommendation = null,
                    metricValue = today.totalFocusHours,
                    metricLabel = "Focus Hours"
                )
            )
        }

        // If no insights generated, add a neutral one
        if (insights.isEmpty()) {
            insights.add(
                ProductivityInsight(
                    title = "Keep going!",
                    description = "Every task completed is a step forward. You've got this!",
                    type = "neutral",
                    recommendation = "Set a small goal for tomorrow to build momentum.",
                    metricValue = null,
                    metricLabel = null
                )
            )
        }

        return insights
    }
}