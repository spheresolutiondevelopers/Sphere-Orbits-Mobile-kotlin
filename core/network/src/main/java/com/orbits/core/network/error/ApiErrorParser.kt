/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.core.network.error

import com.orbits.core.model.ApiError
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.HttpException
import java.io.IOException

/**
 * Parses API error responses into ApiError objects.
 */
object ApiErrorParser {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val apiErrorAdapter: JsonAdapter<ApiError> = moshi.adapter(ApiError::class.java)

    /**
     * Parse an exception and return an ApiError.
     * Handles network errors, HTTP errors, and parsing failures.
     */
    fun parse(throwable: Throwable): ApiError {
        return when (throwable) {
            is HttpException -> {
                try {
                    val errorBody = throwable.response()?.errorBody()?.string()
                    if (errorBody != null) {
                        apiErrorAdapter.fromJson(errorBody) ?: createFallbackError(
                            throwable.code(),
                            throwable.message()
                        )
                    } else {
                        createFallbackError(throwable.code(), throwable.message())
                    }
                } catch (e: Exception) {
                    createFallbackError(throwable.code(), throwable.message())
                }
            }
            is IOException -> {
                ApiError(
                    code = "NETWORK_ERROR",
                    message = "Network error: ${throwable.message}",
                    timestamp = java.time.Instant.now().toString()
                )
            }
            else -> {
                ApiError(
                    code = "UNKNOWN_ERROR",
                    message = throwable.message ?: "An unknown error occurred",
                    timestamp = java.time.Instant.now().toString()
                )
            }
        }
    }

    private fun createFallbackError(code: Int, message: String?): ApiError {
        val errorCode = when (code) {
            400 -> "BAD_REQUEST"
            401 -> "UNAUTHORIZED"
            403 -> "FORBIDDEN"
            404 -> "NOT_FOUND"
            409 -> "CONFLICT"
            422 -> "VALIDATION_ERROR"
            429 -> "RATE_LIMIT_EXCEEDED"
            500 -> "INTERNAL_ERROR"
            503 -> "SERVICE_UNAVAILABLE"
            else -> "HTTP_${code}"
        }
        return ApiError(
            code = errorCode,
            message = message ?: "HTTP error $code",
            timestamp = java.time.Instant.now().toString()
        )
    }
}