package com.orbits.core.sync

import kotlinx.serialization.Serializable

/**
 * Data structure for sync payloads.
 */
@Serializable
data class SyncData(
    val entityType: String,
    val operation: String,
    val entityId: String,
    val data: String? = null, // JSON serialized entity
    val version: String? = null,
    val timestamp: String = java.time.Instant.now().toString()
)

/**
 * Data structure for sync response.
 */
@Serializable
data class SyncResponse(
    val success: Boolean,
    val entityType: String,
    val entityId: String,
    val version: String? = null,
    val data: String? = null, // Updated entity data
    val error: String? = null,
    val conflict: Boolean = false,
    val serverVersion: String? = null,
    val serverData: String? = null
)

/**
 * Data structure for batch sync request.
 */
@Serializable
data class BatchSyncRequest(
    val items: List<SyncData>,
    val lastSyncTimestamp: String? = null
)

/**
 * Data structure for batch sync response.
 */
@Serializable
data class BatchSyncResponse(
    val results: List<SyncResponse>,
    val serverTimestamp: String
)
