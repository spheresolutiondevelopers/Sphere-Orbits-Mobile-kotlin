/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.orbits.data.settings.SettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {

    @Query("SELECT * FROM settings WHERE user_id = :userId")
    suspend fun getSettings(userId: String): SettingsEntity?

    @Query("SELECT * FROM settings WHERE user_id = :userId")
    fun getSettingsFlow(userId: String): Flow<SettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: SettingsEntity)

    @Update
    suspend fun updateSettings(settings: SettingsEntity)

    @Query("""
        UPDATE settings 
        SET theme = :theme, updated_at = datetime('now') 
        WHERE user_id = :userId
    """)
    suspend fun updateTheme(userId: String, theme: String)

    @Query("""
        UPDATE settings 
        SET language = :language, updated_at = datetime('now') 
        WHERE user_id = :userId
    """)
    suspend fun updateLanguage(userId: String, language: String)

    @Query("""
        UPDATE settings 
        SET timezone = :timezone, updated_at = datetime('now') 
        WHERE user_id = :userId
    """)
    suspend fun updateTimezone(userId: String, timezone: String)

    @Query("""
        UPDATE settings 
        SET notification_preferences = :preferences, updated_at = datetime('now') 
        WHERE user_id = :userId
    """)
    suspend fun updateNotificationPreferences(userId: String, preferences: String)

    @Query("DELETE FROM settings WHERE user_id = :userId")
    suspend fun deleteSettings(userId: String)
}