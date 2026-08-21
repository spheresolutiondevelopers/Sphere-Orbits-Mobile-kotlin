package com.orbits.data.appointments.remote

import kotlinx.serialization.Serializable

@Serializable
internal data class AppointmentDto(
    val id: String,
    val userId: String,
    val title: String,
    val description: String? = null,
    val appointmentType: String = "general",
    val startDateTime: String,
    val endDateTime: String,
    val allDayEvent: Boolean = false,
    val location: String? = null,
    val isVirtual: Boolean = false,
    val meetingLink: String? = null,
    val meetingPlatform: String? = null,
    val status: String = "scheduled",
    val reminderMinutesBefore: Int = 15,
    val isRecurring: Boolean = false,
    val recurrencePattern: String? = null,
    val calendarColor: String? = "#2196F3",
    val notes: String? = null,
    val externalEventId: String? = null,
    val externalSyncStatus: String = "not_synced",
    val isDeleted: Boolean = false,
    val createdAt: String,
    val updatedAt: String,
    val participantCount: Int = 0
)

@Serializable
internal data class CreateAppointmentRequest(
    val title: String,
    val description: String? = null,
    val appointmentType: String = "general",
    val startDateTime: String,
    val endDateTime: String,
    val allDayEvent: Boolean = false,
    val location: String? = null,
    val isVirtual: Boolean = false,
    val meetingLink: String? = null,
    val meetingPlatform: String? = null,
    val reminderMinutesBefore: Int = 15,
    val isRecurring: Boolean = false,
    val recurrencePattern: String? = null,
    val calendarColor: String? = "#2196F3",
    val notes: String? = null,
    val participants: List<ParticipantDto> = emptyList()
)

@Serializable
internal data class UpdateAppointmentRequest(
    val title: String? = null,
    val description: String? = null,
    val appointmentType: String? = null,
    val startDateTime: String? = null,
    val endDateTime: String? = null,
    val allDayEvent: Boolean? = null,
    val location: String? = null,
    val isVirtual: Boolean? = null,
    val meetingLink: String? = null,
    val meetingPlatform: String? = null,
    val reminderMinutesBefore: Int? = null,
    val isRecurring: Boolean? = null,
    val recurrencePattern: String? = null,
    val calendarColor: String? = null,
    val notes: String? = null,
    val status: String? = null,
    val participants: List<ParticipantDto>? = null
)

@Serializable
internal data class ParticipantDto(
    val id: String? = null,
    val appointmentId: String? = null,
    val userId: String? = null,
    val email: String,
    val fullName: String? = null,
    val invitationStatus: String = "pending",
    val participantRole: String = "attendee",
    val createdAt: String? = null,
    val updatedAt: String? = null
)

@Serializable
internal data class UpdateParticipantStatusRequest(
    val invitationStatus: String
)

@Serializable
internal data class AppointmentAvailabilityRequest(
    val startDateTime: String,
    val endDateTime: String,
    val durationMinutes: Int,
    val timezone: String
)

@Serializable
internal data class AppointmentAvailabilityResponse(
    val available: Boolean,
    val conflicts: List<AppointmentConflict>? = null
)

@Serializable
internal data class AppointmentConflict(
    val appointmentId: String,
    val title: String,
    val startDateTime: String,
    val endDateTime: String
)