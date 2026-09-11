package com.orbits.data.settings.sync

import com.orbits.data.settings.SettingsEntity
import javax.inject.Inject

internal class SettingsSyncConflictResolver @Inject constructor() {

    fun resolveConflict(
        local: SettingsEntity,
        server: SettingsEntity
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
     * For settings, we use a field-level merge where server wins for system-level
     * preferences, but local wins for user-selected preferences.
     */
    fun mergeFields(
        local: SettingsEntity,
        server: SettingsEntity
    ): SettingsEntity {
        return if (local.updatedAt > server.updatedAt) {
            // Local is newer: keep local preferences
            local.copy(
                // Server might have system-level updates
                // We keep local for user preferences, server for system settings
                syncedAt = server.updatedAt,
                updatedAt = nowUtc(),
                syncStatus = "synced"
            )
        } else {
            // Server is newer: server wins for most fields
            // But preserve local theme, language, and notification settings
            // if they were changed locally and not yet synced
            server.copy(
                // Keep local theme if different from server
                theme = if (local.theme != server.theme) local.theme else server.theme,
                // Keep local language if different from server
                language = if (local.language != server.language) local.language else server.language,
                // Keep local accent color if different from server
                accentColor = if (local.accentColor != server.accentColor) local.accentColor else server.accentColor,
                // Keep local notification preferences if different
                pushNotificationsEnabled = if (local.pushNotificationsEnabled != server.pushNotificationsEnabled) {
                    local.pushNotificationsEnabled
                } else server.pushNotificationsEnabled,
                // Preserve sync status
                syncedAt = nowUtc(),
                updatedAt = nowUtc(),
                syncStatus = "synced"
            )
        }
    }

    /**
     * Smart merge with field-level conflict resolution.
     * Tracks which fields changed on each side.
     */
    fun smartMerge(
        local: SettingsEntity,
        server: SettingsEntity,
        localChangedFields: Set<String> = emptySet(),
        serverChangedFields: Set<String> = emptySet()
    ): SettingsEntity {
        var merged = server.copy(
            syncedAt = nowUtc(),
            updatedAt = nowUtc(),
            syncStatus = "synced"
        )

        // For fields that changed locally, keep local version
        if ("theme" in localChangedFields && local.theme != server.theme) {
            merged = merged.copy(theme = local.theme)
        }
        if ("accentColor" in localChangedFields && local.accentColor != server.accentColor) {
            merged = merged.copy(accentColor = local.accentColor)
        }
        if ("fontSize" in localChangedFields && local.fontSize != server.fontSize) {
            merged = merged.copy(fontSize = local.fontSize)
        }
        if ("compactMode" in localChangedFields && local.compactMode != server.compactMode) {
            merged = merged.copy(compactMode = local.compactMode)
        }
        if ("language" in localChangedFields && local.language != server.language) {
            merged = merged.copy(language = local.language)
        }
        if ("timezone" in localChangedFields && local.timezone != server.timezone) {
            merged = merged.copy(timezone = local.timezone)
        }
        if ("pushNotificationsEnabled" in localChangedFields &&
            local.pushNotificationsEnabled != server.pushNotificationsEnabled) {
            merged = merged.copy(pushNotificationsEnabled = local.pushNotificationsEnabled)
        }
        if ("emailNotificationsEnabled" in localChangedFields &&
            local.emailNotificationsEnabled != server.emailNotificationsEnabled) {
            merged = merged.copy(emailNotificationsEnabled = local.emailNotificationsEnabled)
        }
        if ("inAppNotificationsEnabled" in localChangedFields &&
            local.inAppNotificationsEnabled != server.inAppNotificationsEnabled) {
            merged = merged.copy(inAppNotificationsEnabled = local.inAppNotificationsEnabled)
        }
        if ("reminderMinutesBefore" in localChangedFields &&
            local.reminderMinutesBefore != server.reminderMinutesBefore) {
            merged = merged.copy(reminderMinutesBefore = local.reminderMinutesBefore)
        }
        if ("syncEnabled" in localChangedFields && local.syncEnabled != server.syncEnabled) {
            merged = merged.copy(syncEnabled = local.syncEnabled)
        }
        if ("syncIntervalMinutes" in localChangedFields &&
            local.syncIntervalMinutes != server.syncIntervalMinutes) {
            merged = merged.copy(syncIntervalMinutes = local.syncIntervalMinutes)
        }
        if ("aiSuggestionsEnabled" in localChangedFields &&
            local.aiSuggestionsEnabled != server.aiSuggestionsEnabled) {
            merged = merged.copy(aiSuggestionsEnabled = local.aiSuggestionsEnabled)
        }
        if ("productivityTrackingEnabled" in localChangedFields &&
            local.productivityTrackingEnabled != server.productivityTrackingEnabled) {
            merged = merged.copy(productivityTrackingEnabled = local.productivityTrackingEnabled)
        }

        return merged
    }

    private fun nowUtc(): String {
        return java.time.Instant.now().toString()
    }
}

data class Resolution(
    val strategy: Strategy,
    val entity: SettingsEntity
) {
    enum class Strategy {
        LOCAL_WINS,
        SERVER_WINS
    }
}