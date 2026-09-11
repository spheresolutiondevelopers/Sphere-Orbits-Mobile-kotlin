package com.orbits.domain.events

import com.orbits.core.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Event repository interface.
 * Defines all event operations.
 * Implemented by :core:data:events.
 */
interface EventRepository {

    // ─── Read Operations ──────────────────────────────────────────

    /**
     * Get all events for the current user.
     */
    fun getEvents(): Flow<List<Event>>

    /**
     * Get events by status.
     */
    fun getEventsByStatus(status: String): Flow<List<Event>>

    /**
     * Get events in a date range.
     */
    fun getEventsInDateRange(startDate: String, endDate: String): Flow<List<Event>>

    /**
     * Get events by category.
     */
    fun getEventsByCategory(categoryId: String): Flow<List<Event>>

    /**
     * Get a single event by ID.
     */
    suspend fun getEvent(eventId: String): Result<Event>

    /**
     * Get upcoming events.
     */
    fun getUpcomingEvents(): Flow<List<Event>>

    // ─── Participants ─────────────────────────────────────────────

    /**
     * Get participants for an event.
     */
    suspend fun getParticipants(eventId: String): Result<List<EventParticipant>>

    /**
     * Get confirmed participants for an event.
     */
    suspend fun getConfirmedParticipants(eventId: String): Result<List<EventParticipant>>

    /**
     * Add a participant to an event.
     */
    suspend fun addParticipant(eventId: String, participant: EventParticipant): Result<EventParticipant>

    /**
     * Update a participant's status.
     */
    suspend fun updateParticipantStatus(eventId: String, participantId: String, status: String): Result<Unit>

    /**
     * Remove a participant from an event.
     */
    suspend fun removeParticipant(eventId: String, participantId: String): Result<Unit>

    // ─── Write Operations ─────────────────────────────────────────

    /**
     * Create a new event.
     */
    suspend fun createEvent(event: Event): Result<Event>

    /**
     * Update an existing event.
     */
    suspend fun updateEvent(event: Event): Result<Event>

    /**
     * Delete an event (soft delete).
     */
    suspend fun deleteEvent(eventId: String): Result<Unit>

    /**
     * Restore a deleted event.
     */
    suspend fun restoreEvent(eventId: String): Result<Event>

    // ─── Sync Operations ──────────────────────────────────────────

    /**
     * Synchronize events with the server.
     */
    suspend fun syncWithServer(): Result<Unit>
}
