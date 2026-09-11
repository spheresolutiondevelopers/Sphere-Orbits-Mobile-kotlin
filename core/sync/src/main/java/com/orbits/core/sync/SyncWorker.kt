package com.orbits.core.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull

/**
 * WorkManager worker for performing sync operations in the background.
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val syncService: SyncService,
    private val syncQueueManager: SyncQueueManager
) : CoroutineWorker(context, params) {

    companion object {
        private const val TAG = SyncConstants.TAG_SYNC_WORKER
        private const val SYNC_TIMEOUT_MS = 300000L // 5 minutes
    }

    override suspend fun doWork(): Result {
        return try {
            // Check if there are pending items
            val hasPending = syncQueueManager.hasPendingItems()
            if (!hasPending) {
                // If no pending items, we could still pull changes
                // But for now, we'll just return success
                return Result.success()
            }

            // Perform sync with timeout
            val result = withTimeoutOrNull(SYNC_TIMEOUT_MS) {
                syncService.sync()
            }

            if (result == null) {
                // Timeout
                return Result.failure(workDataOf(
                    "error" to "Sync timed out after ${SYNC_TIMEOUT_MS}ms"
                ))
            }

            when {
                result.isCompleteSuccess() -> Result.success()
                result.hasConflicts() -> {
                    // Conflicts need manual resolution
                    Result.failure(workDataOf(
                        "conflicts" to result.conflicts.size.toString(),
                        "message" to "Sync completed with conflicts"
                    ))
                }
                result.success -> Result.success()
                else -> {
                    // Partial or complete failure
                    val retryCount = runAttemptCount
                    if (retryCount < SyncConstants.MAX_RETRY_COUNT) {
                        Result.retry()
                    } else {
                        Result.failure(workDataOf(
                            "error" to result.errors.joinToString { it.message ?: "Unknown error" }
                        ))
                    }
                }
            }
        } catch (e: Exception) {
            val retryCount = runAttemptCount
            if (retryCount < SyncConstants.MAX_RETRY_COUNT) {
                Result.retry()
            } else {
                Result.failure(workDataOf(
                    "error" to (e.message ?: "Unknown error")
                ))
            }
        }
    }
}
