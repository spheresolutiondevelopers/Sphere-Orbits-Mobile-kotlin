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
import com.orbits.data.analytics.AnalyticsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnalyticsDao {

    @Query("""
        SELECT * FROM analytics_events 
        WHERE user_id = :userId 
        ORDER BY event_timestamp DESC
        LIMIT :limit
    """)
    fun getAnalyticsEvents(userId: String, limit: Int): Flow<List<AnalyticsEntity>>

    @Query("""
        SELECT * FROM analytics_events 
        WHERE user_id = :userId 
          AND event_type = :eventType 
          AND event_timestamp BETWEEN :startDate AND :endDate
        ORDER BY event_timestamp DESC
    """)
    fun getAnalyticsEventsByType(
        userId: String,
        eventType: String,
        startDate: String,
        endDate: String
    ): Flow<List<AnalyticsEntity>>

    @Query("""
        SELECT COUNT(*) FROM analytics_events 
        WHERE user_id = :userId 
          AND event_type = :eventType
          AND event_timestamp >= datetime('now', '-7 days')
    """)
    fun getEventCountLast7Days(userId: String, eventType: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAnalyticsEvent(event: AnalyticsEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAnalyticsEvents(events: List<AnalyticsEntity>)

    @Query("DELETE FROM analytics_events WHERE event_timestamp < datetime('now', '-90 days')")
    suspend fun deleteOldAnalyticsEvents()

    @Query("DELETE FROM analytics_events WHERE user_id = :userId")
    suspend fun deleteAllAnalyticsForUser(userId: String)
}