package com.orbits.data.notes.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Note Entity — Room database representation.
 * ISOLATED in :core:data:notes. Changes here ONLY recompile this module.
 *
 * Notes are rich-text documents that can be linked to tasks, events,
 * appointments, or meetings. They support tags, pinning, and soft delete.
 */
@Entity(
    tableName = "notes",
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["user_id", "updated_at"]),
        Index(value = ["user_id", "is_pinned"], name = "idx_notes_user_pinned"),
        Index(value = ["user_id", "is_archived"], name = "idx_notes_user_archived"),
        Index(value = ["task_id"]),
        Index(value = ["event_id"]),
        Index(value = ["appointment_id"]),
        Index(value = ["meeting_id"]),
        Index(value = ["updated_at"]),
        Index(value = ["is_deleted"])
    ]
)
internal data class NoteEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "title")
    val title: String? = null,

    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "content_format")
    val contentFormat: String = "markdown", // markdown, plain, html

    @ColumnInfo(name = "tags")
    val tags: String? = null, // Comma-separated tags

    @ColumnInfo(name = "is_pinned")
    val isPinned: Boolean = false,

    @ColumnInfo(name = "is_archived")
    val isArchived: Boolean = false,

    @ColumnInfo(name = "color")
    val color: String? = null, // Hex color for note background

    @ColumnInfo(name = "task_id")
    val taskId: String? = null,

    @ColumnInfo(name = "event_id")
    val eventId: String? = null,

    @ColumnInfo(name = "appointment_id")
    val appointmentId: String? = null,

    @ColumnInfo(name = "meeting_id")
    val meetingId: String? = null,

    @ColumnInfo(name = "reminder_at")
    val reminderAt: String? = null,

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false,

    @ColumnInfo(name = "deleted_at")
    val deletedAt: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "updated_at")
    val updatedAt: String,

    @ColumnInfo(name = "synced_at")
    val syncedAt: String? = null,

    @ColumnInfo(name = "sync_status")
    val syncStatus: String = "synced" // synced, pending, error
)