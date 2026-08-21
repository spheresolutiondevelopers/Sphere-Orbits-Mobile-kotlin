package com.orbits.data.analytics.mappers

import com.orbits.core.common.extensions.nowUtc
import com.orbits.data.analytics.local.AnalyticsEntity
import com.orbits.data.analytics.local.AnalyticsSummaryEntity
import com.orbits.data.analytics.remote.AnalyticsEventDto
import com.orbits.data.analytics.remote.CreateAnalyticsEventRequest
import com.orbits.data.analytics.remote.ProductivityStatsDto
import com.orbits.data.analytics.remote.TaskCompletionTrendDto
import com.orbits.data.analytics.remote.CategoryBreakdownDto
import com.orbits.data.analytics.remote.TimeAllocationDto
import com.orbits.data.analytics.remote.ProductivityInsightDto
import com.orbits.data.analytics.remote.FocusTimeDto
import com.orbits.domain.analytics.AnalyticsEvent
import com.orbits.domain.analytics.ProductivityStats
import com.orbits.domain.analytics.TaskCompletionTrend
import com.orbits.domain.analytics.CategoryBreakdown
import com.orbits.domain.analytics.TimeAllocation
import com.orbits.domain.analytics.ProductivityInsight
import com.orbits.domain.analytics.FocusTime
import com.orbits.domain.analytics.DashboardStats
import kotlinx.serialization.json.Json
import javax.inject.Inject

internal class AnalyticsMapper @Inject constructor() {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    // ─── Event: Entity ↔ Domain ───────────────────────────────────

    fun toDomain(entity: AnalyticsEntity): AnalyticsEvent {
        return AnalyticsEvent(
            id = entity.id,
            userId = entity.userId,
            eventType = entity.eventType,
            eventCategory = entity.eventCategory,
            eventSubtype = entity.eventSubtype,
            entityId = entity.entityId,
            entityType = entity.entityType,
            value = entity.value,
            metadata = if (entity.metadata != null) {
                try {
                    json.decodeFromString<Map<String, String>>(entity.metadata)
                } catch (e: Exception) {
                    emptyMap()
                }
            } else emptyMap(),
            sessionId = entity.sessionId,
            eventTimestamp = entity.eventTimestamp,
            createdAt = entity.createdAt
        )
    }

    fun toEntity(domain: AnalyticsEvent): AnalyticsEntity {
        return AnalyticsEntity(
            id = domain.id,
            userId = domain.userId,
            eventType = domain.eventType,
            eventCategory = domain.eventCategory,
            eventSubtype = domain.eventSubtype,
            entityId = domain.entityId,
            entityType = domain.entityType,
            value = domain.value,
            metadata = if (domain.metadata.isNotEmpty()) {
                json.encodeToString(domain.metadata)
            } else null,
            sessionId = domain.sessionId,
            eventTimestamp = domain.eventTimestamp,
            createdAt = domain.createdAt,
            syncedAt = null,
            syncStatus = "pending"
        )
    }

    // ─── Event: DTO ↔ Entity ─────────────────────────────────────

    fun toEntity(dto: AnalyticsEventDto): AnalyticsEntity {
        return AnalyticsEntity(
            id = dto.id,
            userId = dto.userId,
            eventType = dto.eventType,
            eventCategory = dto.eventCategory,
            eventSubtype = dto.eventSubtype,
            entityId = dto.entityId,
            entityType = dto.entityType,
            value = dto.value,
            metadata = dto.metadata?.let { json.encodeToString(it) },
            sessionId = dto.sessionId,
            eventTimestamp = dto.eventTimestamp,
            createdAt = dto.createdAt,
            syncedAt = null,
            syncStatus = "pending"
        )
    }

    fun toDto(entity: AnalyticsEntity): AnalyticsEventDto {
        return AnalyticsEventDto(
            id = entity.id,
            userId = entity.userId,
            eventType = entity.eventType,
            eventCategory = entity.eventCategory,
            eventSubtype = entity.eventSubtype,
            entityId = entity.entityId,
            entityType = entity.entityType,
            value = entity.value,
            metadata = entity.metadata?.let {
                try {
                    json.decodeFromString<Map<String, String>>(it)
                } catch (e: Exception) {
                    null
                }
            },
            sessionId = entity.sessionId,
            eventTimestamp = entity.eventTimestamp,
            createdAt = entity.createdAt
        )
    }

    fun toCreateRequest(domain: AnalyticsEvent): CreateAnalyticsEventRequest {
        return CreateAnalyticsEventRequest(
            eventType = domain.eventType,
            eventCategory = domain.eventCategory,
            eventSubtype = domain.eventSubtype,
            entityId = domain.entityId,
            entityType = domain.entityType,
            value = domain.value,
            metadata = domain.metadata,
            sessionId = domain.sessionId
        )
    }

