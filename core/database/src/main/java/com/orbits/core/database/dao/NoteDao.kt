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
import com.orbits.data.notes.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes WHERE id = :noteId AND is_deleted = 0")
    suspend fun getNote(noteId: String): NoteEntity?

    @Query("""
        SELECT * FROM notes 
        WHERE user_id = :userId AND is_deleted = 0
        ORDER BY updated_at DESC
    """)
    fun getNotesForUser(userId: String): Flow<List<NoteEntity>>

    @Query("""
        SELECT * FROM notes 
        WHERE user_id = :userId 
          AND is_deleted = 0 
          AND (title LIKE '%' || :search || '%' OR content LIKE '%' || :search || '%')
        ORDER BY updated_at DESC
    """)
    fun searchNotes(userId: String, search: String): Flow<List<NoteEntity>>

    @Query("""
        SELECT * FROM notes 
        WHERE user_id = :userId 
          AND is_deleted = 0 
          AND task_id = :taskId
        ORDER BY updated_at DESC
    """)
    fun getNotesForTask(userId: String, taskId: String): Flow<List<NoteEntity>>

    @Query("""
        SELECT * FROM notes 
        WHERE user_id = :userId 
          AND is_deleted = 0 
          AND event_id = :eventId
        ORDER BY updated_at DESC
    """)
    fun getNotesForEvent(userId: String, eventId: String): Flow<List<NoteEntity>>

    @Query("""
        SELECT * FROM notes 
        WHERE user_id = :userId 
          AND is_deleted = 0 
          AND appointment_id = :appointmentId
        ORDER BY updated_at DESC
    """)
    fun getNotesForAppointment(userId: String, appointmentId: String): Flow<List<NoteEntity>>

    @Query("""
        SELECT * FROM notes 
        WHERE user_id = :userId 
          AND is_deleted = 0 
          AND meeting_id = :meetingId
        ORDER BY updated_at DESC
    """)
    fun getNotesForMeeting(userId: String, meetingId: String): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Query("UPDATE notes SET is_deleted = 1, updated_at = datetime('now') WHERE id = :noteId")
    suspend fun softDeleteNote(noteId: String)

    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun permanentlyDeleteNote(noteId: String)

    @Query("""
        UPDATE notes 
        SET synced_at = datetime('now') 
        WHERE id = :noteId
    """)
    suspend fun markNoteSynced(noteId: String)

    @Query("""
        SELECT * FROM notes 
        WHERE synced_at IS NULL OR synced_at < updated_at
    """)
    suspend fun getUnsyncedNotes(): List<NoteEntity>
}