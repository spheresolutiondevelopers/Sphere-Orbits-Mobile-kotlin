package com.orbits.data.calendar.remote

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials
import com.orbits.core.common.Result
import com.orbits.core.common.Logger
import com.orbits.data.calendar.CalendarEventEntity
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class GoogleCalendarClient @Inject constructor() {

    companion object {
        private const val TAG = "GoogleCalendarClient"
        private const val APPLICATION_NAME = "Sphere Schedule"
    }

    private val httpTransport by lazy { GoogleNetHttpTransport.newTrustedTransport() }
    private val jsonFactory by lazy { GsonFactory.getDefaultInstance() }

    /**
     * Creates a Google Calendar service instance with the provided access token.
     */
    private fun createCalendarService(accessToken: String): Calendar {
        val credentials = GoogleCredentials.create(AccessToken(accessToken, null))
        return Calendar.Builder(httpTransport, jsonFactory, HttpCredentialsAdapter(credentials))
            .setApplicationName(APPLICATION_NAME)
            .build()
    }

    /**
     * Syncs events from Google Calendar for the given user.
     */
    suspend fun syncEvents(accessToken: String, calendarId: String = "primary"): Result<List<CalendarEventEntity>> {
        return try {
            val service = createCalendarService(accessToken)

            // Get events from the last 30 days to 30 days ahead
            val now = Instant.now()
            val thirtyDaysAgo = now.minusSeconds(30 * 24 * 60 * 60)
            val thirtyDaysAhead = now.plusSeconds(30 * 24 * 60 * 60)

            val events = service.events().list(calendarId)
                .setTimeMin(com.google.api.client.util.DateTime(thirtyDaysAgo.toEpochMilli()))
                .setTimeMax(com.google.api.client.util.DateTime(thirtyDaysAhead.toEpochMilli()))
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute()
                .items

            val mappedEvents = events.mapNotNull { googleEvent ->
                convertGoogleEventToEntity(googleEvent)
            }

            Logger.d(TAG, "Synced ${mappedEvents.size} events from Google Calendar")
            Result.Success(mappedEvents)
        } catch (e: Exception) {
            Logger.e(TAG, "Error syncing Google Calendar", e)
            Result.Error(e)
        }
    }

    /**
     * Creates an event in Google Calendar.
     */
    suspend fun createEvent(accessToken: String, entity: CalendarEventEntity, calendarId: String = "primary"): Result<String> {
        return try {
            val service = createCalendarService(accessToken)
            val event = convertEntityToGoogleEvent(entity)
            val created = service.events().insert(calendarId, event).execute()
            Logger.d(TAG, "Created Google Calendar event: ${created.id}")
            Result.Success(created.id)
        } catch (e: Exception) {
            Logger.e(TAG, "Error creating Google Calendar event", e)
            Result.Error(e)
        }
    }

    /**
     * Updates an event in Google Calendar.
     */
    suspend fun updateEvent(accessToken: String, externalId: String, entity: CalendarEventEntity, calendarId: String = "primary"): Result<Unit> {
        return try {
            val service = createCalendarService(accessToken)
            val event = convertEntityToGoogleEvent(entity)
            service.events().update(calendarId, externalId, event).execute()
            Logger.d(TAG, "Updated Google Calendar event: $externalId")
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error updating Google Calendar event $externalId", e)
            Result.Error(e)
        }
    }

    /**
     * Deletes an event from Google Calendar.
     */
    suspend fun deleteEvent(accessToken: String, externalId: String, calendarId: String = "primary"): Result<Unit> {
        return try {
            val service = createCalendarService(accessToken)
            service.events().delete(calendarId, externalId).execute()
            Logger.d(TAG, "Deleted Google Calendar event: $externalId")
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error deleting Google Calendar event $externalId", e)
            Result.Error(e)
        }
    }

    // ─── Conversion Helpers ──────────────────────────────────────

    private fun convertGoogleEventToEntity(googleEvent: Event): CalendarEventEntity? {
        return try {
            val start = googleEvent.start
            val end = googleEvent.end
            val startDateTime = start?.dateTime?.toString() ?: start?.date?.toString()
            val endDateTime = end?.dateTime?.toString() ?: end?.date?.toString()

            if (startDateTime == null || endDateTime == null) return null

            val now = Instant.now().toString()
            CalendarEventEntity(
                id = googleEvent.id,
                userId = "", // Will be set by caller
                title = googleEvent.summary ?: "Untitled",
                description = googleEvent.description,
                startDateTime = startDateTime,
                endDateTime = endDateTime,
                allDayEvent = start?.date != null, // If date is set, it's all-day
                location = googleEvent.location,
                isVirtual = googleEvent.hangoutLink != null || googleEvent.conferenceData != null,
                meetingLink = googleEvent.hangoutLink ?: googleEvent.conferenceData?.entryPoints?.firstOrNull()?.uri,
                meetingPlatform = if (googleEvent.hangoutLink != null) "Google Meet" else null,
                status = googleEvent.status ?: "scheduled",
                reminderMinutesBefore = googleEvent.reminders?.overrides?.firstOrNull()?.minutes ?: 15,
                isRecurring = googleEvent.recurrence != null && googleEvent.recurrence.isNotEmpty(),
                recurrencePattern = googleEvent.recurrence?.firstOrNull(),
                calendarColor = googleEvent.colorId,
                externalEventId = googleEvent.id,
                externalSyncStatus = "synced",
                createdAt = now,
                updatedAt = now,
                syncedAt = now
            )
        } catch (e: Exception) {
            Logger.e(TAG, "Error converting Google event to entity", e)
            null
        }
    }

    private fun convertEntityToGoogleEvent(entity: CalendarEventEntity): Event {
        val event = Event()
        event.summary = entity.title
        event.description = entity.description
        event.location = entity.location
        event.status = entity.status

        // Set start time
        val start = EventDateTime()
        if (entity.allDayEvent) {
            start.date = com.google.api.client.util.DateTime(entity.startDateTime)
        } else {
            start.dateTime = com.google.api.client.util.DateTime(entity.startDateTime)
            start.timeZone = ZoneId.systemDefault().id
        }
        event.start = start

        // Set end time
        val end = EventDateTime()
        if (entity.allDayEvent) {
            end.date = com.google.api.client.util.DateTime(entity.endDateTime)
        } else {
            end.dateTime = com.google.api.client.util.DateTime(entity.endDateTime)
            end.timeZone = ZoneId.systemDefault().id
        }
        event.end = end

        // Set recurrence if applicable
        if (entity.isRecurring && entity.recurrencePattern != null) {
            event.recurrence = listOf(entity.recurrencePattern)
        }

        // Set color
        entity.calendarColor?.let { event.colorId = it }

        // Set reminders
        if (entity.reminderMinutesBefore > 0) {
            val reminders = com.google.api.services.calendar.model.Event.Reminders()
            reminders.useDefault = false
            reminders.overrides = listOf(
                com.google.api.services.calendar.model.EventReminder()
                    .setMethod("popup")
                    .setMinutes(entity.reminderMinutesBefore)
            )
            event.reminders = reminders
        }

        // Set conference data for virtual meetings
        if (entity.isVirtual && entity.meetingLink != null) {
            // This would create a conference data structure
            // Simplified for this example
        }

        return event
    }
}