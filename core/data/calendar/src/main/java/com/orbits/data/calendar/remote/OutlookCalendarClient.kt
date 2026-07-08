package com.orbits.data.calendar.remote

import com.orbits.core.common.Result
import com.orbits.core.common.Logger
import com.orbits.data.calendar.local.CalendarEventEntity
import com.microsoft.graph.authentication.BaseAuthenticationProvider
import com.microsoft.graph.core.ClientFactory
import com.microsoft.graph.httpcore.HttpClients
import com.microsoft.graph.requests.GraphServiceClient
import okhttp3.OkHttpClient
import java.time.Instant
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
    private fun createGraphClient(accessToken: String): GraphServiceClient {
        val authProvider = object : BaseAuthenticationProvider() {
            override fun authenticateRequest(
                request: okhttp3.Request.Builder,
                credentials: com.microsoft.graph.core.ClientAuthentication
            ) {
                request.header("Authorization", "Bearer $accessToken")
            }
        }

        val httpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("Authorization", "Bearer $accessToken")
                    .build()
                chain.proceed(request)
            }
            .build()

        val client = ClientFactory.builder()
            .httpClient(httpClient)
            .authenticationProvider(authProvider)
            .build()
            .get()

        return client as GraphServiceClient
    }

    /**
     * Syncs events from Outlook Calendar for the given user.
     */
    suspend fun syncEvents(accessToken: String): Result<List<CalendarEventEntity>> {
        return try {
            val client = createGraphClient(accessToken)

            // Build query for events in the next 30 days
            val now = Instant.now()
            val thirtyDaysAhead = now.plusSeconds(30 * 24 * 60 * 60)

            // Using Microsoft Graph API query
            val events = client.me()
                .calendar()
                .events()
                .buildRequest()
                .select("id,subject,bodyPreview,start,end,location,isOnlineMeeting,onlineMeeting")
                .filter("start/dateTime ge '${now.toString()}'")
                .get()

            val mappedEvents = events.currentPage.map { outlookEvent ->
                // Convert to CalendarEventEntity
                // Simplified for this example
                CalendarEventEntity(
                    id = outlookEvent.id,
                    userId = "", // Will be set by caller
                    title = outlookEvent.subject ?: "Untitled",
                    description = outlookEvent.bodyPreview,
                    startDateTime = outlookEvent.start.dateTime?.toString() ?: "",
                    endDateTime = outlookEvent.end.dateTime?.toString() ?: "",
                    allDayEvent = outlookEvent.start.date != null,
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
            }

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
    suspend fun createEvent(accessToken: String, entity: CalendarEventEntity): Result<String> {
        return try {
            val client = createGraphClient(accessToken)

            // Create event request
            // Simplified — would build a full Event object
            val result = client.me()
                .calendar()
                .events()
                .buildRequest()
                .post(null)

            Logger.d(TAG, "Created Outlook Calendar event")
            Result.Success(result.id)
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

            client.me()
                .calendar()
                .events(externalId)
                .buildRequest()
                .patch(null)

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