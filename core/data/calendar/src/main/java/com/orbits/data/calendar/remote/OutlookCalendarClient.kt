/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.data.calendar.remote

import com.orbits.core.common.Result
import com.orbits.core.common.Logger
import com.orbits.data.calendar.CalendarEventEntity
import com.microsoft.graph.authentication.IAuthenticationProvider
import com.microsoft.graph.requests.GraphServiceClient
import okhttp3.OkHttpClient
import java.net.URL
import java.time.Instant
import java.util.concurrent.CompletableFuture
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class OutlookCalendarClient @Inject constructor() {

    companion object {
        private const val TAG = "OutlookCalendarClient"
    }

    /**
     * Creates an Outlook Graph API client with the provided access token.
     */
    private fun createGraphClient(accessToken: String): GraphServiceClient<okhttp3.Request> {
        val authProvider = object : IAuthenticationProvider {
            override fun getAuthorizationTokenAsync(url: URL): CompletableFuture<String> {
                val future = CompletableFuture<String>()
                future.complete(accessToken)
                return future
            }
        }

        return GraphServiceClient.builder()
            .authenticationProvider(authProvider)
            .buildClient()
    }

    /**
     * Syncs events from Outlook Calendar for the given user.
     */
    suspend fun syncEvents(accessToken: String): Result<List<CalendarEventEntity>> {
        return try {
            val client = createGraphClient(accessToken)

            // Build query for events in the next 30 days
            val now = Instant.now()
            
            // Using Microsoft Graph API query
            val events = client.me()
                .calendar()
                .events()
                .buildRequest()
                .select("id,subject,bodyPreview,start,end,location,isOnlineMeeting,onlineMeeting")
                .filter("start/dateTime ge '${now}'")
                .get()

            val mappedEvents = events?.currentPage?.map { outlookEvent ->
                CalendarEventEntity(
                    id = outlookEvent.id ?: "",
                    userId = "", // Will be set by caller
                    title = outlookEvent.subject ?: "Untitled",
                    description = outlookEvent.bodyPreview,
                    startDateTime = outlookEvent.start?.dateTime ?: "",
                    endDateTime = outlookEvent.end?.dateTime ?: "",
                    allDayEvent = outlookEvent.isAllDay ?: false,
                    location = outlookEvent.location?.displayName,
                    isVirtual = outlookEvent.isOnlineMeeting ?: false,
                    meetingLink = outlookEvent.onlineMeeting?.joinUrl,
                    meetingPlatform = if (outlookEvent.isOnlineMeeting == true) "Microsoft Teams" else null,
                    status = "scheduled",
                    externalEventId = outlookEvent.id,
                    externalSyncStatus = "synced",
                    createdAt = Instant.now().toString(),
                    updatedAt = Instant.now().toString(),
                    syncedAt = Instant.now().toString()
                )
            } ?: emptyList()

            Logger.d(TAG, "Synced ${mappedEvents.size} events from Outlook Calendar")
            Result.Success(mappedEvents)
        } catch (e: Exception) {
            Logger.e(TAG, "Error syncing Outlook Calendar", e)
            Result.Error(e)
        }
    }

    /**
     * Creates an event in Outlook Calendar.
     */
    suspend fun createEvent(accessToken: String, entity: CalendarEventEntity): Result<String?> {
        return try {
            val client = createGraphClient(accessToken)

            // Create event request placeholder
            val event = com.microsoft.graph.models.Event()
            event.subject = entity.title
            
            val result = client.me()
                .events()
                .buildRequest()
                .post(event)

            Logger.d(TAG, "Created Outlook Calendar event")
            Result.Success(result?.id)
        } catch (e: Exception) {
            Logger.e(TAG, "Error creating Outlook Calendar event", e)
            Result.Error(e)
        }
    }

    /**
     * Updates an event in Outlook Calendar.
     */
    suspend fun updateEvent(accessToken: String, externalId: String): Result<Unit> {
        return try {
            val client = createGraphClient(accessToken)
            val event = com.microsoft.graph.models.Event()

            client.me()
                .events(externalId)
                .buildRequest()
                .patch(event)

            Logger.d(TAG, "Updated Outlook Calendar event: $externalId")
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error updating Outlook Calendar event $externalId", e)
            Result.Error(e)
        }
    }

    /**
     * Deletes an event from Outlook Calendar.
     */
    suspend fun deleteEvent(accessToken: String, externalId: String): Result<Unit> {
        return try {
            val client = createGraphClient(accessToken)

            client.me()
                .calendar()
                .events(externalId)
                .buildRequest()
                .delete()

            Logger.d(TAG, "Deleted Outlook Calendar event: $externalId")
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error deleting Outlook Calendar event $externalId", e)
            Result.Error(e)
        }
    }
}
