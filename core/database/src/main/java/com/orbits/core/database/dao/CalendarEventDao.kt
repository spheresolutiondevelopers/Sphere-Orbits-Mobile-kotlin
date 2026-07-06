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
import com.orbits.data.calendar.CalendarEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarEventDao {

    @Query("SELECT * FROM calendar_events WHERE id = :eventId AND is_deleted = 0")
    suspend fun getEvent(eventId: String): CalendarEventEntity?

    @Query("""
        SELECT * FROM calendar_events 
        WHERE user_id = :userId 
          AND is_deleted = 0 
          AND ((start_datetime BETWEEN :startDate AND :endDate) 
               OR (end_datetime BETWEEN :startDate AND :endDate)
               OR (start_datetime <= :startDate AND end_datetime >= :endDate))
        ORDER BY start_datetime ASC
    """)
    fun getEventsInDateRange(
        userId: String,
        startDate: String,
        endDate: String
    ): Flow<List<CalendarEventEntity>>

    @Query("""
        SELECT * FROM calendar_events 
        WHERE user_id = :userId 
          AND is_deleted = 0 
          AND date(start_datetime) = date(:date)
        ORDER BY start_datetime ASC
    """)
    fun getEventsForDate(userId: String, date: String): Flow<List<CalendarEventEntity>>

    @Query("""
        SELECT * FROM calendar_events 
        WHERE user_id = :userId 
          AND is_deleted = 0 
          AND status = :status
        ORDER BY start_datetime ASC
    """)
    fun getEventsByStatus(userId: String, status: String): Flow<List<CalendarEventEntity>>

    @Query("SELECT * FROM calendar_events WHERE external_event_id = :externalId AND user_id = :userId")
    suspend fun getEventByExternalId(userId: String, externalId: String): CalendarEventEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: CalendarEventEntity)

    @Update
    suspend fun updateEvent(event: CalendarEventEntity)

    @Query("UPDATE calendar_events SET is_deleted = 1, updated_at = datetime('now') WHERE id = :eventId")
    suspend fun softDeleteEvent(eventId: String)

    @Query("DELETE FROM calendar_events WHERE id = :eventId")
    suspend fun permanentlyDeleteEvent(eventId: String)

    @Query("""
        UPDATE calendar_events 
        SET synced_at = datetime('now') 
        WHERE id = :eventId
    """)
    suspend fun markEventSynced(eventId: String)

    @Query("""
        SELECT * FROM calendar_events 
        WHERE synced_at IS NULL OR synced_at < updated_at
    """)
    suspend fun getUnsyncedEvents(): List<CalendarEventEntity>

    @Query("""
        UPDATE calendar_events 
        SET external_sync_status = :status, 
            updated_at = datetime('now') 
        WHERE id = :eventId
    """)
    suspend fun updateSyncStatus(eventId: String, status: String)
}