package com.orbits.core.model

import kotlinx.serialization.Serializable

@Serializable
data class AnalyticsEventDto(
    val id: String,
    val userId: String,
    val eventType: String,
    val eventCategory: String = "user_action",
    val eventSubtype: String? = null,
    val entityId: String? = null,
    val entityType: String? = null,
    val value: Double? = null,
    val metadata: Map<String, String>? = null,
    val sessionId: String? = null,
    val eventTimestamp: String,
    val createdAt: String
)

@Serializable
data class CreateAnalyticsEventRequest(
    val eventType: String,
    val eventCategory: String = "user_action",
    val eventSubtype: String? = null,
    val entityId: String? = null,
    val entityType: String? = null,
    val value: Double? = null,
    val metadata: Map<String, String>? = null,
    val sessionId: String? = null
)

@Serializable
data class ProductivityStatsDto(
    val tasksCompleted: Int,
    val tasksCreated: Int,
    val tasksOverdue: Int,
    val tasksCompletionRate: Double,
    val appointmentsAttended: Int,
    val appointmentsScheduled: Int,
    val meetingsAttended: Int,
    val meetingsScheduled: Int,
    val totalFocusHours: Double,
    val averageTaskCompletionTime: Double, // in hours
    val productivityScore: Int, // 0-100
    val trend: String, // up, down, stable
    val period: String, // daily, weekly, monthly
    val startDate: String,
    val endDate: String
)

@Serializable
data class TaskCompletionTrendDto(
    val labels: List<String>,
    val completed: List<Int>,
    val created: List<Int>,
    val overdue: List<Int>
)

@Serializable
data class CategoryBreakdownDto(
    val category: String,
    val count: Int,
    val percentage: Double,
    val color: String? = null
)

@Serializable
data class TimeAllocationDto(
    val category: String,
    val hours: Double,
    val percentage: Double,
    val color: String? = null
)

@Serializable
data class ProductivityInsightDto(
    val title: String,
    val description: String,
    val type: String, // positive, negative, neutral
    val recommendation: String? = null,
    val metricValue: Double? = null,
    val metricLabel: String? = null
)

@Serializable
data class FocusTimeDto(
    val date: String,
    val hours: Double,
    val tasksCompleted: Int,
    val focusScore: Int // 0-100
)

@Serializable
data class AnalyticsReportRequest(
    val reportType: String, // productivity, task_breakdown, time_allocation, custom
    val startDate: String,
    val endDate: String,
    val format: String = "json", // json, pdf, csv
    val includeCharts: Boolean = true,
    val includeInsights: Boolean = true,
    val filters: Map<String, String>? = null
)
