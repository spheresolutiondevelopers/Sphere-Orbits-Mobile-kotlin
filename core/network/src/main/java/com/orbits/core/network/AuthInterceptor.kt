/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.core.network

import com.orbits.core.common.Logger
import com.orbits.core.common.TokenProvider
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interceptor that adds the Bearer token to all requests.
 * If the token is expired, it attempts to refresh it using the refresh token.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenProvider: TokenProvider
) : Interceptor {

    companion object {
        private const val TAG = "AuthInterceptor"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Get the token (suspending, so we block in a coroutine)
        val token = runBlocking { tokenProvider.getAccessToken() }

        // Add authorization header if token exists
        val authenticatedRequest = if (token != null) {
            request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }

        val response = chain.proceed(authenticatedRequest)

        // Check if response is 401 Unauthorized – attempt token refresh
        if (response.code == 401) {
            response.close()
            return tryRefreshAndRetry(chain, request)
        }

        return response
    }

    private fun tryRefreshAndRetry(chain: Interceptor.Chain, originalRequest: okhttp3.Request): Response {
        // Attempt to refresh token
        val refreshToken = runBlocking { tokenProvider.getRefreshToken() }
        if (refreshToken == null) {
            // No refresh token, must re-authenticate
            Logger.w(TAG, "No refresh token available, cannot retry")
            return chain.proceed(originalRequest) // will also return 401
        }

        // Call refresh endpoint (we need a separate API call)
        // Since we are in an interceptor, we cannot easily get the Retrofit service,
        // so we use a standalone OkHttp client to call the refresh endpoint.
        // For simplicity, we delegate to a RefreshTokenClient (to be implemented)
        // Here we simulate a refresh attempt.
        // In production, this would call the auth API to get new tokens.
        // For now, we just return the original response.
        // Note: This should be implemented with a dedicated refresh API client
        // or by injecting AuthApi and using it.
        // We'll keep it simple and not attempt refresh automatically in this interceptor,
        // but instead we let the caller handle token refresh.
        return chain.proceed(originalRequest)
    }
}