    // ─── Productivity Stats: DTO ↔ Domain ────────────────────────

    fun toDomain(dto: ProductivityStatsDto): ProductivityStats {
        return ProductivityStats(
            tasksCompleted = dto.tasksCompleted,
            tasksCreated = dto.tasksCreated,
            tasksOverdue = dto.tasksOverdue,
            tasksCompletionRate = dto.tasksCompletionRate,
            appointmentsAttended = dto.appointmentsAttended,
            appointmentsScheduled = dto.appointmentsScheduled,
            meetingsAttended = dto.meetingsAttended,
            meetingsScheduled = dto.meetingsScheduled,
            totalFocusHours = dto.totalFocusHours,
            averageTaskCompletionTime = dto.averageTaskCompletionTime,
            productivityScore = dto.productivityScore,
            trend = dto.trend,
            period = dto.period,
            startDate = dto.startDate,
            endDate = dto.endDate
        )
    }

    fun toDto(domain: ProductivityStats): ProductivityStatsDto {
        return ProductivityStatsDto(
            tasksCompleted = domain.tasksCompleted,
            tasksCreated = domain.tasksCreated,
            tasksOverdue = domain.tasksOverdue,
            tasksCompletionRate = domain.tasksCompletionRate,
            appointmentsAttended = domain.appointmentsAttended,
            appointmentsScheduled = domain.appointmentsScheduled,
            meetingsAttended = domain.meetingsAttended,
            meetingsScheduled = domain.meetingsScheduled,
            totalFocusHours = domain.totalFocusHours,
            averageTaskCompletionTime = domain.averageTaskCompletionTime,
            productivityScore = domain.productivityScore,
            trend = domain.trend,
            period = domain.period,
            startDate = domain.startDate,
            endDate = domain.endDate
        )
    }

    // ─── Task Completion Trend: DTO ↔ Domain ─────────────────────

    fun toDomain(dto: TaskCompletionTrendDto): TaskCompletionTrend {
        return TaskCompletionTrend(
            labels = dto.labels,
            completed = dto.completed,
            created = dto.created,
            overdue = dto.overdue
        )
    }

    fun toDto(domain: TaskCompletionTrend): TaskCompletionTrendDto {
        return TaskCompletionTrendDto(
            labels = domain.labels,
            completed = domain.completed,
            created = domain.created,
            overdue = domain.overdue
        )
    }

    // ─── Category Breakdown: DTO ↔ Domain ────────────────────────

    fun toDomain(dto: CategoryBreakdownDto): CategoryBreakdown {
        return CategoryBreakdown(
            category = dto.category,
            count = dto.count,
            percentage = dto.percentage,
            color = dto.color
        )
    }

    fun toDto(domain: CategoryBreakdown): CategoryBreakdownDto {
        return CategoryBreakdownDto(
            category = domain.category,
            count = domain.count,
            percentage = domain.percentage,
            color = domain.color
        )
    }

    fun toDomainList(dtos: List<CategoryBreakdownDto>): List<CategoryBreakdown> {
        return dtos.map { toDomain(it) }
    }

    fun toDtoList(domains: List<CategoryBreakdown>): List<CategoryBreakdownDto> {
        return domains.map { toDto(it) }
    }

    // ─── Time Allocation: DTO ↔ Domain ───────────────────────────

    fun toDomain(dto: TimeAllocationDto): TimeAllocation {
        return TimeAllocation(
            category = dto.category,
            hours = dto.hours,
            percentage = dto.percentage,
            color = dto.color
        )
    }

    fun toDto(domain: TimeAllocation): TimeAllocationDto {
        return TimeAllocationDto(
            category = domain.category,
            hours = domain.hours,
            percentage = domain.percentage,
            color = domain.color
        )
    }

    fun toDomainList(dtos: List<TimeAllocationDto>): List<TimeAllocation> {
        return dtos.map { toDomain(it) }
    }

    fun toDtoList(domains: List<TimeAllocation>): List<TimeAllocationDto> {
        return domains.map { toDto(it) }
    }

    // ─── Productivity Insights: DTO ↔ Domain ─────────────────────

    fun toDomain(dto: ProductivityInsightDto): ProductivityInsight {
        return ProductivityInsight(
            title = dto.title,
            description = dto.description,
            type = dto.type,
            recommendation = dto.recommendation,
            metricValue = dto.metricValue,
            metricLabel = dto.metricLabel
        )
    }

    fun toDto(domain: ProductivityInsight): ProductivityInsightDto {
        return ProductivityInsightDto(
            title = domain.title,
            description = domain.description,
            type = domain.type,
            recommendation = domain.recommendation,
            metricValue = domain.metricValue,
            metricLabel = domain.metricLabel
        )
    }

    fun toDomainList(dtos: List<ProductivityInsightDto>): List<ProductivityInsight> {
        return dtos.map { toDomain(it) }
    }

