package com.orbits.data.analytics.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Analytics Event Entity — Room database representation.
 * ISOLATED in :core:data:analytics. Changes here ONLY recompile this module.
 *
 * Tracks user events for productivity analysis, reporting, and insights.
 * Events are stored locally and synced to the server for aggregate analysis.
 */
@Entity(
    tableName = "analytics_events",
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["user_id", "event_timestamp"]),
        Index(value = ["event_type"]),
        Index(value = ["session_id"]),
        Index(value = ["event_timestamp"]),
        Index(value = ["user_id", "event_type", "event_timestamp"])
    ]
)
internal data class AnalyticsEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "event_type")
    val eventType: String, // task_created, task_completed, appointment_scheduled, meeting_joined, etc.

    @ColumnInfo(name = "event_category")
    val eventCategory: String = "user_action", // user_action, system, performance

    @ColumnInfo(name = "event_subtype")
    val eventSubtype: String? = null,

    @ColumnInfo(name = "entity_id")
    val entityId: String? = null, // ID of the entity this event relates to (task, appointment, etc.)

    @ColumnInfo(name = "entity_type")
    val entityType: String? = null, // task, appointment, meeting, etc.

    @ColumnInfo(name = "value")
    val value: Double? = null, // Numeric value (e.g., task priority, duration, etc.)

    @ColumnInfo(name = "metadata")
    val metadata: String? = null, // JSON string of additional data

    @ColumnInfo(name = "session_id")
    val sessionId: String? = null,

    @ColumnInfo(name = "event_timestamp")
    val eventTimestamp: String,

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "synced_at")
    val syncedAt: String? = null,

    @ColumnInfo(name = "sync_status")
    val syncStatus: String = "pending" // pending, synced, error
)

/**
 * Analytics Summary Entity — aggregated analytics data.
 * Used for caching daily/weekly/monthly summaries.
 */
@Entity(
    tableName = "analytics_summaries",
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["user_id", "summary_type"]),
        Index(value = ["summary_date"])
    ]
)
internal data class AnalyticsSummaryEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "summary_type")
    val summaryType: String, // daily, weekly, monthly

    @ColumnInfo(name = "summary_date")
    val summaryDate: String, // ISO date of the summary

    @ColumnInfo(name = "data")
    val data: String, // JSON data of the summary

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "updated_at")
    val updatedAt: String
)