package com.orbits.data.meetings

import com.orbits.core.common.Result
import com.orbits.core.common.Logger
import com.orbits.core.common.TokenProvider
import com.orbits.core.common.extensions.nowUtc
import com.orbits.core.database.dao.MeetingDao
import com.orbits.core.database.dao.SyncQueueDao
import com.orbits.core.database.dao.TaskDao
import com.orbits.core.network.api.MeetingApi
import com.orbits.core.network.error.ApiErrorParser
import com.orbits.data.sync.SyncQueueEntity
import com.orbits.data.meetings.local.MeetingEntity
import com.orbits.data.meetings.local.MeetingParticipantEntity
import com.orbits.data.meetings.mappers.MeetingMapper
import com.orbits.data.meetings.mappers.MeetingParticipantMapper
import com.orbits.data.meetings.remote.ZoomClient
import com.orbits.data.meetings.remote.TeamsClient
import com.orbits.domain.meetings.Meeting
import com.orbits.domain.meetings.MeetingParticipant
import com.orbits.domain.meetings.MeetingRepository
import com.orbits.domain.tasks.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class MeetingRepositoryImpl @Inject constructor(
    private val meetingDao: MeetingDao,
    private val syncQueueDao: SyncQueueDao,
    private val taskDao: TaskDao,
    private val meetingApi: MeetingApi,
    private val zoomClient: ZoomClient,
    private val teamsClient: TeamsClient,
    private val meetingMapper: MeetingMapper,
    private val participantMapper: MeetingParticipantMapper,
    private val tokenProvider: TokenProvider
) : MeetingRepository {

    companion object {
        private const val TAG = "MeetingRepository"
    }

    // ─── Read Operations ──────────────────────────────────────────

    override fun getMeetings(): Flow<List<Meeting>> {
        return meetingDao.getMeetingsForUser(getUserId())
            .map { entities -> meetingMapper.toDomainList(entities) }
    }

    override fun getMeetingsByStatus(status: String): Flow<List<Meeting>> {
        return meetingDao.getMeetingsByStatus(getUserId(), status)
            .map { entities -> meetingMapper.toDomainList(entities) }
    }

    override fun getUpcomingMeetings(): Flow<List<Meeting>> {
        return meetingDao.getUpcomingMeetings(getUserId())
            .map { entities -> meetingMapper.toDomainList(entities) }
    }

    override fun getLiveMeetings(): Flow<List<Meeting>> {
        return meetingDao.getLiveMeetings(getUserId())
            .map { entities -> meetingMapper.toDomainList(entities) }
    }

    override suspend fun getMeeting(meetingId: String): Result<Meeting> {
        return try {
            val entity = meetingDao.getMeeting(meetingId)
            if (entity != null) {
                Result.Success(meetingMapper.toDomain(entity))
            } else {
                Result.Error(IllegalStateException("Meeting not found: $meetingId"))
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Error fetching meeting $meetingId", e)
            Result.Error(e)
        }
    }

    override suspend fun getMeetingByTaskId(taskId: String): Result<Meeting> {
        return try {
            val entity = meetingDao.getMeetingByTaskId(taskId)
            if (entity != null) {
                Result.Success(meetingMapper.toDomain(entity))
            } else {
                Result.Error(IllegalStateException("Meeting not found for task: $taskId"))
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Error fetching meeting for task $taskId", e)
            Result.Error(e)
        }
    }

    // ─── Participants ─────────────────────────────────────────────

    override suspend fun getParticipants(meetingId: String): Result<List<MeetingParticipant>> {
        return try {
            val entities = meetingDao.getParticipantsForMeeting(meetingId)
            Result.Success(participantMapper.toDomainList(entities))
        } catch (e: Exception) {
            Logger.e(TAG, "Error fetching participants for meeting $meetingId", e)
            Result.Error(e)
        }
    }

    override suspend fun getAcceptedParticipants(meetingId: String): Result<List<MeetingParticipant>> {
        return try {
            val entities = meetingDao.getAcceptedParticipants(meetingId)
            Result.Success(participantMapper.toDomainList(entities))
        } catch (e: Exception) {
            Logger.e(TAG, "Error fetching accepted participants for meeting $meetingId", e)
            Result.Error(e)
        }
    }

    override suspend fun addParticipant(meetingId: String, participant: MeetingParticipant): Result<MeetingParticipant> {
        return try {
            val entity = participantMapper.toEntity(participant)
            meetingDao.insertParticipant(entity)

            enqueueSync(SyncQueueEntity(
                entityType = "meeting_participant",
                operation = "create",
                entityId = participant.id,
                payloadJson = """{"meetingId":"$meetingId","email":"${participant.email}"}"""
            ))

            Logger.d(TAG, "Participant added to meeting $meetingId")
            Result.Success(participant)
        } catch (e: Exception) {
            Logger.e(TAG, "Error adding participant to meeting $meetingId", e)
            Result.Error(e)
        }
    }

    override suspend fun updateParticipantStatus(
        meetingId: String,
        email: String,
        status: String
    ): Result<Unit> {
        return try {
            meetingDao.updateParticipantStatus(meetingId, email, status)

            enqueueSync(SyncQueueEntity(
                entityType = "meeting_participant",
                operation = "update",
                entityId = email, // Using email as identifier
                payloadJson = """{"invitationStatus":"$status"}"""
            ))

            Logger.d(TAG, "Participant $email status updated to $status")
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error updating participant status $email", e)
            Result.Error(e)
        }
    }

    override suspend fun removeParticipant(meetingId: String, participantId: String): Result<Unit> {
        return try {
            enqueueSync(SyncQueueEntity(
                entityType = "meeting_participant",
                operation = "delete",
                entityId = participantId,
                payloadJson = null
            ))
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error removing participant $participantId", e)
            Result.Error(e)
        }
    }

    // ─── Write Operations ─────────────────────────────────────────

    override suspend fun createMeeting(meeting: Meeting): Result<Meeting> {
        return try {
            validateMeeting(meeting)

            // Ensure the associated task exists
            val task = taskDao.getTask(meeting.taskId)
            if (task == null) {
                return Result.Error(IllegalStateException("Task not found: ${meeting.taskId}"))
            }

            // Create meeting locally
            val entity = meetingMapper.toEntity(meeting)
            meetingDao.insertMeeting(entity)

            // Create platform-specific meeting if platform specified
            if (meeting.meetingPlatform != null) {
                createPlatformMeeting(meeting)
            }

            enqueueSync(SyncQueueEntity(
                entityType = "meeting",
                operation = "create",
                entityId = meeting.id,
                payloadJson = meetingMapper.toCreateRequest(meeting).toString()
            ))

            Logger.d(TAG, "Meeting created locally: ${meeting.id}")
            Result.Success(meeting)
        } catch (e: Exception) {
            Logger.e(TAG, "Error creating meeting", e)
            Result.Error(e)
        }
    }

    override suspend fun updateMeeting(meeting: Meeting): Result<Meeting> {
        return try {
            validateMeeting(meeting)

            // Get existing meeting
            val existing = meetingDao.getMeeting(meeting.id)
            if (existing == null) {
                return Result.Error(IllegalStateException("Meeting not found: ${meeting.id}"))
            }

            val entity = meetingMapper.toEntity(meeting).copy(updatedAt = nowUtc())
            meetingDao.updateMeeting(entity)

            // Update platform meeting if needed
            if (meeting.meetingPlatform != null && existing.meetingId != null) {
                updatePlatformMeeting(meeting)
            } else if (meeting.meetingPlatform != null && existing.meetingId == null) {
                // Meeting wasn't created on platform before, create it now
                createPlatformMeeting(meeting)
            }

            enqueueSync(SyncQueueEntity(
                entityType = "meeting",
                operation = "update",
                entityId = meeting.id,
                payloadJson = meetingMapper.toUpdateRequest(meeting).toString()
            ))

            Logger.d(TAG, "Meeting updated locally: ${meeting.id}")
            Result.Success(meeting)
        } catch (e: Exception) {
            Logger.e(TAG, "Error updating meeting ${meeting.id}", e)
            Result.Error(e)
        }
    }

    override suspend fun deleteMeeting(meetingId: String): Result<Unit> {
        return try {
            val meeting = meetingDao.getMeeting(meetingId)
            if (meeting != null && meeting.meetingId != null) {
                // Delete platform meeting if exists
                deletePlatformMeeting(meeting)
            }

            meetingDao.softDeleteMeeting(meetingId)

            enqueueSync(SyncQueueEntity(
                entityType = "meeting",
                operation = "delete",
                entityId = meetingId,
                payloadJson = null
            ))

            Logger.d(TAG, "Meeting deleted locally: $meetingId")
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error deleting meeting $meetingId", e)
            Result.Error(e)
        }
    }

    override suspend fun updateMeetingStatus(meetingId: String, status: String): Result<Unit> {
        return try {
            meetingDao.updateMeetingStatus(meetingId, status)

            enqueueSync(SyncQueueEntity(
                entityType = "meeting",
                operation = "update",
                entityId = meetingId,
                payloadJson = """{"status":"$status"}"""
            ))

            Logger.d(TAG, "Meeting status updated: $meetingId -> $status")
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error updating meeting status $meetingId", e)
            Result.Error(e)
        }
    }

    override suspend fun joinMeeting(meetingId: String): Result<String> {
        return try {
            val meeting = meetingDao.getMeeting(meetingId)
            if (meeting == null) {
                return Result.Error(IllegalStateException("Meeting not found: $meetingId"))
            }

            // Check if the meeting is live or scheduled
            if (meeting.status == "cancelled" || meeting.status == "ended") {
                return Result.Error(IllegalStateException("Meeting is ${meeting.status}"))
            }

            // Return the meeting link
            val link = meeting.meetingLink ?: return Result.Error(
                IllegalStateException("Meeting link not available")
            )

            // Update meeting status to live if it's scheduled
            if (meeting.status == "scheduled") {
                meetingDao.updateMeetingStatus(meetingId, "live")
            }

            Result.Success(link)
        } catch (e: Exception) {
            Logger.e(TAG, "Error joining meeting $meetingId", e)
            Result.Error(e)
        }
    }

    // ─── Platform-Specific Meetings ──────────────────────────────

    private suspend fun createPlatformMeeting(meeting: Meeting) {
        try {
            val token = tokenProvider.getAccessToken()
            if (token == null) {
                Logger.w(TAG, "Cannot create platform meeting: No access token")
                return
            }

            when (meeting.meetingPlatform?.lowercase()) {
                "zoom" -> {
                    val zoomRequest = meetingMapper.toZoomRequest(meeting)
                    val result = zoomClient.createMeeting(token, zoomRequest)
                    result.onSuccess { zoomResponse ->
                        // Update meeting with Zoom details
                        meetingDao.updateMeetingStatus(meeting.id, meeting.status)
                        // Update meeting link and meetingId
                    }
                    result.onError { error ->
                        Logger.e(TAG, "Failed to create Zoom meeting", error)
                    }
                }
                "teams", "microsoft_teams" -> {
                    val teamsRequest = meetingMapper.toTeamsRequest(meeting)
                    val result = teamsClient.createMeeting(token, teamsRequest)
                    result.onSuccess { teamsResponse ->
                        // Update meeting with Teams details
                    }
                    result.onError { error ->
                        Logger.e(TAG, "Failed to create Teams meeting", error)
                    }
                }
                "google_meet" -> {
                    // Handle Google Meet creation
                    Logger.d(TAG, "Google Meet creation not yet implemented")
                }
                else -> {
                    Logger.d(TAG, "Unknown meeting platform: ${meeting.meetingPlatform}")
                }
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Error creating platform meeting", e)
        }
    }

    private suspend fun updatePlatformMeeting(meeting: Meeting) {
        try {
            val token = tokenProvider.getAccessToken()
            if (token == null) {
                Logger.w(TAG, "Cannot update platform meeting: No access token")
                return
            }

            val existing = meetingDao.getMeeting(meeting.id)
            if (existing == null || existing.meetingId == null) {
                Logger.w(TAG, "No platform meeting ID found for update")
                return
            }

            when (meeting.meetingPlatform?.lowercase()) {
                "zoom" -> {
                    val zoomRequest = meetingMapper.toZoomRequest(meeting)
                    zoomClient.updateMeeting(token, existing.meetingId, zoomRequest)
                }
                "teams", "microsoft_teams" -> {
                    val teamsRequest = meetingMapper.toTeamsRequest(meeting)
                    teamsClient.updateMeeting(token, existing.meetingId, teamsRequest)
                }
                else -> {
                    Logger.d(TAG, "Unknown meeting platform: ${meeting.meetingPlatform}")
                }
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Error updating platform meeting", e)
        }
    }

    private suspend fun deletePlatformMeeting(meeting: MeetingEntity) {
        try {
            val token = tokenProvider.getAccessToken()
            if (token == null || meeting.meetingId == null) {
                Logger.w(TAG, "Cannot delete platform meeting: No token or meeting ID")
                return
            }

            when (meeting.meetingPlatform?.lowercase()) {
                "zoom" -> {
                    zoomClient.deleteMeeting(token, meeting.meetingId)
                }
                "teams", "microsoft_teams" -> {
                    teamsClient.deleteMeeting(token, meeting.meetingId)
                }
                else -> {
                    Logger.d(TAG, "Unknown meeting platform: ${meeting.meetingPlatform}")
                }
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Error deleting platform meeting", e)
        }
    }

    // ─── Private Helpers ──────────────────────────────────────────

    private fun getUserId(): String {
        // This should come from auth state
        return "test_user_id"
    }

    private suspend fun enqueueSync(item: SyncQueueEntity) {
        syncQueueDao.enqueue(item)
    }

    private fun validateMeeting(meeting: Meeting) {
        require(meeting.title.isNotBlank()) { "Meeting title cannot be empty" }
        require(meeting.title.length <= 255) { "Meeting title must be <= 255 characters" }
        require(meeting.startDateTime < meeting.endDateTime) { "End time must be after start time" }
        require(meeting.status in listOf("scheduled", "live", "ended", "cancelled")) {
            "Invalid meeting status"
        }
        if (meeting.meetingPlatform != null) {
            require(meeting.meetingPlatform in listOf("zoom", "teams", "google_meet", "custom")) {
                "Invalid meeting platform"
            }
        }
        // Additional validation as needed
    }
}