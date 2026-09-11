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
import com.orbits.data.auth.AuthEntity
import com.orbits.data.auth.TokenEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // ─── Auth Entity ─────────────────────────────────────────────

    @Query("SELECT * FROM auth WHERE id = :userId")
    suspend fun getAuth(userId: String): AuthEntity?

    @Query("SELECT * FROM auth WHERE email = :email")
    suspend fun getAuthByEmail(email: String): AuthEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuth(auth: AuthEntity)

    @Update
    suspend fun updateAuth(auth: AuthEntity)

    @Query("UPDATE auth SET is_active = 0 WHERE id = :userId")
    suspend fun deactivateUser(userId: String)

    @Query("DELETE FROM auth WHERE id = :userId")
    suspend fun deleteAuth(userId: String)

    @Query("SELECT * FROM auth WHERE is_active = 1")
    suspend fun getActiveUsers(): List<AuthEntity>

    @Query("SELECT * FROM auth WHERE is_active = 1")
    fun getActiveUsersFlow(): Flow<List<AuthEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM auth WHERE id = :userId AND is_active = 1)")
    suspend fun isUserActive(userId: String): Boolean

    // ─── Token Entity ────────────────────────────────────────────

    @Query("SELECT * FROM tokens WHERE user_id = :userId")
    suspend fun getTokens(userId: String): TokenEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTokens(tokens: TokenEntity)

    @Update
    suspend fun updateTokens(tokens: TokenEntity)

    @Query("DELETE FROM tokens WHERE user_id = :userId")
    suspend fun deleteTokens(userId: String)

    @Query("""
        UPDATE tokens 
        SET access_token = :accessToken, 
            refresh_token = :refreshToken, 
            expires_at = :expiresAt,
            updated_at = datetime('now')
        WHERE user_id = :userId
    """)
    suspend fun updateToken(
        userId: String,
        accessToken: String,
        refreshToken: String?,
        expiresAt: String
    )
}