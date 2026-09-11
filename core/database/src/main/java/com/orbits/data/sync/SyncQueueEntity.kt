/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.data.sync

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.orbits.core.common.extensions.nowUtc

/**
 * Sync Queue Entity — stores pending operations for offline-first sync.
 * ISOLATED in :core:database so both the sync module and data modules can reference it.
 *
 * This entity is used by SyncQueueManager to track operations that need to be
 * synced with the server when connectivity is restored.
 */
@Entity(
    tableName = "sync_queue",
    indices = [
        Index(value = ["entity_type", "entity_id", "operation"]),
        Index(value = ["created_at"])
    ]
)
data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "entity_type")
    val entityType: String, // "task", "appointment", "note", etc.

    @ColumnInfo(name = "operation")
    val operation: String, // "create", "update", "delete"

    @ColumnInfo(name = "entity_id")
    val entityId: String,

    @ColumnInfo(name = "payload_json")
    val payloadJson: String? = null, // JSON representation of the entity data

    @ColumnInfo(name = "priority")
    val priority: Int = 0, // Higher = higher priority (e.g., 1 for user-initiated, 0 for background)

    @ColumnInfo(name = "created_at")
    val createdAt: String = nowUtc(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: String = nowUtc(),

    @ColumnInfo(name = "attempt_count")
    val attemptCount: Int = 0
)
