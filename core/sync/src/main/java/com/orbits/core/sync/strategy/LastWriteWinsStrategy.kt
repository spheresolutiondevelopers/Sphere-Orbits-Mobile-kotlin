package com.orbits.core.sync.strategy

import com.orbits.core.sync.SyncConflict
import com.orbits.core.sync.SyncData
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Last Write Wins conflict resolution strategy.
 * The entity with the latest timestamp wins.
 */
@Singleton
class LastWriteWinsStrategy @Inject constructor() : SyncStrategy {

    override fun getName(): String = "LastWriteWins"

    override fun getDescription(): String = "The entity with the latest timestamp wins the conflict."

    override fun resolve(
        localData: SyncData,
        serverData: SyncData,
        entityType: String,
        entityId: String
    ): SyncConflictResolution {
        val localTime = localData.timestamp
        val serverTime = serverData.timestamp

        // Compare timestamps
        val resolution = when {
            serverTime > localTime -> "server_wins"
            localTime > serverTime -> "local_wins"
            else -> "server_wins" // Tie-breaker: server wins
        }

        val resolvedData = when (resolution) {
            "server_wins" -> serverData.data
            "local_wins" -> localData.data
            else -> serverData.data
        }

        val conflict = SyncConflict(
            entityType = entityType,
            entityId = entityId,
            localVersion = localTime,
            serverVersion = serverTime,
            localData = localData.data,
            serverData = serverData.data,
            resolution = resolution
        )

        return SyncConflictResolution(
            conflict = conflict,
            resolution = resolution,
            resolvedData = resolvedData
        )
    }
}
