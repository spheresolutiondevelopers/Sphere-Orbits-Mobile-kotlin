package com.orbits.data.auth.sync

import com.orbits.data.auth.AuthEntity
import javax.inject.Inject

internal class AuthConflictResolver @Inject constructor() {

    fun resolveConflict(
        local: AuthEntity,
        server: AuthEntity
    ): Resolution {
        val localTime = local.updatedAt
        val serverTime = server.updatedAt

        return if (serverTime > localTime) {
            Resolution(Resolution.Strategy.SERVER_WINS, server)
        } else if (localTime > serverTime) {
            Resolution(Resolution.Strategy.LOCAL_WINS, local)
        } else {
            // Tie-breaker: server wins
            Resolution(Resolution.Strategy.SERVER_WINS, server)
        }
    }
}

data class Resolution(
    val strategy: Strategy,
    val entity: AuthEntity
) {
    enum class Strategy {
        LOCAL_WINS,
        SERVER_WINS
    }
}