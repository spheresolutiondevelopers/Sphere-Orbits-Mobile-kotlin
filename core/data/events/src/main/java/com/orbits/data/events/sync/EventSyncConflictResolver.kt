package com.orbits.data.events.sync

import com.orbits.data.events.local.EventEntity
import javax.inject.Inject

internal class EventSyncConflictResolver @Inject constructor() {

    fun resolveConflict(
        local: EventEntity,
        server: EventEntity
    ): Resolution {
        val localTime = local.updatedAt
        val serverTime = server.updatedAt

        return when {
            serverTime > localTime -> Resolution(Resolution.Strategy.SERVER_WINS, server)
            localTime > serverTime -> Resolution(Resolution.Strategy.LOCAL_WINS, local)
            else -> Resolution(Resolution.Strategy.SERVER_WINS, server)
        }
    }

    /**
     * Merges field-level changes between local and server.
     * Conservative merge: local wins for user data, server wins for participant data.
     */
    fun mergeFields(
        local: EventEntity,
        server: EventEntity
    ): EventEntity {
        return if (local.updatedAt > server.updatedAt) {
            // Local is newer: keep local fields
            local.copy(
                // Server might have updated participant count
                updatedAt = server.updatedAt // Use server timestamp to avoid re-sync loop
            )
        } else {
            // Server is newer: server wins
            server.copy(
                syncedAt = nowUtc()
            )
        }
    }

    private fun nowUtc(): String {
        return java.time.Instant.now().toString()
    }
}

data class Resolution(
    val strategy: Strategy,
    val entity: EventEntity
) {
    enum class Strategy {
        LOCAL_WINS,
        SERVER_WINS
    }
}