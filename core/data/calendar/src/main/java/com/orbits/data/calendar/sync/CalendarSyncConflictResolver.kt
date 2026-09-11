package com.orbits.data.calendar.sync

import com.orbits.data.calendar.CalendarEventEntity
import javax.inject.Inject

internal class CalendarSyncConflictResolver @Inject constructor() {

    fun resolveConflict(
        local: CalendarEventEntity,
        server: CalendarEventEntity
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
     * Conservative merge: local wins for user data, server wins for external data.
     */
    fun mergeFields(
        local: CalendarEventEntity,
        server: CalendarEventEntity
    ): CalendarEventEntity {
        return if (local.updatedAt > server.updatedAt) {
            // Local is newer: keep local fields, but server has external sync data
            local.copy(
                externalEventId = server.externalEventId ?: local.externalEventId,
                externalSyncStatus = server.externalSyncStatus,
                syncedAt = server.updatedAt
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
    val entity: CalendarEventEntity
) {
    enum class Strategy {
        LOCAL_WINS,
        SERVER_WINS
    }
}