package com.orbits.domain.calendar

import com.orbits.core.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Calendar repository interface.
 * Defines all calendar event operations.
 * Implemented by :core:data:calendar.
 */
interface CalendarRepository {

    // ─── Read Operations ──────────────────────────────────────────

    /**
     * Get events in a date range.
     */
    fun getEventsInDateRange(startDate: String, endDate: String): Flow<List<CalendarEvent>>

    /**
     * Get events for a specific date.
     */
    fun getEventsForDate(date: String): Flow<List<CalendarEvent>>

    /**
     * Get events by status.
     */
    fun getEventsByStatus(status: String): Flow<List<CalendarEvent>>

    /**
     * Get a single event by ID.
     */
    suspend fun getEvent(eventId: String): Result<CalendarEvent>

    // ─── Write Operations ─────────────────────────────────────────

    /**
     * Create a new calendar event.
     */
    suspend fun createEvent(event: CalendarEvent): Result<CalendarEvent>

    /**
     * Update an existing calendar event.
     */
    suspend fun updateEvent(event: CalendarEvent): Result<CalendarEvent>

    /**
     * Delete a calendar event.
     */
    suspend fun deleteEvent(eventId: String): Result<Unit>

    // ─── External Sync ────────────────────────────────────────────

    /**
     * Sync with Google Calendar.
     */
    suspend fun syncWithGoogleCalendar(accessToken: String): Result<Unit>

    /**
     * Sync with Outlook Calendar.
     */
    suspend fun syncWithOutlookCalendar(accessToken: String): Result<Unit>
}