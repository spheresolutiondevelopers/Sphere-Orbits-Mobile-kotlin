package com.orbits.data.meetings.remote

import kotlinx.serialization.Serializable

@Serializable
internal data class MeetingDto(
    val id: String,
    val taskId: String,
    val organizerUserId: String,
    val title: String,
    val description: String? = null,
    val startDateTime: String,
    val endDateTime: String,
    val meetingLink: String? = null,
    val meetingPlatform: String? = null,
    val meetingId: String? = null,
    val passcode: String? = null,
    val agenda: String? = null,
    val minutes: String? = null,
    val actionItems: List<ActionItemDto>? = null,
    val isRecurring: Boolean = false,
    val recurrencePattern: String? = null,
    val status: String = "scheduled",
    val recordingUrl: String? = null,
    val transcriptUrl: String? = null,
    val isDeleted: Boolean = false,
    val createdAt: String,
    val updatedAt: String,
    val participantCount: Int = 0,
    val attendedCount: Int = 0
)

@Serializable
internal data class CreateMeetingRequest(
    val taskId: String,
    val title: String,
    val description: String? = null,
    val startDateTime: String,
    val endDateTime: String,
    val meetingPlatform: String? = null,
    val agenda: String? = null,
    val isRecurring: Boolean = false,
    val recurrencePattern: String? = null,
    val participants: List<MeetingParticipantDto> = emptyList()
)

@Serializable
internal data class UpdateMeetingRequest(
    val title: String? = null,
    val description: String? = null,
    val startDateTime: String? = null,
    val endDateTime: String? = null,
    val meetingPlatform: String? = null,
    val agenda: String? = null,
    val minutes: String? = null,
    val actionItems: List<ActionItemDto>? = null,
    val isRecurring: Boolean? = null,
    val recurrencePattern: String? = null,
    val status: String? = null,
    val recordingUrl: String? = null,
    val transcriptUrl: String? = null,
    val participants: List<MeetingParticipantDto>? = null
)

@Serializable
internal data class MeetingParticipantDto(
    val id: String? = null,
    val meetingId: String? = null,
    val userId: String? = null,
    val email: String,
    val fullName: String? = null,
    val invitationStatus: String = "pending",
    val participantRole: String = "attendee",
    val joinedAt: String? = null,
    val leftAt: String? = null,
    val durationSeconds: Int? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

@Serializable
internal data class ActionItemDto(
    val id: String,
    val meetingId: String,
    val description: String,
    val assignedTo: String? = null,
    val dueDate: String? = null,
    val status: String = "pending", // pending, in_progress, completed, cancelled
    val createdAt: String,
    val updatedAt: String
)

@Serializable
internal data class CreateActionItemRequest(
    val meetingId: String,
    val description: String,
    val assignedTo: String? = null,
    val dueDate: String? = null
)

@Serializable
internal data class UpdateActionItemRequest(
    val description: String? = null,
    val assignedTo: String? = null,
    val dueDate: String? = null,
    val status: String? = null
)

@Serializable
internal data class ZoomMeetingRequest(
    val topic: String,
    val type: Int = 2, // 2 = scheduled meeting
    val startTime: String,
    val duration: Int, // minutes
    val timezone: String,
    val agenda: String? = null,
    val password: String? = null
)

@Serializable
internal data class ZoomMeetingResponse(
    val id: Long,
    val topic: String,
    val startTime: String,
    val duration: Int,
    val joinUrl: String,
    val meetingId: String,
    val password: String? = null,
    val status: String
)

@Serializable
internal data class TeamsMeetingRequest(
    val subject: String,
    val body: TeamsBody? = null,
    val start: TeamsDateTime,
    val end: TeamsDateTime,
    val location: TeamsLocation? = null,
    val attendees: List<TeamsAttendee>? = null,
    val isOnlineMeeting: Boolean = true,
    val onlineMeetingProvider: String = "teamsForBusiness"
)

@Serializable
internal data class TeamsBody(
    val contentType: String = "HTML",
    val content: String
)

@Serializable
internal data class TeamsDateTime(
    val dateTime: String,
    val timeZone: String
)

@Serializable
internal data class TeamsLocation(
    val displayName: String? = null
)

@Serializable
internal data class TeamsAttendee(
    val emailAddress: TeamsEmailAddress,
    val type: String = "required"
)

@Serializable
internal data class TeamsEmailAddress(
    val address: String,
    val name: String? = null
)