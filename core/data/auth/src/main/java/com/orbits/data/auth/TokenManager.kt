package com.orbits.data.auth

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.orbits.core.common.TokenProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("auth_tokens")

@Singleton
internal class TokenManager @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: Context
) : TokenProvider {

    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val EXPIRES_AT_KEY = stringPreferencesKey("expires_at")
    }

    // ─── TokenProvider Implementation ─────────────────────────────

    override suspend fun getAccessToken(): String? {
        return context.dataStore.data
            .map { preferences -> preferences[ACCESS_TOKEN_KEY] }
            .firstOrNull()
    }

    override suspend fun getRefreshToken(): String? {
        return context.dataStore.data
            .map { preferences -> preferences[REFRESH_TOKEN_KEY] }
            .firstOrNull()
    }

    override suspend fun clearTokens() {
        context.dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
            preferences.remove(EXPIRES_AT_KEY)
        }
    }

    // ─── Additional Methods ──────────────────────────────────────

    suspend fun saveTokens(accessToken: String, refreshToken: String?, expiresAt: String) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            if (refreshToken != null) {
                preferences[REFRESH_TOKEN_KEY] = refreshToken
            }
            preferences[EXPIRES_AT_KEY] = expiresAt
        }
    }

    fun getAccessTokenFlow(): Flow<String?> {
        return context.dataStore.data
            .map { preferences -> preferences[ACCESS_TOKEN_KEY] }
    }

    fun getRefreshTokenFlow(): Flow<String?> {
        return context.dataStore.data
            .map { preferences -> preferences[REFRESH_TOKEN_KEY] }
    }

    fun getExpiresAtFlow(): Flow<String?> {
        return context.dataStore.data
            .map { preferences -> preferences[EXPIRES_AT_KEY] }
    }

    suspend fun isTokenExpired(): Boolean {
        val expiresAt = context.dataStore.data
            .map { preferences -> preferences[EXPIRES_AT_KEY] }
            .firstOrNull()
        if (expiresAt == null) return true
        return try {
            java.time.Instant.parse(expiresAt).isBefore(java.time.Instant.now())
        } catch (e: Exception) {
            true
        }
    }
}