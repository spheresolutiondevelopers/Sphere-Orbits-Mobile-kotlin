package com.orbits.core.sync

/**
 * Represents the status of a sync operation.
 */
sealed class SyncStatus {
    object Idle : SyncStatus()
    object InProgress : SyncStatus()
    data class Success(val timestamp: String, val itemsSynced: Int) : SyncStatus()
    data class Error(val exception: Throwable, val retryCount: Int) : SyncStatus()
    data class Conflict(val entityType: String, val entityId: String, val message: String) : SyncStatus()
    data class PartialSuccess(val itemsSynced: Int, val errors: List<Throwable>) : SyncStatus()
}

/**
 * Extension functions for SyncStatus.
 */
fun SyncStatus.isIdle(): Boolean = this is SyncStatus.Idle
fun SyncStatus.isInProgress(): Boolean = this is SyncStatus.InProgress
fun SyncStatus.isSuccess(): Boolean = this is SyncStatus.Success
fun SyncStatus.isError(): Boolean = this is SyncStatus.Error
fun SyncStatus.isConflict(): Boolean = this is SyncStatus.Conflict
fun SyncStatus.isPartialSuccess(): Boolean = this is SyncStatus.PartialSuccess
fun SyncStatus.isTerminal(): Boolean = this is SyncStatus.Success || this is SyncStatus.Error

fun SyncStatus.getErrorMessage(): String? {
    return when (this) {
        is SyncStatus.Error -> this.exception.message ?: "Unknown error"
        is SyncStatus.Conflict -> this.message
        else -> null
    }
}

fun SyncStatus.getItemsSynced(): Int {
    return when (this) {
        is SyncStatus.Success -> this.itemsSynced
        is SyncStatus.PartialSuccess -> this.itemsSynced
        else -> 0
    }
}
