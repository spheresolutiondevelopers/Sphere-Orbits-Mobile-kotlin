/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.core.common

/**
 * Interface for providing the current access token.
 * Implemented in :core:data:auth with TokenManager.
 */
interface TokenProvider {
    /**
     * Get the current access token, or null if not authenticated.
     */
    suspend fun getAccessToken(): String?

    /**
     * Get the current refresh token, or null if not authenticated.
     */
    suspend fun getRefreshToken(): String?

    /**
     * Clear tokens on sign out.
     */
    suspend fun clearTokens()
}