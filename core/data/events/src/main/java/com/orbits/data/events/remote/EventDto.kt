package com.orbits.data.events.remote

import kotlinx.serialization.Serializable

@Serializable
internal data class EventDto(
    val id: String,
    val userId: String,
    val categoryId: String? = null,
    val taskId: String? = null,
    val name: String,
    val description: String? = null,
    val format: String? = null,
    val planningNotes: String? = null,
    val startDateTime: String? = null,
    val endDateTime: String? = null,
    val location: String? = null,
    val status: String = "planned",
    val isRecurring: Boolean = false,
    val recurrencePattern: String? = null,
    val budget: Double? = null,
    val currency: String = "USD",
    val maxAttendees: Int? = null,
    val isPublic: Boolean = false,
    val imageUrl: String? = null,
    val coverPhotoUrl: String? = null,
    val isDeleted: Boolean = false,
    val createdAt: String,
    val updatedAt: String,
    val participantCount: Int = 0,
    val confirmedCount: Int = 0
)

@Serializable
internal data class CreateEventRequest(
    val name: String,
    val categoryId: String? = null,
    val description: String? = null,
    val format: String? = null,
    val planningNotes: String? = null,
    val startDateTime: String? = null,
    val endDateTime: String? = null,
    val location: String? = null,
    val status: String = "planned",
    val isRecurring: Boolean = false,
    val recurrencePattern: String? = null,
    val budget: Double? = null,
    val currency: String = "USD",
    val maxAttendees: Int? = null,
    val isPublic: Boolean = false,
    val imageUrl: String? = null,
    val coverPhotoUrl: String? = null,
    val participants: List<EventParticipantDto> = emptyList()
)

@Serializable
internal data class UpdateEventRequest(
    val name: String? = null,
    val categoryId: String? = null,
    val description: String? = null,
    val format: String? = null,
    val planningNotes: String? = null,
    val startDateTime: String? = null,
    val endDateTime: String? = null,
    val location: String? = null,
    val status: String? = null,
    val isRecurring: Boolean? = null,
    val recurrencePattern: String? = null,
    val budget: Double? = null,
    val currency: String? = null,
    val maxAttendees: Int? = null,
    val isPublic: Boolean? = null,
    val imageUrl: String? = null,
    val coverPhotoUrl: String? = null,
    val participants: List<EventParticipantDto>? = null
)

@Serializable
internal data class EventParticipantDto(
    val id: String? = null,
    val eventId: String? = null,
    val userId: String? = null,
    val email: String,
    val fullName: String? = null,
    val invitationStatus: String = "pending",
    val participantRole: String = "attendee",
    val plusOnes: Int = 0,
    val dietaryRestrictions: String? = null,
    val specialRequests: String? = null,
    val checkedIn: Boolean = false,
    val checkInTime: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

@Serializable
internal data class UpdateParticipantStatusRequest(
    val invitationStatus: String,
    val plusOnes: Int? = null,
    val dietaryRestrictions: String? = null,
    val specialRequests: String? = null
)

@Serializable
internal data class EventStatistics(
    val totalAttendees: Int,
    val confirmedAttendees: Int,
    val pendingAttendees: Int,
    val declinedAttendees: Int,
    val checkedInAttendees: Int,
    val capacityPercentage: Double
)

@Serializable
internal data class BudgetCategory(
    val category: String,
    val allocated: Double,
    val spent: Double,
    val remaining: Double
)

@Serializable
internal data class EventTimelineItem(
    val id: String,
    val eventId: String,
    val title: String,
    val description: String? = null,
    val dueDateTime: String,
    val status: String, // pending, in_progress, completed, missed
    val order: Int
)