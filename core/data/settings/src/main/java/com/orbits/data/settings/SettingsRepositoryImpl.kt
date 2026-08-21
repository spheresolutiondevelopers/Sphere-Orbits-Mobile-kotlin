package com.orbits.data.settings

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.orbits.core.common.Result
import com.orbits.core.common.Logger
import com.orbits.core.common.TokenProvider
import com.orbits.core.common.extensions.nowUtc
import com.orbits.core.database.dao.SettingsDao
import com.orbits.core.database.dao.SyncQueueDao
import com.orbits.core.network.api.SettingsApi
import com.orbits.core.network.error.ApiErrorParser
import com.orbits.data.sync.SyncQueueEntity
import com.orbits.data.settings.local.SettingsEntity
import com.orbits.data.settings.mappers.SettingsMapper
import com.orbits.domain.settings.Settings
import com.orbits.domain.settings.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("sphere_settings")

@Singleton
internal class SettingsRepositoryImpl @Inject constructor(
    private val context: Context,
    private val settingsDao: SettingsDao,
    private val syncQueueDao: SyncQueueDao,
    private val settingsApi: SettingsApi,
    private val settingsMapper: SettingsMapper,
    private val tokenProvider: TokenProvider
) : SettingsRepository {

    companion object {
        private const val TAG = "SettingsRepository"
        private val THEME_KEY = stringPreferencesKey("theme")
        private val LANGUAGE_KEY = stringPreferencesKey("language")
    }

    // ─── Read Operations ──────────────────────────────────────────

    override suspend fun getSettings(): Result<Settings> {
        return try {
            val entity = settingsDao.getSettings(getUserId())
            if (entity != null) {
                Result.Success(settingsMapper.toDomain(entity))
            } else {
                // Create default settings
                val defaultSettings = createDefaultSettings()
                val createResult = createSettings(defaultSettings)
                if (createResult is Result.Success) {
                    Result.Success(createResult.data)
                } else {
                    Result.Error(IllegalStateException("Failed to create default settings"))
                }
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Error getting settings", e)
            Result.Error(e)
        }
    }

    override fun observeSettings(): Flow<Settings> {
        return settingsDao.getSettingsFlow(getUserId())
            .map { entity ->
                if (entity != null) {
                    settingsMapper.toDomain(entity)
                } else {
                    // Return default settings if none exist
                    createDefaultSettings()
                }
            }
    }

    override suspend fun getThemePreference(): Result<String> {
        return try {
            val theme = context.dataStore.data
                .map { preferences -> preferences[THEME_KEY] ?: "system" }
                .first()
            Result.Success(theme)
        } catch (e: Exception) {
            Logger.e(TAG, "Error getting theme preference", e)
            Result.Error(e)
        }
    }

    override fun observeThemePreference(): Flow<String> {
        return context.dataStore.data
            .map { preferences -> preferences[THEME_KEY] ?: "system" }
    }

    override suspend fun getLanguagePreference(): Result<String> {
        return try {
            val language = context.dataStore.data
                .map { preferences -> preferences[LANGUAGE_KEY] ?: "en" }
                .first()
            Result.Success(language)
        } catch (e: Exception) {
            Logger.e(TAG, "Error getting language preference", e)
            Result.Error(e)
        }
    }

    // ─── Write Operations ─────────────────────────────────────────

    override suspend fun createSettings(settings: Settings): Result<Settings> {
        return try {
            validateSettings(settings)

            val entity = settingsMapper.toEntity(settings)
            settingsDao.insertSettings(entity)

            // Save theme and language to DataStore for fast access
            saveThemePreference(settings.theme)
            saveLanguagePreference(settings.language)

            enqueueSync(SyncQueueEntity(
                entityType = "settings",
                operation = "create",
                entityId = settings.id,
                payloadJson = settingsMapper.toUpdateRequest(settings).toString()
            ))

            Logger.d(TAG, "Settings created for user: ${settings.userId}")
            Result.Success(settings)
        } catch (e: Exception) {
            Logger.e(TAG, "Error creating settings", e)
            Result.Error(e)
        }
    }

    override suspend fun updateSettings(settings: Settings): Result<Settings> {
        return try {
            validateSettings(settings)

            val existing = settingsDao.getSettings(settings.userId)
            if (existing == null) {
                return Result.Error(IllegalStateException("Settings not found for user: ${settings.userId}"))
            }

            val entity = settingsMapper.toEntity(settings).copy(updatedAt = nowUtc())
            settingsDao.updateSettings(entity)

            // Save theme and language to DataStore
            saveThemePreference(settings.theme)
            saveLanguagePreference(settings.language)

            enqueueSync(SyncQueueEntity(
                entityType = "settings",
                operation = "update",
                entityId = settings.id,
                payloadJson = settingsMapper.toUpdateRequest(settings).toString()
            ))

            Logger.d(TAG, "Settings updated for user: ${settings.userId}")
            Result.Success(settings)
        } catch (e: Exception) {
            Logger.e(TAG, "Error updating settings", e)
            Result.Error(e)
        }
    }

    override suspend fun updateTheme(theme: String): Result<Unit> {
        return try {
            val userId = getUserId()
            settingsDao.updateTheme(userId, theme)
            saveThemePreference(theme)

            enqueueSync(SyncQueueEntity(
                entityType = "settings",
                operation = "update",
                entityId = userId,
                payloadJson = """{"theme":"$theme"}"""
            ))

            Logger.d(TAG, "Theme updated: $theme")
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error updating theme", e)
            Result.Error(e)
        }
    }

    override suspend fun updateLanguage(language: String): Result<Unit> {
        return try {
            val userId = getUserId()
            settingsDao.updateLanguage(userId, language)
            saveLanguagePreference(language)

            enqueueSync(SyncQueueEntity(
                entityType = "settings",
                operation = "update",
                entityId = userId,
                payloadJson = """{"language":"$language"}"""
            ))

            Logger.d(TAG, "Language updated: $language")
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error updating language", e)
            Result.Error(e)
        }
    }

    override suspend fun updateTimezone(timezone: String): Result<Unit> {
        return try {
            val userId = getUserId()
            settingsDao.updateTimezone(userId, timezone)

            enqueueSync(SyncQueueEntity(
                entityType = "settings",
                operation = "update",
                entityId = userId,
                payloadJson = """{"timezone":"$timezone"}"""
            ))

            Logger.d(TAG, "Timezone updated: $timezone")
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error updating timezone", e)
            Result.Error(e)
        }
    }

    override suspend fun updateNotificationPreferences(
        pushEnabled: Boolean,
        emailEnabled: Boolean,
        inAppEnabled: Boolean
    ): Result<Unit> {
        return try {
            val userId = getUserId()
            val settings = settingsDao.getSettings(userId)
            if (settings == null) {
                return Result.Error(IllegalStateException("Settings not found for user: $userId"))
            }

            val updated = settings.copy(
                pushNotificationsEnabled = pushEnabled,
                emailNotificationsEnabled = emailEnabled,
                inAppNotificationsEnabled = inAppEnabled,
                updatedAt = nowUtc()
            )
            settingsDao.updateSettings(updated)

            enqueueSync(SyncQueueEntity(
                entityType = "settings",
                operation = "update",
                entityId = userId,
                payloadJson = """
                    {
                        "pushNotificationsEnabled":$pushEnabled,
                        "emailNotificationsEnabled":$emailEnabled,
                        "inAppNotificationsEnabled":$inAppEnabled
                    }
                """.trimIndent()
            ))

            Logger.d(TAG, "Notification preferences updated")
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error updating notification preferences", e)
            Result.Error(e)
        }
    }

    override suspend fun updateSyncPreferences(
        syncEnabled: Boolean,
        syncIntervalMinutes: Int,
        syncOnlyOnWifi: Boolean
    ): Result<Unit> {
        return try {
            val userId = getUserId()
            val settings = settingsDao.getSettings(userId)
            if (settings == null) {
                return Result.Error(IllegalStateException("Settings not found for user: $userId"))
            }

            val updated = settings.copy(
                syncEnabled = syncEnabled,
                syncIntervalMinutes = syncIntervalMinutes,
                syncOnlyOnWifi = syncOnlyOnWifi,
                updatedAt = nowUtc()
            )
            settingsDao.updateSettings(updated)

            enqueueSync(SyncQueueEntity(
                entityType = "settings",
                operation = "update",
                entityId = userId,
                payloadJson = """
                    {
                        "syncEnabled":$syncEnabled,
                        "syncIntervalMinutes":$syncIntervalMinutes,
                        "syncOnlyOnWifi":$syncOnlyOnWifi
                    }
                """.trimIndent()
            ))

            Logger.d(TAG, "Sync preferences updated")
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error updating sync preferences", e)
            Result.Error(e)
        }
    }

    override suspend fun updatePrivacyPreferences(
        shareAnalytics: Boolean,
        shareCrashReports: Boolean
    ): Result<Unit> {
        return try {
            val userId = getUserId()
            val settings = settingsDao.getSettings(userId)
            if (settings == null) {
                return Result.Error(IllegalStateException("Settings not found for user: $userId"))
            }

            val updated = settings.copy(
                shareAnalytics = shareAnalytics,
                shareCrashReports = shareCrashReports,
                updatedAt = nowUtc()
            )
            settingsDao.updateSettings(updated)

            enqueueSync(SyncQueueEntity(
                entityType = "settings",
                operation = "update",
                entityId = userId,
                payloadJson = """
                    {
                        "shareAnalytics":$shareAnalytics,
                        "shareCrashReports":$shareCrashReports
                    }
                """.trimIndent()
            ))

            Logger.d(TAG, "Privacy preferences updated")
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error updating privacy preferences", e)
            Result.Error(e)
        }
    }

    override suspend fun resetToDefaults(): Result<Settings> {
        return try {
            val userId = getUserId()
            val defaultSettings = createDefaultSettings().copy(userId = userId)

            val entity = settingsMapper.toEntity(defaultSettings)
            settingsDao.insertSettings(entity)

            // Save theme and language to DataStore
            saveThemePreference(defaultSettings.theme)
            saveLanguagePreference(defaultSettings.language)

            enqueueSync(SyncQueueEntity(
                entityType = "settings",
                operation = "update",
                entityId = userId,
                payloadJson = settingsMapper.toUpdateRequest(defaultSettings).toString()
            ))

            Logger.d(TAG, "Settings reset to defaults for user: $userId")
            Result.Success(defaultSettings)
        } catch (e: Exception) {
            Logger.e(TAG, "Error resetting settings to defaults", e)
            Result.Error(e)
        }
    }

    // ─── Private Helpers ──────────────────────────────────────────

    private fun getUserId(): String {
        // This should come from auth state
        return "test_user_id"
    }

    private suspend fun enqueueSync(item: SyncQueueEntity) {
        syncQueueDao.enqueue(item)
    }

    private suspend fun saveThemePreference(theme: String) {
        try {
            context.dataStore.edit { preferences ->
                preferences[THEME_KEY] = theme
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Error saving theme preference", e)
        }
    }

    private suspend fun saveLanguagePreference(language: String) {
        try {
            context.dataStore.edit { preferences ->
                preferences[LANGUAGE_KEY] = language
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Error saving language preference", e)
        }
    }

    private fun createDefaultSettings(): Settings {
        return Settings(
            id = java.util.UUID.randomUUID().toString(),
            userId = getUserId(),
            theme = "system",
            accentColor = null,
            fontSize = "medium",
            compactMode = false,
            reduceAnimations = false,
            language = "en",
            timezone = null,
            dateFormat = "MM/dd/yyyy",
            timeFormat = "12h",
            weekStartDay = "monday",
            pushNotificationsEnabled = true,
            emailNotificationsEnabled = true,
            inAppNotificationsEnabled = true,
            meetingRemindersEnabled = true,
            taskRemindersEnabled = true,
            eventRemindersEnabled = true,
            reminderMinutesBefore = 15,
            notificationSound = null,
            notificationVibration = true,
            twoFactorEnabled = false,
            biometricEnabled = false,
            incognitoMode = false,
            shareAnalytics = true,
            shareCrashReports = true,
            syncEnabled = true,
            syncIntervalMinutes = 30,
            syncOnlyOnWifi = false,
            googleCalendarSyncEnabled = false,
            outlookCalendarSyncEnabled = false,
            defaultCalendarId = null,
            defaultTaskPriority = "medium",
            defaultTaskCategory = "general",
            defaultReminderMinutes = 15,
            defaultAppointmentDuration = 60,
            aiSuggestionsEnabled = true,
            smartSchedulingEnabled = true,
            autoCategorizationEnabled = true,
            productivityTrackingEnabled = true,
            createdAt = nowUtc(),
            updatedAt = nowUtc()
        )
    }

    private fun validateSettings(settings: Settings) {
        require(settings.theme in listOf("light", "dark", "system")) {
            "Invalid theme. Must be: light, dark, or system"
        }
        require(settings.fontSize in listOf("small", "medium", "large", "x_large")) {
            "Invalid font size"
        }
        require(settings.language.matches(Regex("^[a-z]{2}(-[A-Z]{2})?$"))) {
            "Invalid language format"
        }
        require(settings.dateFormat.isNotBlank()) { "Date format cannot be empty" }
        require(settings.timeFormat in listOf("12h", "24h")) {
            "Invalid time format. Must be: 12h or 24h"
        }
        require(settings.weekStartDay in listOf("monday", "sunday", "saturday")) {
            "Invalid week start day"
        }
        require(settings.reminderMinutesBefore in 0..1440) {
            "Reminder minutes must be between 0 and 1440"
        }
        require(settings.syncIntervalMinutes in 5..1440) {
            "Sync interval must be between 5 and 1440 minutes"
        }
        require(settings.defaultReminderMinutes in 0..1440) {
            "Default reminder minutes must be between 0 and 1440"
        }
        require(settings.defaultAppointmentDuration in 15..480) {
            "Default appointment duration must be between 15 and 480 minutes"
        }
        if (settings.accentColor != null) {
            require(settings.accentColor.matches(Regex("^#[0-9A-Fa-f]{6}$"))) {
                "Invalid accent color format. Must be #RRGGBB"
            }
        }
    }
}