/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.core.common.utils

import java.util.UUID

/**
 * Utility for generating unique identifiers.
 */
object IdGenerator {

    /**
     * Generate a random UUID v4 string.
     */
    fun generateUuid(): String {
        return UUID.randomUUID().toString()
    }

    /**
     * Generate a short ID (8 characters) for non-critical use.
     */
    fun generateShortId(): String {
        return UUID.randomUUID().toString().substring(0, 8)
    }

    /**
     * Generate a nanosecond-precise ID for ordering.
     * Format: TIMESTAMP_NANO_RANDOM
     */
    fun generateTimestampId(): String {
        val timestamp = System.currentTimeMillis()
        val nano = System.nanoTime()
        val random = (Math.random() * 10000).toInt()
        return "${timestamp}_${nano}_${random}"
    }

    /**
     * Check if a string is a valid UUID.
     */
    fun isValidUuid(id: String): Boolean {
        return try {
            UUID.fromString(id)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}