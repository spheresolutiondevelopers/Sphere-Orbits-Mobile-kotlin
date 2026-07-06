/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.core.common.extensions

/**
 * String validation and transformation utilities.
 */
object StringExtensions {

    /**
     * Check if string is null or blank.
     */
    fun String?.isNullOrBlank(): Boolean {
        return this == null || this.isBlank()
    }

    /**
     * Check if string is a valid email address.
     */
    fun String.isValidEmail(): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return emailRegex.matches(this)
    }

    /**
     * Check if string is a valid phone number (minimum 10 digits).
     */
    fun String.isValidPhoneNumber(): Boolean {
        val digitsOnly = this.replace(Regex("[^0-9]"), "")
        return digitsOnly.length >= 10
    }

    /**
     * Truncate string to max length with ellipsis.
     */
    fun String.truncate(maxLength: Int = 100): String {
        return if (this.length <= maxLength) {
            this
        } else {
            this.substring(0, maxLength - 3) + "..."
        }
    }

    /**
     * Convert string to title case.
     */
    fun String.toTitleCase(): String {
        return this.split(" ")
            .joinToString(" ") { word ->
                word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            }
    }

    /**
     * Check if string contains only alphanumeric characters.
     */
    fun String.isAlphanumeric(): Boolean {
        return this.matches(Regex("^[a-zA-Z0-9]*$"))
    }

    /**
     * Check if string is a valid UUID.
     */
    fun String.isValidUuid(): Boolean {
        return try {
            java.util.UUID.fromString(this)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    /**
     * Remove all whitespace from string.
     */
    fun String.removeWhitespace(): String {
        return this.replace(" ", "")
    }
}