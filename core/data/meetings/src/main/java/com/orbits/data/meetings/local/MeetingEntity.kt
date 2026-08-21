package com.orbits.data.meetings.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Meeting Entity — Room database representation.
 * ISOLATED in :core:data:meetings. Changes here ONLY recompile this module.
 *
 * Meetings are scheduled virtual or in-person gatherings with agendas,
 * minutes, and action items. Each meeting is linked to a Task.
 */
@Entity(
    tableName = "meetings",
    indices = [
        Index(value = ["task_id"], unique = true), // One meeting per task
        Index(value = ["organizer_user_id"]),
        Index(value = ["organizer_user_id", "start_datetime"]),
        Index(value = ["status"]),
        Index(value = ["updated_at"]),
        Index(value = ["is_deleted"])
    ]
)
internal data class MeetingEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "task_id")
    val taskId: String,

    @ColumnInfo(name = "organizer_user_id")
    val organizerUserId: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "start_datetime")
    val startDateTime: String,

    @ColumnInfo(name = "end_datetime")
    val endDateTime: String,

    @ColumnInfo(name = "meeting_link")
    val meetingLink: String? = null,

    @ColumnInfo(name = "meeting_platform")
    val meetingPlatform: String? = null, // zoom, teams, google_meet, custom

    @ColumnInfo(name = "meeting_id")
    val meetingId: String? = null, // Platform-specific meeting ID

    @ColumnInfo(name = "passcode")
    val passcode: String? = null,

    @ColumnInfo(name = "agenda")
    val agenda: String? = null,

    @ColumnInfo(name = "minutes")
    val minutes: String? = null,

    @ColumnInfo(name = "action_items")
    val actionItems: String? = null, // JSON array of action items

    @ColumnInfo(name = "is_recurring")
    val isRecurring: Boolean = false,

    @ColumnInfo(name = "recurrence_pattern")
    val recurrencePattern: String? = null, // RRULE format

    @ColumnInfo(name = "status")
    val status: String = "scheduled", // scheduled, live, ended, cancelled

    @ColumnInfo(name = "recording_url")
    val recordingUrl: String? = null,

    @ColumnInfo(name = "transcript_url")
    val transcriptUrl: String? = null,

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

/**
 * Meeting Participant Entity — attendees for a meeting.
 */
@Entity(
    tableName = "meeting_participants",
    indices = [
        Index(value = ["meeting_id"]),
        Index(value = ["meeting_id", "email"], unique = true),
        Index(value = ["user_id"])
    ]
)
internal data class MeetingParticipantEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "meeting_id")
    val meetingId: String,

    @ColumnInfo(name = "user_id")
    val userId: String? = null,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "full_name")
    val fullName: String? = null,

    @ColumnInfo(name = "invitation_status")
    val invitationStatus: String = "pending", // pending, sent, accepted, declined, tentative

    @ColumnInfo(name = "participant_role")
    val participantRole: String = "attendee", // organizer, presenter, attendee

    @ColumnInfo(name = "joined_at")
    val joinedAt: String? = null,

    @ColumnInfo(name = "left_at")
    val leftAt: String? = null,

    @ColumnInfo(name = "duration_seconds")
    val durationSeconds: Int? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "updated_at")
    val updatedAt: String
)