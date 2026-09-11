package com.orbits.core.sync

import com.orbits.core.common.Logger
import com.orbits.core.common.TokenProvider
import com.orbits.core.common.extensions.nowUtc
import com.orbits.core.database.dao.SyncQueueDao
import com.orbits.core.network.api.SyncApi
import com.orbits.core.network.api.SyncRequest
import com.orbits.core.network.api.SyncResponse
import com.orbits.core.network.api.Change
import com.orbits.core.network.api.ServerChange
import com.orbits.core.sync.strategy.SyncStrategy
import com.orbits.data.sync.SyncQueueEntity
import com.orbits.domain.auth.AuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Core sync service that orchestrates the sync process.
 */
@Singleton
class SyncService @Inject constructor(
    private val syncApi: SyncApi,
    private val syncQueueDao: SyncQueueDao,
    private val syncQueueManager: SyncQueueManager,
    private val conflictResolver: ConflictResolver,
    private val authRepository: AuthRepository,
    private val tokenProvider: TokenProvider,
    private val json: Json
) {

    companion object {
        private const val TAG = SyncConstants.TAG_SYNC
    }

    /**
     * Perform a full sync with the server.
     * Pushes pending changes and pulls new changes.
     */
    suspend fun sync(): SyncResult {
        Logger.d(TAG, "Starting full sync")

        // Check if user is local (offline mode)
        val user = authRepository.getCurrentUser()
        if (user?.isLocal == true) {
            Logger.d(TAG, "User is local, skipping remote sync")
            return SyncResult.success(0)
        }

        // Check authentication
        val token = tokenProvider.getAccessToken()
        if (token == null) {
            Logger.w(TAG, "Cannot sync: Not authenticated")
            return SyncResult.failure(IllegalStateException("Not authenticated"))
        }

        // Get pending items
        val pendingItems = syncQueueManager.getPendingItemsSync()
        Logger.d(TAG, "Found ${pendingItems.size} pending sync items")

        if (pendingItems.isNotEmpty()) {
            // Push pending changes
            val pushResult = pushChanges(pendingItems)
            if (!pushResult.success) {
                Logger.e(TAG, "Push failed: ${pushResult.errors}")
                return pushResult
            }
        }

        // Pull changes from server
        val pullResult = pullChanges(token)
        return pullResult
    }

    /**
     * Push pending changes to the server.
     */
    private suspend fun pushChanges(items: List<SyncQueueEntity>): SyncResult {
        Logger.d(TAG, "Pushing ${items.size} changes")

        val changes = items.mapNotNull { item ->
            try {
                Change(
                    entityType = item.entityType,
                    operation = item.operation,
                    entityId = item.entityId,
                    entityData = item.payloadJson
                )
            } catch (e: Exception) {
                Logger.e(TAG, "Error mapping item $item", e)
                null
            }
        }

        if (changes.isEmpty()) {
            return SyncResult.success(0)
        }

        // Process in batches to avoid overloading the server
        var totalSynced = 0
        var totalFailed = 0
        val allErrors = mutableListOf<Throwable>()
        val allConflicts = mutableListOf<SyncConflict>()

        for (batch in changes.chunked(SyncConstants.MAX_BATCH_SIZE)) {
            try {
                val request = SyncRequest(
                    lastSyncTimestamp = nowUtc(),
                    changes = batch
                )

                val response = syncApi.sync(request)

                // Process the response
                response.changes.forEach { serverChange ->
                    when {
                        serverChange.operation == "conflict" -> {
                            // Handle conflict
                            val conflict = handleConflict(serverChange)
                            if (conflict != null) {
                                allConflicts.add(conflict)
                            }
                            totalFailed++
                        }
                        else -> {
                            // Success
                            totalSynced++
                            // Dequeue the item
                            val item = items.find { it.entityId == serverChange.entityId }
                            item?.let {
                                syncQueueManager.dequeue(it.id)
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                Logger.e(TAG, "Error pushing batch", e)
                allErrors.add(e)
                totalFailed += batch.size
            }
        }

        return if (allErrors.isEmpty() && allConflicts.isEmpty()) {
            SyncResult.success(totalSynced)
        } else if (allConflicts.isNotEmpty()) {
            SyncResult.withConflicts(totalSynced, allConflicts)
        } else {
            SyncResult.partial(totalSynced, totalFailed, allErrors)
        }
    }

    /**
     * Pull changes from the server.
     */
    private suspend fun pullChanges(token: String): SyncResult {
        Logger.d(TAG, "Pulling changes from server")

        return try {
            // Request changes from server (simplified)
            // In a real implementation, we'd include a last sync timestamp
            // and the server would return only new changes

            // For now, we just return success
            // The actual pull is handled by individual repository sync methods

            SyncResult.success(0)
        } catch (e: Exception) {
            Logger.e(TAG, "Error pulling changes", e)
            SyncResult.failure(e)
        }
    }

    /**
     * Handle a sync conflict.
     */
    private suspend fun handleConflict(serverChange: ServerChange): SyncConflict? {
        return try {
            val entityType = serverChange.entityType
            val entityId = serverChange.entityId

            // Find the local item
            val localItems = syncQueueManager.findPendingForEntity(entityType, entityId)
            val localItem = localItems.firstOrNull()

            if (localItem == null) {
                Logger.w(TAG, "No local item found for conflict: $entityType/$entityId")
                return null
            }

            // Create sync data objects
            val localData = SyncData(
                entityType = entityType,
                operation = localItem.operation,
                entityId = localItem.entityId,
                data = localItem.payloadJson,
                version = localItem.updatedAt
            )

            val serverData = SyncData(
                entityType = entityType,
                operation = "server_update",
                entityId = entityId,
                data = serverChange.entity,
                version = nowUtc()
            )

            // Resolve the conflict
            val resolution = conflictResolver.resolveConflict(
                localData = localData,
                serverData = serverData,
                entityType = entityType,
                entityId = entityId
            )

            Logger.d(TAG, "Conflict resolved: ${resolution.resolution}")

            if (resolution.isAutomatic()) {
                // Apply the resolution
                when (resolution.resolution) {
                    "local_wins" -> {
                        // Keep local changes, re-enqueue
                        // The item stays in the queue
                    }
                    "server_wins" -> {
                        // Use server data, dequeue local item
                        syncQueueManager.dequeue(localItem.id)
                    }
                }
            }

            resolution.conflict
        } catch (e: Exception) {
            Logger.e(TAG, "Error handling conflict", e)
            null
        }
    }
}