    fun toDtoList(domains: List<ProductivityInsight>): List<ProductivityInsightDto> {
        return domains.map { toDto(it) }
    }

    // ─── Focus Time: DTO ↔ Domain ────────────────────────────────

    fun toDomain(dto: FocusTimeDto): FocusTime {
        return FocusTime(
            date = dto.date,
            hours = dto.hours,
            tasksCompleted = dto.tasksCompleted,
            focusScore = dto.focusScore
        )
    }

    fun toDto(domain: FocusTime): FocusTimeDto {
        return FocusTimeDto(
            date = domain.date,
            hours = domain.hours,
            tasksCompleted = domain.tasksCompleted,
            focusScore = domain.focusScore
        )
    }

    fun toDomainList(dtos: List<FocusTimeDto>): List<FocusTime> {
        return dtos.map { toDomain(it) }
    }

    fun toDtoList(domains: List<FocusTime>): List<FocusTimeDto> {
        return domains.map { toDto(it) }
    }

    // ─── Dashboard Stats: Aggregate ──────────────────────────────

    fun toDomain(
        today: ProductivityStatsDto,
        week: ProductivityStatsDto,
        month: ProductivityStatsDto,
        insights: List<ProductivityInsightDto>,
        taskTrend: TaskCompletionTrendDto,
        categoryBreakdown: List<CategoryBreakdownDto>,
        timeAllocation: List<TimeAllocationDto>
    ): DashboardStats {
        return DashboardStats(
            today = toDomain(today),
            week = toDomain(week),
            month = toDomain(month),
            insights = toDomainList(insights),
            taskTrend = toDomain(taskTrend),
            categoryBreakdown = toDomainList(categoryBreakdown),
            timeAllocation = toDomainList(timeAllocation)
        )
    }

    // ─── Event Types Helpers ──────────────────────────────────────

    fun createTaskCreatedEvent(userId: String, taskId: String, priority: String, category: String? = null): AnalyticsEvent {
        return AnalyticsEvent(
            id = java.util.UUID.randomUUID().toString(),
            userId = userId,
            eventType = "task_created",
            eventCategory = "user_action",
            eventSubtype = priority,
            entityId = taskId,
            entityType = "task",
            value = when (priority) {
                "low" -> 1.0
                "medium" -> 2.0
                "high" -> 3.0
                "critical" -> 4.0
                else -> 0.0
            },
            metadata = mapOf(
                "category" to (category ?: "unspecified")
            ),
            sessionId = null,
            eventTimestamp = nowUtc(),
            createdAt = nowUtc()
        )
    }

    fun createTaskCompletedEvent(userId: String, taskId: String, completionTimeHours: Double, priority: String): AnalyticsEvent {
        return AnalyticsEvent(
            id = java.util.UUID.randomUUID().toString(),
            userId = userId,
            eventType = "task_completed",
            eventCategory = "user_action",
            eventSubtype = priority,
            entityId = taskId,
            entityType = "task",
            value = completionTimeHours,
            metadata = mapOf(
                "priority" to priority
            ),
            sessionId = null,
            eventTimestamp = nowUtc(),
            createdAt = nowUtc()
        )
    }

    fun createAppointmentCreatedEvent(userId: String, appointmentId: String, durationMinutes: Int): AnalyticsEvent {
        return AnalyticsEvent(
            id = java.util.UUID.randomUUID().toString(),
            userId = userId,
            eventType = "appointment_created",
            eventCategory = "user_action",
            eventSubtype = "scheduled",
            entityId = appointmentId,
            entityType = "appointment",
            value = durationMinutes.toDouble(),
            metadata = emptyMap(),
            sessionId = null,
            eventTimestamp = nowUtc(),
            createdAt = nowUtc()
        )
    }

    fun createMeetingJoinedEvent(userId: String, meetingId: String, platform: String): AnalyticsEvent {
        return AnalyticsEvent(
            id = java.util.UUID.randomUUID().toString(),
            userId = userId,
            eventType = "meeting_joined",
            eventCategory = "user_action",
            eventSubtype = platform,
            entityId = meetingId,
            entityType = "meeting",
            value = null,
            metadata = mapOf(
                "platform" to platform
            ),
            sessionId = null,
            eventTimestamp = nowUtc(),
            createdAt = nowUtc()
        )
    }

    fun createFocusTimeEvent(userId: String, durationMinutes: Double, taskId: String? = null): AnalyticsEvent {
        return AnalyticsEvent(
            id = java.util.UUID.randomUUID().toString(),
            userId = userId,
            eventType = "focus_time",
            eventCategory = "system",
            eventSubtype = "tracked",
            entityId = taskId,
            entityType = if (taskId != null) "task" else null,
            value = durationMinutes,
            metadata = emptyMap(),
            sessionId = null,
            eventTimestamp = nowUtc(),
            createdAt = nowUtc()
        )
    }
}