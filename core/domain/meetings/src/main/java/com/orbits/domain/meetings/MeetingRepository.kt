package com.orbits.domain.meetings

import com.orbits.core.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Meeting repository interface.
 * Defines all meeting operations.
 * Implemented by :core:data:meetings.
 */
interface MeetingRepository {

    // ─── Read Operations ──────────────────────────────────────────

    /**
     * Get all meetings for the current user.
     */
    fun getMeetings(): Flow<List<Meeting>>

    /**
     * Get meetings by status.
     */
    fun getMeetingsByStatus(status: String): Flow<List<Meeting>>

    /**
     * Get upcoming meetings.
     */
    fun getUpcomingMeetings(): Flow<List<Meeting>>

    /**
     * Get live meetings.
     */
    fun getLiveMeetings(): Flow<List<Meeting>>

    /**
     * Get a single meeting by ID.
     */
    suspend fun getMeeting(meetingId: String): Result<Meeting>

    /**
     * Get a meeting by its associated task ID.
     */
    suspend fun getMeetingByTaskId(taskId: String): Result<Meeting>

    // ─── Participants ─────────────────────────────────────────────

    /**
     * Get participants for a meeting.
     */
    suspend fun getParticipants(meetingId: String): Result<List<MeetingParticipant>>

    /**
     * Get accepted participants for a meeting.
     */
    suspend fun getAcceptedParticipants(meetingId: String): Result<List<MeetingParticipant>>

    /**
     * Add a participant to a meeting.
     */
    suspend fun addParticipant(meetingId: String, participant: MeetingParticipant): Result<MeetingParticipant>

    /**
     * Update a participant's status.
     */
    suspend fun updateParticipantStatus(meetingId: String, email: String, status: String): Result<Unit>

    /**
     * Remove a participant from a meeting.
     */
    suspend fun removeParticipant(meetingId: String, participantId: String): Result<Unit>

    // ─── Write Operations ─────────────────────────────────────────

    /**
     * Create a new meeting.
     */
    suspend fun createMeeting(meeting: Meeting): Result<Meeting>

    /**
     * Update an existing meeting.
     */
    suspend fun updateMeeting(meeting: Meeting): Result<Meeting>

    /**
     * Update meeting status (scheduled, live, ended, cancelled).
     */
    suspend fun updateMeetingStatus(meetingId: String, status: String): Result<Unit>

    /**
     * Delete a meeting.
     */
    suspend fun deleteMeeting(meetingId: String): Result<Unit>

    /**
     * Join a meeting (returns the meeting link).
     */
    suspend fun joinMeeting(meetingId: String): Result<String>
}
