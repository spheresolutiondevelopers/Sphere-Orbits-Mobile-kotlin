package com.orbits.core.sync.strategy

import com.orbits.core.sync.SyncConflict
import com.orbits.core.sync.SyncData

/**
 * Interface for conflict resolution strategies.
 */
interface SyncStrategy {

    /**
     * Get the strategy name.
     */
    fun getName(): String

    /**
     * Resolve a sync conflict.
     * @param localData The local entity data
     * @param serverData The server entity data
     * @param entityType The entity type
     * @param entityId The entity ID
     * @return The resolved conflict with the chosen resolution
     */
    fun resolve(
        localData: SyncData,
        serverData: SyncData,
        entityType: String,
        entityId: String
    ): SyncConflictResolution

    /**
     * Determine if the strategy supports automatic resolution.
     */
    fun isAutomatic(): Boolean = true

    /**
     * Get a description of the strategy.
     */
    fun getDescription(): String
}

/**
 * Result of a conflict resolution.
 */
data class SyncConflictResolution(
    val conflict: SyncConflict,
    val resolution: String, // "local_wins", "server_wins", "manual"
    val resolvedData: String? = null // The resolved entity data
) {

    /**
     * Check if the conflict was resolved automatically.
     */
    fun isAutomatic(): Boolean = resolution != "manual"

    /**
     * Get the resolution display name.
     */
    fun getResolutionDisplayName(): String {
        return when (resolution) {
            "local_wins" -> "Local Version Kept"
            "server_wins" -> "Server Version Applied"
            "manual" -> "Manual Resolution Required"
            else -> resolution
        }
    }
}
