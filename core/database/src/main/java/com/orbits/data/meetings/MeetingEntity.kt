package com.orbits.data.meetings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "meetings",
    indices = [
        Index(value = ["task_id"], unique = true),
        Index(value = ["organizer_user_id"]),
        Index(value = ["organizer_user_id", "start_datetime"]),
        Index(value = ["status"]),
        Index(value = ["updated_at"]),
        Index(value = ["is_deleted"])
    ]
)
data class MeetingEntity(
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
    val meetingPlatform: String? = null,

    @ColumnInfo(name = "meeting_id")
    val meetingId: String? = null,

    @ColumnInfo(name = "passcode")
    val passcode: String? = null,

    @ColumnInfo(name = "agenda")
    val agenda: String? = null,

    @ColumnInfo(name = "minutes")
    val minutes: String? = null,

    @ColumnInfo(name = "action_items")
    val actionItems: String? = null,

    @ColumnInfo(name = "is_recurring")
    val isRecurring: Boolean = false,

    @ColumnInfo(name = "recurrence_pattern")
    val recurrencePattern: String? = null,

    @ColumnInfo(name = "status")
    val status: String = "scheduled",

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
    val syncStatus: String = "synced"
)

@Entity(
    tableName = "meeting_participants",
    indices = [
        Index(value = ["meeting_id"]),
        Index(value = ["meeting_id", "email"], unique = true),
        Index(value = ["user_id"])
    ]
)
data class MeetingParticipantEntity(
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
    val invitationStatus: String = "pending",

    @ColumnInfo(name = "participant_role")
    val participantRole: String = "attendee",

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
