/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.core.theme

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.enumPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DataStore key for theme preference.
 */
private val THEME_MODE_KEY = enumPreferencesKey<ThemeMode>("theme_mode")

/**
 * Extension property for DataStore.
 */
private val Context.dataStore by preferencesDataStore("sphere_theme")

/**
 * Theme preference manager.
 * Handles persistence of user's theme preference.
 */
@Singleton
class ThemePreference @Inject constructor(
    private val context: Context
) {

    /**
     * Flow of the current theme mode.
     * Emits the saved preference, defaulting to SYSTEM.
     */
    val themeModeFlow: Flow<ThemeMode> = context.dataStore.data
        .map { preferences ->
            preferences[THEME_MODE_KEY] ?: ThemeMode.SYSTEM
        }

    /**
     * Save theme mode preference.
     */
    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode
        }
    }

    /**
     * Get the current theme mode synchronously (blocking).
     * Should only be used in non-UI contexts (e.g., WorkManager).
     */
    suspend fun getThemeModeSync(): ThemeMode {
        return context.dataStore.data
            .map { preferences ->
                preferences[THEME_MODE_KEY] ?: ThemeMode.SYSTEM
            }
            .collect { return it }
    }
}