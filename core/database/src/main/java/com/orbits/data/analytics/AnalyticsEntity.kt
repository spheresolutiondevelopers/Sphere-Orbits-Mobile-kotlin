package com.orbits.data.analytics

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

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
data class AnalyticsEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "event_type")
    val eventType: String,

    @ColumnInfo(name = "event_category")
    val eventCategory: String = "user_action",

    @ColumnInfo(name = "event_subtype")
    val eventSubtype: String? = null,

    @ColumnInfo(name = "entity_id")
    val entityId: String? = null,

    @ColumnInfo(name = "entity_type")
    val entityType: String? = null,

    @ColumnInfo(name = "value")
    val value: Double? = null,

    @ColumnInfo(name = "metadata")
    val metadata: String? = null,

    @ColumnInfo(name = "session_id")
    val sessionId: String? = null,

    @ColumnInfo(name = "event_timestamp")
    val eventTimestamp: String,

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "synced_at")
    val syncedAt: String? = null,

    @ColumnInfo(name = "sync_status")
    val syncStatus: String = "pending"
)

@Entity(
    tableName = "analytics_summaries",
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["user_id", "summary_type"]),
        Index(value = ["summary_date"])
    ]
)
data class AnalyticsSummaryEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "summary_type")
    val summaryType: String,

    @ColumnInfo(name = "summary_date")
    val summaryDate: String,

    @ColumnInfo(name = "data")
    val data: String,

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "updated_at")
    val updatedAt: String
)
