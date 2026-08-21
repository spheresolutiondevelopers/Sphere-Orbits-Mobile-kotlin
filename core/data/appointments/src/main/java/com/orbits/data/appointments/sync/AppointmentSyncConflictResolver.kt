package com.orbits.data.appointments.sync

import com.orbits.data.appointments.local.AppointmentEntity
import javax.inject.Inject

internal class AppointmentSyncConflictResolver @Inject constructor() {

    fun resolveConflict(
        local: AppointmentEntity,
        server: AppointmentEntity
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
     * Conservative merge with external sync preserved.
     */
    fun mergeFields(
        local: AppointmentEntity,
        server: AppointmentEntity
    ): AppointmentEntity {
        return if (local.updatedAt > server.updatedAt) {
            // Local is newer: keep local fields
            local.copy(
                // Server might have external sync data
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
    val entity: AppointmentEntity
) {
    enum class Strategy {
        LOCAL_WINS,
        SERVER_WINS
    }
}