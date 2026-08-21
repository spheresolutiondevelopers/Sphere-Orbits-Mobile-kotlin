package com.orbits.data.notes.sync

import com.orbits.data.notes.local.NoteEntity
import javax.inject.Inject

internal class NoteSyncConflictResolver @Inject constructor() {

    fun resolveConflict(
        local: NoteEntity,
        server: NoteEntity
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
     * Conservative merge: local wins for content, server wins for metadata.
     */
    fun mergeFields(
        local: NoteEntity,
        server: NoteEntity
    ): NoteEntity {
        return if (local.updatedAt > server.updatedAt) {
            // Local is newer: keep local content, but accept server metadata
            local.copy(
                // Server might have updated tags, pin status, or archive status
                tags = server.tags ?: local.tags,
                isPinned = if (server.isPinned && !local.isPinned) {
                    // If server says pinned but local doesn't, server wins (data integrity)
                    server.isPinned
                } else {
                    local.isPinned
                },
                isArchived = if (server.isArchived && !local.isArchived) {
                    // If server says archived but local doesn't, server wins
                    server.isArchived
                } else {
                    local.isArchived
                },
                color = server.color ?: local.color,
                reminderAt = server.reminderAt ?: local.reminderAt,
                syncedAt = server.updatedAt
            )
        } else {
            // Server is newer: server wins
            server.copy(
                syncedAt = nowUtc()
            )
        }
    }

    /**
     * Smart merge that preserves local changes but accepts server updates.
     * Uses a diff-based approach: check which fields changed on each side.
     */
    fun smartMerge(
        local: NoteEntity,
        server: NoteEntity,
        localChangedFields: Set<String> = emptySet(),
        serverChangedFields: Set<String> = emptySet()
    ): NoteEntity {
        // Build merged note: start with server, override with local for fields that changed locally
        var merged = server.copy(
            syncedAt = nowUtc()
        )

        // For fields that changed locally, keep local version
        if ("title" in localChangedFields && local.title != server.title) {
            merged = merged.copy(title = local.title)
        }
        if ("content" in localChangedFields && local.content != server.content) {
            merged = merged.copy(content = local.content)
        }
        if ("contentFormat" in localChangedFields && local.contentFormat != server.contentFormat) {
            merged = merged.copy(contentFormat = local.contentFormat)
        }
        if ("tags" in localChangedFields && local.tags != server.tags) {
            merged = merged.copy(tags = local.tags)
        }
        if ("isPinned" in localChangedFields && local.isPinned != server.isPinned) {
            merged = merged.copy(isPinned = local.isPinned)
        }
        if ("isArchived" in localChangedFields && local.isArchived != server.isArchived) {
            merged = merged.copy(isArchived = local.isArchived)
        }
        if ("color" in localChangedFields && local.color != server.color) {
            merged = merged.copy(color = local.color)
        }
        if ("reminderAt" in localChangedFields && local.reminderAt != server.reminderAt) {
            merged = merged.copy(reminderAt = local.reminderAt)
        }

        return merged
    }

    private fun nowUtc(): String {
        return java.time.Instant.now().toString()
    }
}

data class Resolution(
    val strategy: Strategy,
    val entity: NoteEntity
) {
    enum class Strategy {
        LOCAL_WINS,
        SERVER_WINS
    }
}