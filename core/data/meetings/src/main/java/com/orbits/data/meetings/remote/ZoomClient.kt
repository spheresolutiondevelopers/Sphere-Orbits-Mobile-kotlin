package com.orbits.data.meetings.remote

import com.orbits.core.common.Result
import com.orbits.core.common.Logger
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ZoomClient @Inject constructor() {

    companion object {
        private const val TAG = "ZoomClient"
        private const val ZOOM_API_BASE = "https://api.zoom.us/v2"
        private val JSON_MEDIA_TYPE = "application/json".toMediaType()
        private val DATE_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME
            .withZone(ZoneId.systemDefault())
    }

    private val httpClient = OkHttpClient.Builder()
        .build()

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    /**
     * Creates a Zoom meeting.
     */
    suspend fun createMeeting(
        accessToken: String,
        request: ZoomMeetingRequest
    ): Result<ZoomMeetingResponse> {
        return try {
            val body = json.encodeToString(ZoomMeetingRequest.serializer(), request)
            val requestBody = body.toRequestBody(JSON_MEDIA_TYPE)

            val httpRequest = Request.Builder()
                .url("$ZOOM_API_BASE/users/me/meetings")
                .header("Authorization", "Bearer $accessToken")
                .header("Content-Type", "application/json")
                .post(requestBody)
                .build()

            val response = httpClient.newCall(httpRequest).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && responseBody != null) {
                val zoomResponse = json.decodeFromString(ZoomMeetingResponse.serializer(), responseBody)
                Logger.d(TAG, "Zoom meeting created: ${zoomResponse.id}")
                Result.Success(zoomResponse)
            } else {
                Logger.e(TAG, "Zoom API error: ${response.code} - $responseBody")
                Result.Error(Exception("Zoom API error: ${response.code}"))
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Error creating Zoom meeting", e)
            Result.Error(e)
        }
    }

    /**
     * Updates a Zoom meeting.
     */
    suspend fun updateMeeting(
        accessToken: String,
        meetingId: String,
        request: ZoomMeetingRequest
    ): Result<Unit> {
        return try {
            val body = json.encodeToString(ZoomMeetingRequest.serializer(), request)
            val requestBody = body.toRequestBody(JSON_MEDIA_TYPE)

            val httpRequest = Request.Builder()
                .url("$ZOOM_API_BASE/meetings/$meetingId")
                .header("Authorization", "Bearer $accessToken")
                .header("Content-Type", "application/json")
                .patch(requestBody)
                .build()

            val response = httpClient.newCall(httpRequest).execute()

            if (response.isSuccessful) {
                Logger.d(TAG, "Zoom meeting updated: $meetingId")
                Result.Success(Unit)
            } else {
                val body = response.body?.string()
                Logger.e(TAG, "Zoom API error: ${response.code} - $body")
                Result.Error(Exception("Zoom API error: ${response.code}"))
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Error updating Zoom meeting $meetingId", e)
            Result.Error(e)
        }
    }

    /**
     * Deletes a Zoom meeting.
     */
    suspend fun deleteMeeting(
        accessToken: String,
        meetingId: String
    ): Result<Unit> {
        return try {
            val httpRequest = Request.Builder()
                .url("$ZOOM_API_BASE/meetings/$meetingId")
                .header("Authorization", "Bearer $accessToken")
                .delete()
                .build()

            val response = httpClient.newCall(httpRequest).execute()

            if (response.isSuccessful) {
                Logger.d(TAG, "Zoom meeting deleted: $meetingId")
                Result.Success(Unit)
            } else {
                val body = response.body?.string()
                Logger.e(TAG, "Zoom API error: ${response.code} - $body")
                Result.Error(Exception("Zoom API error: ${response.code}"))
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Error deleting Zoom meeting $meetingId", e)
            Result.Error(e)
        }
    }

    /**
     * Gets meeting details from Zoom.
     */
    suspend fun getMeeting(
        accessToken: String,
        meetingId: String
    ): Result<ZoomMeetingResponse> {
        return try {
            val httpRequest = Request.Builder()
                .url("$ZOOM_API_BASE/meetings/$meetingId")
                .header("Authorization", "Bearer $accessToken")
                .get()
                .build()

            val response = httpClient.newCall(httpRequest).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && responseBody != null) {
                val zoomResponse = json.decodeFromString(ZoomMeetingResponse.serializer(), responseBody)
                Result.Success(zoomResponse)
            } else {
                Logger.e(TAG, "Zoom API error: ${response.code}")
                Result.Error(Exception("Zoom API error: ${response.code}"))
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Error getting Zoom meeting $meetingId", e)
            Result.Error(e)
        }
    }
}