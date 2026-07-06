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

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * ISO 8601 date/time formatter for UTC.
 * Example: "2026-06-30T14:00:00Z"
 */
private val ISO_8601_UTC = DateTimeFormatter.ISO_INSTANT

/**
 * ISO 8601 date formatter without time.
 * Example: "2026-06-30"
 */
private val ISO_8601_DATE = DateTimeFormatter.ISO_LOCAL_DATE

/**
 * Human-readable date formatter.
 * Example: "Jun 30, 2026"
 */
private val HUMAN_READABLE_DATE = DateTimeFormatter.ofPattern("MMM dd, yyyy")

/**
 * Human-readable time formatter (12-hour).
 * Example: "2:00 PM"
 */
private val HUMAN_READABLE_TIME = DateTimeFormatter.ofPattern("h:mm a")

/**
 * Human-readable date and time formatter.
 * Example: "Jun 30, 2026 at 2:00 PM"
 */
private val HUMAN_READABLE_DATETIME = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' h:mm a")

/**
 * Converts ISO 8601 string to Instant.
 */
fun String.toInstant(): Instant? {
    return try {
        Instant.parse(this)
    } catch (e: DateTimeParseException) {
        null
    }
}

/**
 * Converts ISO 8601 string to LocalDateTime (system default timezone).
 */
fun String.toLocalDateTime(): LocalDateTime? {
    return try {
        Instant.parse(this).atZone(ZoneId.systemDefault()).toLocalDateTime()
    } catch (e: DateTimeParseException) {
        null
    }
}

/**
 * Converts ISO 8601 string to LocalDate (system default timezone).
 */
fun String.toLocalDate(): LocalDate? {
    return try {
        LocalDate.parse(this, ISO_8601_DATE)
    } catch (e: DateTimeParseException) {
        null
    }
}

/**
 * Formats Instant to ISO 8601 UTC string.
 */
fun Instant.toIsoString(): String {
    return ISO_8601_UTC.format(this)
}

/**
 * Formats Instant to human-readable string.
 */
fun Instant.toHumanReadable(): String {
    return HUMAN_READABLE_DATETIME.format(this.atZone(ZoneId.systemDefault()))
}

/**
 * Formats Instant to human-readable date.
 */
fun Instant.toHumanReadableDate(): String {
    return HUMAN_READABLE_DATE.format(this.atZone(ZoneId.systemDefault()))
}

/**
 * Formats Instant to human-readable time.
 */
fun Instant.toHumanReadableTime(): String {
    return HUMAN_READABLE_TIME.format(this.atZone(ZoneId.systemDefault()))
}

/**
 * Get current UTC time as ISO 8601 string.
 */
fun nowUtc(): String {
    return Instant.now().toIsoString()
}

/**
 * Get current UTC Instant.
 */
fun nowInstant(): Instant {
    return Instant.now()
}

/**
 * Check if ISO 8601 string is valid.
 */
fun String.isValidIsoDateTime(): Boolean {
    return try {
        Instant.parse(this)
        true
    } catch (e: DateTimeParseException) {
        false
    }
}