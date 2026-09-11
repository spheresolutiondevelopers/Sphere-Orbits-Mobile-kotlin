package com.orbits.core.sync.strategy

import com.orbits.core.sync.SyncConflict
import com.orbits.core.sync.SyncData
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Server Wins conflict resolution strategy.
 * The server's version always wins.
 */
@Singleton
class ServerWinsStrategy @Inject constructor() : SyncStrategy {

    override fun getName(): String = "ServerWins"

    override fun getDescription(): String = "The server's version always wins the conflict."

    override fun resolve(
        localData: SyncData,
        serverData: SyncData,
        entityType: String,
        entityId: String
    ): SyncConflictResolution {
        val conflict = SyncConflict(
            entityType = entityType,
            entityId = entityId,
            localVersion = localData.timestamp,
            serverVersion = serverData.timestamp,
            localData = localData.data,
            serverData = serverData.data,
            resolution = "server_wins"
        )

        return SyncConflictResolution(
            conflict = conflict,
            resolution = "server_wins",
            resolvedData = serverData.data
        )
    }
}
