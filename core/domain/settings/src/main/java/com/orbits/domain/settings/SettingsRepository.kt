/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.domain.settings

import com.orbits.core.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing user settings and preferences.
 */
interface SettingsRepository {

    /**
     * Get the current user settings.
     */
    suspend fun getSettings(): Result<Settings>

    /**
     * Observe the user settings as a Flow.
     */
    fun observeSettings(): Flow<Settings>

    /**
     * Get the current theme preference.
     */
    suspend fun getThemePreference(): Result<String>

    /**
     * Observe the theme preference as a Flow.
     */
    fun observeThemePreference(): Flow<String>

    /**
     * Get the current language preference.
     */
    suspend fun getLanguagePreference(): Result<String>

    /**
     * Create initial settings for a user.
     */
    suspend fun createSettings(settings: Settings): Result<Settings>

    /**
     * Update existing user settings.
     */
    suspend fun updateSettings(settings: Settings): Result<Settings>

    /**
     * Update the user's theme preference.
     */
    suspend fun updateTheme(theme: String): Result<Unit>

    /**
     * Update the user's language preference.
     */
    suspend fun updateLanguage(language: String): Result<Unit>

    /**
     * Update the user's timezone preference.
     */
    suspend fun updateTimezone(timezone: String): Result<Unit>

    /**
     * Update notification preferences.
     */
    suspend fun updateNotificationPreferences(
        pushEnabled: Boolean,
        emailEnabled: Boolean,
        inAppEnabled: Boolean
    ): Result<Unit>

    /**
     * Update synchronization preferences.
     */
    suspend fun updateSyncPreferences(
        syncEnabled: Boolean,
        syncIntervalMinutes: Int,
        syncOnlyOnWifi: Boolean
    ): Result<Unit>

    /**
     * Update privacy and analytics preferences.
     */
    suspend fun updatePrivacyPreferences(
        shareAnalytics: Boolean,
        shareCrashReports: Boolean
    ): Result<Unit>

    /**
     * Reset all settings to their default values.
     */
    suspend fun resetToDefaults(): Result<Settings>
}
