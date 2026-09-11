package com.orbits.core.sync

/**
 * Result of a sync operation with detailed information.
 */
data class SyncResult(
    val success: Boolean,
    val itemsSynced: Int = 0,
    val itemsFailed: Int = 0,
    val conflicts: List<SyncConflict> = emptyList(),
    val errors: List<Throwable> = emptyList(),
    val timestamp: String = java.time.Instant.now().toString()
) {

    /**
     * Check if there were conflicts.
     */
    fun hasConflicts(): Boolean = conflicts.isNotEmpty()

    /**
     * Check if there were errors.
     */
    fun hasErrors(): Boolean = errors.isNotEmpty()

    /**
     * Get the total number of items processed.
     */
    fun getTotalItemsProcessed(): Int = itemsSynced + itemsFailed

    /**
     * Get the success rate as a percentage.
     */
    fun getSuccessRate(): Double {
        val total = getTotalItemsProcessed()
        return if (total > 0) {
            (itemsSynced.toDouble() / total) * 100
        } else {
            100.0
        }
    }

    /**
     * Get the formatted success rate.
     */
    fun getFormattedSuccessRate(): String {
        return "${String.format("%.1f", getSuccessRate())}%"
    }

    /**
     * Check if the sync was completely successful.
     */
    fun isCompleteSuccess(): Boolean = success && itemsFailed == 0 && conflicts.isEmpty() && errors.isEmpty()

    /**
     * Combine two sync results.
     */
    fun combine(other: SyncResult): SyncResult {
        return SyncResult(
            success = this.success && other.success,
            itemsSynced = this.itemsSynced + other.itemsSynced,
            itemsFailed = this.itemsFailed + other.itemsFailed,
            conflicts = this.conflicts + other.conflicts,
            errors = this.errors + other.errors,
            timestamp = java.time.Instant.now().toString()
        )
    }

    companion object {
        fun success(itemsSynced: Int): SyncResult {
            return SyncResult(
                success = true,
                itemsSynced = itemsSynced
            )
        }

        fun failure(error: Throwable): SyncResult {
            return SyncResult(
                success = false,
                errors = listOf(error)
            )
        }

        fun partial(itemsSynced: Int, itemsFailed: Int, errors: List<Throwable> = emptyList()): SyncResult {
            return SyncResult(
                success = false,
                itemsSynced = itemsSynced,
                itemsFailed = itemsFailed,
                errors = errors
            )
        }

        fun withConflicts(itemsSynced: Int, conflicts: List<SyncConflict>): SyncResult {
            return SyncResult(
                success = false,
                itemsSynced = itemsSynced,
                conflicts = conflicts
            )
        }
    }
}

/**
 * Represents a sync conflict.
 */
data class SyncConflict(
    val entityType: String,
    val entityId: String,
    val localVersion: String,
    val serverVersion: String,
    val localData: String? = null,
    val serverData: String? = null,
    val resolution: String? = null // "local_wins", "server_wins", "manual"
) {

    /**
     * Check if the conflict has been resolved.
     */
    fun isResolved(): Boolean = resolution != null

    /**
     * Get the resolution display name.
     */
    fun getResolutionDisplayName(): String? {
        return when (resolution) {
            "local_wins" -> "Local Version Kept"
            "server_wins" -> "Server Version Applied"
            "manual" -> "Manual Resolution"
            else -> null
        }
    }
}
