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
import com.orbits.core.network.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interceptor that logs HTTP requests and responses using Timber.
 * Only logs in debug builds.
 */
@Singleton
class LoggingInterceptor @Inject constructor() : Interceptor {

    companion object {
        private const val TAG = "HTTP"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Log request (only in debug)
        if (BuildConfig.DEBUG) {
            val url = request.url.toString()
            val method = request.method
            val headers = request.headers
            Logger.d(TAG, "--> $method $url")
            headers.forEach { (name, value) ->
                Logger.d(TAG, "$name: $value")
            }
            // Body logging would require reading the body, which can only be done once.
            // For simplicity, we skip body logging.
        }

        val startTime = System.currentTimeMillis()
        return try {
            val response = chain.proceed(request)
            val duration = System.currentTimeMillis() - startTime

            if (BuildConfig.DEBUG) {
                Logger.d(TAG, "<-- ${response.code} ${response.message} (${duration}ms)")
                response.headers.forEach { (name, value) ->
                    Logger.d(TAG, "$name: $value")
                }
                // Optionally log response body with a custom body interceptor (not implemented)
            }
            response
        } catch (e: IOException) {
            Logger.e(TAG, "Network error: ${e.message}", e)
            throw e
        }
    }
}