package com.orbits.core.sync

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * Schedules sync operations using WorkManager.
 */
@Singleton
class SyncScheduler @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: Context,
    private val workerFactory: HiltWorkerFactory
) {

    companion object {
        private const val UNIQUE_WORK_NAME = SyncConstants.WORK_TAG_SYNC_PERIODIC
        private const val UNIQUE_ONE_TIME_NAME = SyncConstants.WORK_TAG_SYNC_ONE_TIME
    }

    private val workManager = WorkManager.getInstance(context)

    /**
     * Schedule periodic sync.
     * @param intervalMinutes Interval in minutes between syncs
     */
    fun schedulePeriodicSync(intervalMinutes: Int = SyncConstants.DEFAULT_SYNC_INTERVAL_MINUTES) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            intervalMinutes.toLong(),
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .addTag(SyncConstants.WORK_TAG_SYNC_PERIODIC)
            .build()

        workManager.enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )

        CoroutineScope(Dispatchers.IO).launch {
            // Log that sync is scheduled
        }
    }

    /**
     * Schedule a one-time sync (immediate).
     */
    fun scheduleOneTimeSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .addTag(SyncConstants.WORK_TAG_SYNC_ONE_TIME)
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        workManager.enqueueUniqueWork(
            UNIQUE_ONE_TIME_NAME,
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
    }

    /**
     * Cancel periodic sync.
     */
    fun cancelPeriodicSync() {
        workManager.cancelUniqueWork(UNIQUE_WORK_NAME)
    }

    /**
     * Cancel all sync work.
     */
    fun cancelAllSync() {
        workManager.cancelAllWorkByTag(SyncConstants.WORK_TAG_SYNC_PERIODIC)
        workManager.cancelAllWorkByTag(SyncConstants.WORK_TAG_SYNC_ONE_TIME)
    }

    /**
     * Check if periodic sync is scheduled.
     */
    fun isPeriodicSyncScheduled(): Boolean {
        val workInfos = workManager.getWorkInfosForUniqueWork(UNIQUE_WORK_NAME).get()
        return workInfos.any { it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.RUNNING }
    }

    /**
     * Get the status of the last sync.
     */
    fun getLastSyncStatus(): SyncStatus {
        // In a real implementation, this would read from persistent storage
        return SyncStatus.Idle
    }

    /**
     * Update the sync interval.
     */
    fun updateSyncInterval(intervalMinutes: Int) {
        cancelPeriodicSync()
        schedulePeriodicSync(intervalMinutes)
    }
}
