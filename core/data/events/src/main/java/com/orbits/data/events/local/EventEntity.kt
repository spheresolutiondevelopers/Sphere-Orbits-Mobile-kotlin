package com.orbits.data.events.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Event Entity — Room database representation.
 * ISOLATED in :core:data:events. Changes here ONLY recompile this module.
 *
 * Events are social/organizational events like conferences, workshops,
 * weddings, parties, etc. They differ from CalendarEventEntity which
 * represents calendar appointments.
 */
@Entity(
    tableName = "events",
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["user_id", "start_datetime"]),
        Index(value = ["user_id", "status"]),
        Index(value = ["user_id", "category_id"]),
        Index(value = ["updated_at"]),
        Index(value = ["is_deleted"])
    ]
)
internal data class EventEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "category_id")
    val categoryId: String? = null,

    @ColumnInfo(name = "task_id")
    val taskId: String? = null,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "format")
    val format: String? = null, // "In-Person", "Virtual", "Hybrid"

    @ColumnInfo(name = "planning_notes")
    val planningNotes: String? = null,

    @ColumnInfo(name = "start_datetime")
    val startDateTime: String? = null,

    @ColumnInfo(name = "end_datetime")
    val endDateTime: String? = null,

    @ColumnInfo(name = "location")
    val location: String? = null,

    @ColumnInfo(name = "status")
    val status: String = "planned", // planned, ongoing, completed, cancelled

    @ColumnInfo(name = "is_recurring")
    val isRecurring: Boolean = false,

    @ColumnInfo(name = "recurrence_pattern")
    val recurrencePattern: String? = null,

    @ColumnInfo(name = "budget")
    val budget: Double? = null,

    @ColumnInfo(name = "currency")
    val currency: String = "USD",

    @ColumnInfo(name = "max_attendees")
    val maxAttendees: Int? = null,

    @ColumnInfo(name = "is_public")
    val isPublic: Boolean = false,

    @ColumnInfo(name = "image_url")
    val imageUrl: String? = null,

    @ColumnInfo(name = "cover_photo_url")
    val coverPhotoUrl: String? = null,

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
 * Event Participant Entity — represents RSVPs/invitees for an event.
 */
@Entity(
    tableName = "event_participants",
    indices = [
        Index(value = ["event_id"]),
        Index(value = ["event_id", "email"], unique = true)
    ]
)
internal data class EventParticipantEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "event_id")
    val eventId: String,

    @ColumnInfo(name = "user_id")
    val userId: String? = null,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "full_name")
    val fullName: String? = null,

    @ColumnInfo(name = "invitation_status")
    val invitationStatus: String = "pending", // pending, sent, accepted, declined, tentative

    @ColumnInfo(name = "participant_role")
    val participantRole: String = "attendee", // organizer, attendee, optional, speaker, vendor

    @ColumnInfo(name = "plus_ones")
    val plusOnes: Int = 0,

    @ColumnInfo(name = "dietary_restrictions")
    val dietaryRestrictions: String? = null,

    @ColumnInfo(name = "special_requests")
    val specialRequests: String? = null,

    @ColumnInfo(name = "checked_in")
    val checkedIn: Boolean = false,

    @ColumnInfo(name = "check_in_time")
    val checkInTime: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "updated_at")
    val updatedAt: String
)