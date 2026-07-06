/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.core.model

import kotlinx.serialization.Serializable

/**
 * Standard API error response.
 */
@Serializable
data class ApiError(
    val code: String,
    val message: String,
    val details: Map<String, List<String>>? = null,
    val timestamp: String
)

/**
 * Common error codes.
 */
object ErrorCodes {
    const val BAD_REQUEST = "BAD_REQUEST"
    const val UNAUTHORIZED = "UNAUTHORIZED"
    const val FORBIDDEN = "FORBIDDEN"
    const val NOT_FOUND = "NOT_FOUND"
    const val CONFLICT = "CONFLICT"
    const val VALIDATION_ERROR = "VALIDATION_ERROR"
    const val INTERNAL_ERROR = "INTERNAL_ERROR"
    const val SERVICE_UNAVAILABLE = "SERVICE_UNAVAILABLE"
    const val RATE_LIMIT_EXCEEDED = "RATE_LIMIT_EXCEEDED"
    const val SYNC_CONFLICT = "SYNC_CONFLICT"
}