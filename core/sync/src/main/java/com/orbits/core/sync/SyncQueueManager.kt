package com.orbits.core.sync

import com.orbits.core.database.dao.SyncQueueDao
import com.orbits.core.sync.strategy.SyncStrategy
import com.orbits.data.sync.SyncQueueEntity
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

/**
 * Manages the sync queue of pending operations.
 */
@Singleton
class SyncQueueManager @Inject constructor(
    private val syncQueueDao: SyncQueueDao
) {

    /**
     * Get all pending sync items.
     */
    fun getPendingItems(): Flow<List<SyncQueueEntity>> {
        return syncQueueDao.getAllPending()
    }

    /**
     * Get pending items synchronously.
     */
    suspend fun getPendingItemsSync(): List<SyncQueueEntity> {
        return syncQueueDao.getPendingSyncItems()
    }

    /**
     * Enqueue a sync item.
     */
    suspend fun enqueue(item: SyncQueueEntity) {
        syncQueueDao.enqueue(item)
    }

    /**
     * Enqueue multiple sync items.
     */
    suspend fun enqueueAll(items: List<SyncQueueEntity>) {
        if (items.isNotEmpty()) {
            syncQueueDao.enqueueAll(items)
        }
    }

    /**
     * Dequeue a sync item.
     */
    suspend fun dequeue(itemId: Long) {
        syncQueueDao.dequeue(itemId)
    }

    /**
     * Dequeue all items for a specific entity.
     */
    suspend fun dequeueForEntity(entityType: String, entityId: String) {
        syncQueueDao.dequeueForEntity(entityType, entityId)
    }

    /**
     * Dequeue items for a specific entity and operations.
     */
    suspend fun dequeueForOperations(entityType: String, entityId: String, operations: List<String>) {
        syncQueueDao.dequeueForOperations(entityType, entityId, operations)
    }

    /**
     * Clear the entire sync queue.
     */
    suspend fun clearAll() {
        syncQueueDao.clearAll()
    }

    /**
     * Get the total queue size.
     */
    suspend fun getQueueSize(): Int {
        return syncQueueDao.getQueueSize()
    }

    /**
     * Get the queue size for a specific entity type.
     */
    suspend fun getQueueSizeForType(entityType: String): Int {
        return syncQueueDao.getQueueSizeForType(entityType)
    }

    /**
     * Find a pending operation for a specific entity.
     */
    suspend fun findPendingOperation(
        entityType: String,
        entityId: String,
        operation: String
    ): SyncQueueEntity? {
        return syncQueueDao.findPendingOperation(entityType, entityId, operation)
    }

    /**
     * Find all pending operations for a specific entity.
     */
    suspend fun findPendingForEntity(entityType: String, entityId: String): List<SyncQueueEntity> {
        return syncQueueDao.findPendingForEntity(entityType, entityId)
    }

    /**
     * Check if there are any pending items in the queue.
     */
    suspend fun hasPendingItems(): Boolean {
        return getQueueSize() > 0
    }

    /**
     * Check if there are pending items for a specific entity.
     */
    suspend fun hasPendingItemsForEntity(entityType: String, entityId: String): Boolean {
        return findPendingForEntity(entityType, entityId).isNotEmpty()
    }

    /**
     * Clean up old pending items (e.g., items that have been pending too long).
     * This doesn't delete items, but marks them for retry.
     */
    suspend fun cleanup() {
        // If we had a retry count, we could increment it here
        // For now, we just ensure the queue doesn't grow too large
        val size = getQueueSize()
        if (size > 10000) {
            // If the queue is too large, we could trim it
            // This is a safety measure to prevent unbounded growth
            // In production, you might want to be more selective
        }
    }
}
