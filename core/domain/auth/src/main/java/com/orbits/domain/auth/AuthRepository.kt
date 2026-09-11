package com.orbits.domain.auth

import com.orbits.core.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Authentication repository interface.
 * Defines all authentication operations.
 * Implemented by :core:data:auth.
 */
interface AuthRepository {

    // ─── Read Operations ──────────────────────────────────────────

    /**
     * Get the currently authenticated user.
     */
    suspend fun getCurrentUser(): AuthUser?

    /**
     * Get a user by ID.
     */
    suspend fun getUser(userId: String): AuthUser?

    /**
     * Observe the current user changes.
     */
    fun observeCurrentUser(): Flow<AuthUser?>

    /**
     * Check if the user is authenticated.
     */
    suspend fun isAuthenticated(): Boolean

    // ─── Authentication Operations ───────────────────────────────

    /**
     * Login with email and password.
     */
    suspend fun login(credentials: LoginCredentials): Result<AuthUser>

    /**
     * Create a local user profile (offline support).
     */
    suspend fun createLocalUser(data: RegistrationData? = null): Result<AuthUser>

    /**
     * Register a new user.
     */
    suspend fun register(data: RegistrationData): Result<AuthUser>

    /**
     * Logout the current user.
     */
    suspend fun logout(): Result<Unit>

    /**
     * Refresh the access token.
     */
    suspend fun refreshAccessToken(): Result<AuthTokens>

    /**
     * Delete the user's account.
     */
    suspend fun deleteAccount(): Result<Unit>
}