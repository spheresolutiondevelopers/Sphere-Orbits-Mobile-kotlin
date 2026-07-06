/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.orbits.data.meetings.MeetingEntity
import com.orbits.data.meetings.MeetingParticipantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MeetingDao {

    // ─── Meeting Queries ─────────────────────────────────────────

    @Query("SELECT * FROM meetings WHERE id = :meetingId AND is_deleted = 0")
    suspend fun getMeeting(meetingId: String): MeetingEntity?

    @Query("""
        SELECT * FROM meetings 
        WHERE organizer_user_id = :userId AND is_deleted = 0
        ORDER BY start_datetime DESC
    """)
    fun getMeetingsForUser(userId: String): Flow<List<MeetingEntity>>

    @Query("""
        SELECT * FROM meetings 
        WHERE organizer_user_id = :userId 
          AND is_deleted = 0 
          AND status = :status
        ORDER BY start_datetime ASC
    """)
    fun getMeetingsByStatus(userId: String, status: String): Flow<List<MeetingEntity>>

    @Query("""
        SELECT * FROM meetings 
        WHERE organizer_user_id = :userId 
          AND is_deleted = 0 
          AND start_datetime >= datetime('now')
          AND status = 'scheduled'
        ORDER BY start_datetime ASC
    """)
    fun getUpcomingMeetings(userId: String): Flow<List<MeetingEntity>>

    @Query("""
        SELECT * FROM meetings 
        WHERE organizer_user_id = :userId 
          AND is_deleted = 0 
          AND start_datetime <= datetime('now')
          AND end_datetime >= datetime('now')
          AND status = 'scheduled'
    """)
    fun getLiveMeetings(userId: String): Flow<List<MeetingEntity>>

    @Query("""
        SELECT * FROM meetings 
        WHERE task_id = :taskId AND is_deleted = 0
    """)
    suspend fun getMeetingByTaskId(taskId: String): MeetingEntity?

    // ─── Meeting CRUD ────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeeting(meeting: MeetingEntity)

    @Update
    suspend fun updateMeeting(meeting: MeetingEntity)

    @Query("UPDATE meetings SET status = :status, updated_at = datetime('now') WHERE id = :meetingId")
    suspend fun updateMeetingStatus(meetingId: String, status: String)

    @Query("UPDATE meetings SET is_deleted = 1, updated_at = datetime('now') WHERE id = :meetingId")
    suspend fun softDeleteMeeting(meetingId: String)

    @Query("DELETE FROM meetings WHERE id = :meetingId")
    suspend fun permanentlyDeleteMeeting(meetingId: String)

    @Query("""
        UPDATE meetings 
        SET synced_at = datetime('now') 
        WHERE id = :meetingId
    """)
    suspend fun markMeetingSynced(meetingId: String)

    @Query("""
        SELECT * FROM meetings 
        WHERE synced_at IS NULL OR synced_at < updated_at
    """)
    suspend fun getUnsyncedMeetings(): List<MeetingEntity>

    // ─── Meeting Participants ────────────────────────────────────

    @Query("SELECT * FROM meeting_participants WHERE meeting_id = :meetingId")
    suspend fun getParticipantsForMeeting(meetingId: String): List<MeetingParticipantEntity>

    @Query("""
        SELECT * FROM meeting_participants 
        WHERE meeting_id = :meetingId AND invitation_status = 'accepted'
    """)
    suspend fun getAcceptedParticipants(meetingId: String): List<MeetingParticipantEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipant(participant: MeetingParticipantEntity)

    @Query("""
        UPDATE meeting_participants 
        SET invitation_status = :status, 
            updated_at = datetime('now') 
        WHERE meeting_id = :meetingId AND email = :email
    """)
    suspend fun updateParticipantStatus(meetingId: String, email: String, status: String)

    @Query("DELETE FROM meeting_participants WHERE meeting_id = :meetingId")
    suspend fun deleteAllParticipantsForMeeting(meetingId: String)
}