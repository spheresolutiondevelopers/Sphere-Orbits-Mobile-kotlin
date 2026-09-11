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
import androidx.room.Transaction
import com.orbits.data.events.EventEntity
import com.orbits.data.events.EventParticipantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Query("SELECT * FROM events WHERE id = :eventId AND is_deleted = 0")
    suspend fun getEvent(eventId: String): EventEntity?

    @Query("SELECT * FROM events WHERE user_id = :userId AND is_deleted = 0 ORDER BY start_datetime DESC")
    fun getEventsForUser(userId: String): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE user_id = :userId AND status = :status AND is_deleted = 0 ORDER BY start_datetime DESC")
    fun getEventsByStatus(userId: String, status: String): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE user_id = :userId AND start_datetime >= :startDate AND end_datetime <= :endDate AND is_deleted = 0 ORDER BY start_datetime DESC")
    fun getEventsInDateRange(userId: String, startDate: String, endDate: String): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE user_id = :userId AND category_id = :categoryId AND is_deleted = 0 ORDER BY start_datetime DESC")
    fun getEventsByCategory(userId: String, categoryId: String): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE user_id = :userId AND start_datetime >= datetime('now') AND is_deleted = 0 ORDER BY start_datetime ASC")
    fun getUpcomingEvents(userId: String): Flow<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity)

    @Update
    suspend fun updateEvent(event: EventEntity)

    @Query("UPDATE events SET is_deleted = 1, updated_at = :timestamp WHERE id = :eventId")
    suspend fun softDeleteEvent(eventId: String, timestamp: String)

    @Query("UPDATE events SET is_deleted = 0, updated_at = :timestamp WHERE id = :eventId")
    suspend fun restoreEvent(eventId: String, timestamp: String)

    @Query("SELECT * FROM events WHERE sync_status = 'pending'")
    suspend fun getUnsyncedEvents(): List<EventEntity>

    @Query("DELETE FROM events WHERE id = :eventId")
    suspend fun permanentlyDeleteEvent(eventId: String)

    // Participants

    @Query("SELECT * FROM event_participants WHERE event_id = :eventId")
    fun getParticipantsForEvent(eventId: String): Flow<List<EventParticipantEntity>>

    @Query("SELECT * FROM event_participants WHERE event_id = :eventId")
    suspend fun getParticipantsForEventSuspend(eventId: String): List<EventParticipantEntity>

    @Query("SELECT * FROM event_participants WHERE event_id = :eventId AND invitation_status = 'confirmed'")
    suspend fun getConfirmedParticipantsForEvent(eventId: String): List<EventParticipantEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipant(participant: EventParticipantEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipants(participants: List<EventParticipantEntity>)

    @Query("UPDATE event_participants SET invitation_status = :status, updated_at = :timestamp WHERE event_id = :eventId AND id = :participantId")
    suspend fun updateParticipantStatus(eventId: String, participantId: String, status: String, timestamp: String)

    @Query("DELETE FROM event_participants WHERE event_id = :eventId")
    suspend fun deleteParticipantsForEvent(eventId: String)

    @Transaction
    suspend fun updateEventWithParticipants(event: EventEntity, participants: List<EventParticipantEntity>) {
        insertEvent(event)
        deleteParticipantsForEvent(event.id)
        insertParticipants(participants)
    }
}
