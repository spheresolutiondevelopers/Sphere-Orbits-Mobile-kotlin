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
import com.orbits.data.sync.SyncQueueEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncQueueDao {

    @Query("SELECT * FROM sync_queue ORDER BY created_at ASC")
    fun getAllPending(): Flow<List<SyncQueueEntity>>

    @Query("SELECT * FROM sync_queue ORDER BY created_at ASC")
    suspend fun getPendingSyncItems(): List<SyncQueueEntity>

    @Query("""
        SELECT * FROM sync_queue 
        WHERE entity_type = :entityType 
          AND entity_id = :entityId 
          AND operation = :operation
    """)
    suspend fun findPendingOperation(
        entityType: String,
        entityId: String,
        operation: String
    ): SyncQueueEntity?

    @Query("""
        SELECT * FROM sync_queue 
        WHERE entity_type = :entityType 
          AND entity_id = :entityId
    """)
    suspend fun findPendingForEntity(entityType: String, entityId: String): List<SyncQueueEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enqueue(item: SyncQueueEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enqueueAll(items: List<SyncQueueEntity>)

    @Update
    suspend fun update(item: SyncQueueEntity)

    @Query("DELETE FROM sync_queue WHERE id = :id")
    suspend fun dequeue(id: Long)

    @Query("DELETE FROM sync_queue WHERE entity_type = :entityType AND entity_id = :entityId")
    suspend fun dequeueForEntity(entityType: String, entityId: String)

    @Query("""
        DELETE FROM sync_queue 
        WHERE entity_type = :entityType 
          AND entity_id = :entityId 
          AND operation IN (:operations)
    """)
    suspend fun dequeueForOperations(
        entityType: String,
        entityId: String,
        operations: List<String>
    )

    @Query("DELETE FROM sync_queue")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM sync_queue")
    suspend fun getQueueSize(): Int

    @Query("""
        SELECT COUNT(*) FROM sync_queue 
        WHERE entity_type = :entityType
    """)
    suspend fun getQueueSizeForType(entityType: String): Int
}