package com.orbits.data.calendar.remote

import kotlinx.serialization.Serializable

@Serializable
internal data class CalendarEventDto(
    val id: String,
    val userId: String,
    val title: String,
    val description: String? = null,
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
    val updatedAt: String
)

@Serializable
internal data class CreateCalendarEventRequest(
    val title: String,
    val description: String? = null,
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
internal data class UpdateCalendarEventRequest(
    val title: String? = null,
    val description: String? = null,
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
    val email: String,
    val fullName: String? = null,
    val invitationStatus: String = "pending", // pending, sent, accepted, declined, tentative
    val participantRole: String = "attendee" // organizer, attendee, optional
)

@Serializable
internal data class GoogleCalendarEvent(
    val id: String,
    val summary: String,
    val description: String? = null,
    val start: GoogleDateTime,
    val end: GoogleDateTime,
    val location: String? = null,
    val status: String = "confirmed",
    val recurrence: List<String>? = null,
    val htmlLink: String? = null,
    val hangoutLink: String? = null,
    val conferenceData: ConferenceData? = null,
    val colorId: String? = null,
    val reminders: Reminders? = null
)

@Serializable
internal data class GoogleDateTime(
    val dateTime: String? = null,
    val date: String? = null,
    val timeZone: String? = null
)

@Serializable
internal data class ConferenceData(
    val conferenceId: String? = null,
    val conferenceSolution: ConferenceSolution? = null,
    val entryPoints: List<EntryPoint>? = null
)

@Serializable
internal data class ConferenceSolution(
    val key: ConferenceSolutionKey? = null,
    val name: String? = null,
    val iconUri: String? = null
)

@Serializable
internal data class ConferenceSolutionKey(
    val type: String? = null
)

@Serializable
internal data class EntryPoint(
    val entryPointType: String, // video, phone, sip, more
    val uri: String,
    val label: String? = null,
    val pin: String? = null,
    val accessCode: String? = null
)

@Serializable
internal data class Reminders(
    val useDefault: Boolean = true,
    val overrides: List<ReminderOverride>? = null
)

@Serializable
internal data class ReminderOverride(
    val method: String, // email, popup
    val minutes: Int
)