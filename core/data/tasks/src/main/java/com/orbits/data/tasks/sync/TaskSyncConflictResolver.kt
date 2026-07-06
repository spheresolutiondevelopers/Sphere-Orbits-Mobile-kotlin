/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.data.tasks.sync

import com.orbits.data.tasks.local.TaskEntity
import com.orbits.data.tasks.mappers.TaskMapper
import com.orbits.domain.tasks.Task
import javax.inject.Inject

/**
 * Conflict resolver for task sync operations.
 * Uses Last-Write-Wins strategy based on updatedAt timestamps.
 */
internal class TaskSyncConflictResolver @Inject constructor(
    private val taskMapper: TaskMapper
) {

    /**
     * Resolve conflict between local and server versions.
     * Returns the winning version.
     */
    fun resolveConflict(
        local: TaskEntity,
        server: TaskEntity
    ): Resolution {
        val localTime = local.updatedAt
        val serverTime = server.updatedAt

        return when {
            // Server is newer: server wins
            serverTime > localTime -> Resolution(Resolution.Strategy.SERVER_WINS, server)

            // Local is newer: local wins
            localTime > serverTime -> Resolution(Resolution.Strategy.LOCAL_WINS, local)

            // Same timestamp: last write wins (server, as tiebreaker)
            else -> Resolution(Resolution.Strategy.SERVER_WINS, server)
        }
    }

    /**
     * Merge two tasks field by field (for partial updates).
     * Returns a merged TaskDomain model.
     */
    fun mergeFields(
        local: Task,
        server: Task
    ): Task {
        // For field-level merge, local takes precedence for fields it changed,
        // server for fields it changed.
        // For simplicity, we use a conservative merge: server wins for most fields,
        // but local keeps its title/description if they differ.
        return if (local.updatedAt > server.updatedAt) {
            // Local is newer: keep local fields, but server might have new data
            local.copy(
                status = if (server.status != local.status) server.status else local.status,
                updatedAt = server.updatedAt // Use server timestamp to avoid re-sync loop
            )
        } else {
            // Server is newer: server wins
            taskMapper.toDomain(server)
        }
    }
}

data class Resolution(
    val strategy: Strategy,
    val entity: TaskEntity
) {
    enum class Strategy {
        LOCAL_WINS,
        SERVER_WINS
    }
}