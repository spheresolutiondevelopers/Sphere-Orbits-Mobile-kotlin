/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.core.network.api

import retrofit2.http.Body
import retrofit2.http.POST

@kotlinx.serialization.Serializable
data class SyncRequest(
    val lastSyncTimestamp: String,
    val changes: List<Change>? = null
)

@kotlinx.serialization.Serializable
data class Change(
    val entityType: String, // "task", "appointment", "note", etc.
    val operation: String, // "create", "update", "delete"
    val entityId: String,
    val entityData: String? // JSON string of the entity
)

@kotlinx.serialization.Serializable
data class SyncResponse(
    val serverTimestamp: String,
    val changes: List<ServerChange>
)

@kotlinx.serialization.Serializable
data class ServerChange(
    val entityType: String,
    val operation: String,
    val entityId: String,
    val entity: String // JSON string
)

interface SyncApi {

    @POST("api/v1/sync")
    suspend fun sync(
        @Body request: SyncRequest
    ): SyncResponse
}