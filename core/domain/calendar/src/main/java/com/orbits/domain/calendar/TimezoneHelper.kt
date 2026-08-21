package com.orbits.domain.calendar

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Helper for timezone operations.
 */
object TimezoneHelper {

    private val ISO_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    /**
     * Convert a date/time string from one timezone to another.
     */
    fun convertTimezone(
        dateTime: String,
        fromTimezone: ZoneId,
        toTimezone: ZoneId
    ): String {
        return try {
            val zoned = ZonedDateTime.parse(dateTime, ISO_FORMATTER)
            val converted = zoned.withZoneSameInstant(toTimezone)
            converted.format(ISO_FORMATTER)
        } catch (e: Exception) {
            dateTime
        }
    }

    /**
     * Get the user's local timezone from the date/time string.
     */
    fun getTimezoneFromDateTime(dateTime: String): ZoneId? {
        return try {
            val zoned = ZonedDateTime.parse(dateTime, ISO_FORMATTER)
            zoned.zone
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Format a date/time string to a readable format in the user's timezone.
     */
    fun formatReadable(
        dateTime: String,
        timezone: ZoneId = ZoneId.systemDefault()
    ): String {
        return try {
            val instant = Instant.parse(dateTime)
            val zoned = instant.atZone(timezone)
            DateTimeFormatter.ofPattern("MMM dd, yyyy h:mm a")
                .format(zoned)
        } catch (e: Exception) {
            dateTime
        }
    }

    /**
     * Format a date/time string to a time-only format.
     */
    fun formatTimeOnly(
        dateTime: String,
        timezone: ZoneId = ZoneId.systemDefault()
    ): String {
        return try {
            val instant = Instant.parse(dateTime)
            val zoned = instant.atZone(timezone)
            DateTimeFormatter.ofPattern("h:mm a")
                .format(zoned)
        } catch (e: Exception) {
            dateTime
        }
    }

    /**
     * Format a date/time string to a date-only format.
     */
    fun formatDateOnly(
        dateTime: String,
        timezone: ZoneId = ZoneId.systemDefault()
    ): String {
        return try {
            val instant = Instant.parse(dateTime)
            val zoned = instant.atZone(timezone)
            DateTimeFormatter.ofPattern("MMM dd, yyyy")
                .format(zoned)
        } catch (e: Exception) {
            dateTime
        }
    }

    /**
     * Check if two date/time strings are on the same day in the given timezone.
     */
    fun isSameDay(
        dateTime1: String,
        dateTime2: String,
        timezone: ZoneId = ZoneId.systemDefault()
    ): Boolean {
        return try {
            val instant1 = Instant.parse(dateTime1)
            val instant2 = Instant.parse(dateTime2)
            val zoned1 = instant1.atZone(timezone)
            val zoned2 = instant2.atZone(timezone)
            zoned1.toLocalDate() == zoned2.toLocalDate()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get the current time in ISO format with offset.
     */
    fun nowWithOffset(timezone: ZoneId = ZoneId.systemDefault()): String {
        return ZonedDateTime.now(timezone).format(ISO_FORMATTER)
    }

    /**
     * Get the start of a day in ISO format.
     */
    fun getDayStart(
        date: String,
        timezone: ZoneId = ZoneId.systemDefault()
    ): String {
        return try {
            val localDate = java.time.LocalDate.parse(date)
            val zoned = localDate.atStartOfDay(timezone)
            zoned.format(ISO_FORMATTER)
        } catch (e: Exception) {
            date
        }
    }

    /**
     * Get the end of a day in ISO format.
     */
    fun getDayEnd(
        date: String,
        timezone: ZoneId = ZoneId.systemDefault()
    ): String {
        return try {
            val localDate = java.time.LocalDate.parse(date)
            val zoned = localDate.atTime(java.time.LocalTime.MAX).atZone(timezone)
            zoned.format(ISO_FORMATTER)
        } catch (e: Exception) {
            date
        }
    }
}