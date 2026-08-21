package com.orbits.data.meetings.remote

import com.orbits.core.common.Result
import com.orbits.core.common.Logger
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class TeamsClient @Inject constructor() {

    companion object {
        private const val TAG = "TeamsClient"
        private const val GRAPH_API_BASE = "https://graph.microsoft.com/v1.0"
        private val JSON_MEDIA_TYPE = "application/json".toMediaType()
    }

    private val httpClient = OkHttpClient.Builder()
        .build()

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    /**
     * Creates a Microsoft Teams meeting.
     */
    suspend fun createMeeting(
        accessToken: String,
        request: TeamsMeetingRequest
    ): Result<TeamsMeetingResponse> {
        return try {
            val body = json.encodeToString(TeamsMeetingRequest.serializer(), request)
            val requestBody = body.toRequestBody(JSON_MEDIA_TYPE)

            val httpRequest = Request.Builder()
                .url("$GRAPH_API_BASE/users/me/onlineMeetings")
                .header("Authorization", "Bearer $accessToken")
                .header("Content-Type", "application/json")
                .post(requestBody)
                .build()

            val response = httpClient.newCall(httpRequest).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && responseBody != null) {
                val teamsResponse = json.decodeFromString(TeamsMeetingResponse.serializer(), responseBody)
                Logger.d(TAG, "Teams meeting created")
                Result.Success(teamsResponse)
            } else {
                Logger.e(TAG, "Teams API error: ${response.code}")
                Result.Error(Exception("Teams API error: ${response.code}"))
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Error creating Teams meeting", e)
            Result.Error(e)
        }
    }

    /**
     * Updates a Teams meeting.
     */
    suspend fun updateMeeting(
        accessToken: String,
        meetingId: String,
        request: TeamsMeetingRequest
    ): Result<Unit> {
        return try {
            val body = json.encodeToString(TeamsMeetingRequest.serializer(), request)
            val requestBody = body.toRequestBody(JSON_MEDIA_TYPE)

            val httpRequest = Request.Builder()
                .url("$GRAPH_API_BASE/users/me/onlineMeetings/$meetingId")
                .header("Authorization", "Bearer $accessToken")
                .header("Content-Type", "application/json")
                .patch(requestBody)
                .build()

            val response = httpClient.newCall(httpRequest).execute()

            if (response.isSuccessful) {
                Logger.d(TAG, "Teams meeting updated: $meetingId")
                Result.Success(Unit)
            } else {
                Logger.e(TAG, "Teams API error: ${response.code}")
                Result.Error(Exception("Teams API error: ${response.code}"))
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Error updating Teams meeting $meetingId", e)
            Result.Error(e)
        }
    }

    /**
     * Deletes a Teams meeting.
     */
    suspend fun deleteMeeting(
        accessToken: String,
        meetingId: String
    ): Result<Unit> {
        return try {
            val httpRequest = Request.Builder()
                .url("$GRAPH_API_BASE/users/me/onlineMeetings/$meetingId")
                .header("Authorization", "Bearer $accessToken")
                .delete()
                .build()

            val response = httpClient.newCall(httpRequest).execute()

            if (response.isSuccessful) {
                Logger.d(TAG, "Teams meeting deleted: $meetingId")
                Result.Success(Unit)
            } else {
                Logger.e(TAG, "Teams API error: ${response.code}")
                Result.Error(Exception("Teams API error: ${response.code}"))
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Error deleting Teams meeting $meetingId", e)
            Result.Error(e)
        }
    }
}

// Response model for Teams
@kotlinx.serialization.Serializable
internal data class TeamsMeetingResponse(
    val id: String,
    val joinUrl: String,
    val subject: String,
    val startDateTime: String,
    val endDateTime: String,
    val joinWebUrl: String?
)