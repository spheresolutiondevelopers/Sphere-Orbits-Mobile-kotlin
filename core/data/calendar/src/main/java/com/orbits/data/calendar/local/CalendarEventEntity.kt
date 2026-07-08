package com.orbits.data.calendar.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Calendar Event Entity — Room database representation.
 * ISOLATED in :core:data:calendar. Changes here ONLY recompile this module.
 */
@Entity(
    tableName = "calendar_events",
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["user_id", "start_datetime"]),
        Index(value = ["user_id", "status"]),
        Index(value = ["external_event_id"]),
        Index(value = ["updated_at"]),
        Index(value = ["is_deleted"])
    ]
)
internal data class CalendarEventEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "start_datetime")
    val startDateTime: String,

    @ColumnInfo(name = "end_datetime")
    val endDateTime: String,

    @ColumnInfo(name = "all_day_event")
    val allDayEvent: Boolean = false,

    @ColumnInfo(name = "location")
    val location: String? = null,

    @ColumnInfo(name = "is_virtual")
    val isVirtual: Boolean = false,

    @ColumnInfo(name = "meeting_link")
    val meetingLink: String? = null,

    @ColumnInfo(name = "meeting_platform")
    val meetingPlatform: String? = null,

    @ColumnInfo(name = "status")
    val status: String = "scheduled", // scheduled, confirmed, cancelled, completed, rescheduled

    @ColumnInfo(name = "reminder_minutes_before")
    val reminderMinutesBefore: Int = 15,

    @ColumnInfo(name = "is_recurring")
    val isRecurring: Boolean = false,

    @ColumnInfo(name = "recurrence_pattern")
    val recurrencePattern: String? = null, // RRULE format

    @ColumnInfo(name = "calendar_color")
    val calendarColor: String? = "#2196F3",

    @ColumnInfo(name = "notes")
    val notes: String? = null,

    @ColumnInfo(name = "external_event_id")
    val externalEventId: String? = null,

    @ColumnInfo(name = "external_sync_status")
    val externalSyncStatus: String = "not_synced", // not_synced, synced, error

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "updated_at")
    val updatedAt: String,

    @ColumnInfo(name = "synced_at")
    val syncedAt: String? = null,

    @ColumnInfo(name = "sync_status")
    val syncStatus: String = "synced" // synced, pending, error
)