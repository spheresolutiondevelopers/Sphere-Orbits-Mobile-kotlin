package com.orbits.data.events

import com.orbits.core.common.Result
import com.orbits.core.common.Logger
import com.orbits.core.common.TokenProvider
import com.orbits.core.common.extensions.nowUtc
import com.orbits.core.database.dao.EventDao
import com.orbits.core.database.dao.SyncQueueDao
import com.orbits.core.network.api.EventApi
import com.orbits.core.network.error.ApiErrorParser
import com.orbits.data.sync.SyncQueueEntity
import com.orbits.data.events.local.EventEntity
import com.orbits.data.events.local.EventParticipantEntity
import com.orbits.data.events.mappers.EventMapper
import com.orbits.data.events.mappers.EventParticipantMapper
import com.orbits.domain.events.Event
import com.orbits.domain.events.EventParticipant
import com.orbits.domain.events.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class EventRepositoryImpl @Inject constructor(
    private val eventDao: EventDao,
    private val syncQueueDao: SyncQueueDao,
    private val eventApi: EventApi,
    private val eventMapper: EventMapper,
    private val participantMapper: EventParticipantMapper,
    private val tokenProvider: TokenProvider
) : EventRepository {

    companion object {
        private const val TAG = "EventRepository"
    }

    // ─── Read Operations ──────────────────────────────────────────

    override fun getEvents(): Flow<List<Event>> {
        return eventDao.getEventsForUser(getUserId())
            .map { entities -> eventMapper.toDomainList(entities) }
    }

    override fun getEventsByStatus(status: String): Flow<List<Event>> {
        return eventDao.getEventsByStatus(getUserId(), status)
            .map { entities -> eventMapper.toDomainList(entities) }
    }

    override fun getEventsInDateRange(startDate: String, endDate: String): Flow<List<Event>> {
        return eventDao.getEventsInDateRange(getUserId(), startDate, endDate)
            .map { entities -> eventMapper.toDomainList(entities) }
    }

    override fun getEventsByCategory(categoryId: String): Flow<List<Event>> {
        return eventDao.getEventsByCategory(getUserId(), categoryId)
            .map { entities -> eventMapper.toDomainList(entities) }
    }

    override suspend fun getEvent(eventId: String): Result<Event> {
        return try {
            val entity = eventDao.getEvent(eventId)
            if (entity != null) {
                Result.Success(eventMapper.toDomain(entity))
            } else {
                Result.Error(IllegalStateException("Event not found: $eventId"))
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Error fetching event $eventId", e)
            Result.Error(e)
        }
    }

    override suspend fun getUpcomingEvents(): Flow<List<Event>> {
        return eventDao.getUpcomingEvents(getUserId())
            .map { entities -> eventMapper.toDomainList(entities) }
    }

    // ─── Participants ─────────────────────────────────────────────

    override suspend fun getParticipants(eventId: String): Result<List<EventParticipant>> {
        return try {
            val entities = eventDao.getParticipantsForEvent(eventId)
            Result.Success(participantMapper.toDomainList(entities))
        } catch (e: Exception) {
            Logger.e(TAG, "Error fetching participants for event $eventId", e)
            Result.Error(e)
        }
    }

    override suspend fun getConfirmedParticipants(eventId: String): Result<List<EventParticipant>> {
        return try {
            val entities = eventDao.getConfirmedParticipantsForEvent(eventId)
            Result.Success(participantMapper.toDomainList(entities))
        } catch (e: Exception) {
            Logger.e(TAG, "Error fetching confirmed participants for event $eventId", e)
            Result.Error(e)
        }
    }

    override suspend fun addParticipant(eventId: String, participant: EventParticipant): Result<EventParticipant> {
        return try {
            val entity = participantMapper.toEntity(participant)
            eventDao.insertParticipant(entity)

            enqueueSync(SyncQueueEntity(
                entityType = "event_participant",
                operation = "create",
                entityId = participant.id,
                payloadJson = """{"eventId":"$eventId","email":"${participant.email}"}"""
            ))

            Logger.d(TAG, "Participant added to event $eventId")
            Result.Success(participant)
        } catch (e: Exception) {
            Logger.e(TAG, "Error adding participant to event $eventId", e)
            Result.Error(e)
        }
    }

    override suspend fun updateParticipantStatus(
        eventId: String,
        participantId: String,
        status: String
    ): Result<Unit> {
        return try {
            eventDao.updateParticipantStatus(eventId, participantId, status)

            enqueueSync(SyncQueueEntity(
                entityType = "event_participant",
                operation = "update",
                entityId = participantId,
                payloadJson = """{"invitationStatus":"$status"}"""
            ))

            Logger.d(TAG, "Participant $participantId status updated to $status")
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error updating participant status $participantId", e)
            Result.Error(e)
        }
    }

    override suspend fun removeParticipant(eventId: String, participantId: String): Result<Unit> {
        return try {
            // Soft delete participant
            enqueueSync(SyncQueueEntity(
                entityType = "event_participant",
                operation = "delete",
                entityId = participantId,
                payloadJson = null
            ))
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error removing participant $participantId", e)
            Result.Error(e)
        }
    }

    // ─── Write Operations ─────────────────────────────────────────

    override suspend fun createEvent(event: Event): Result<Event> {
        return try {
            validateEvent(event)

            val entity = eventMapper.toEntity(event)
            eventDao.insertEvent(entity)

            enqueueSync(SyncQueueEntity(
                entityType = "event",
                operation = "create",
                entityId = event.id,
                payloadJson = eventMapper.toCreateRequest(event).toString()
            ))

            Logger.d(TAG, "Event created locally: ${event.id}")
            Result.Success(event)
        } catch (e: Exception) {
            Logger.e(TAG, "Error creating event", e)
            Result.Error(e)
        }
    }

    override suspend fun updateEvent(event: Event): Result<Event> {
        return try {
            validateEvent(event)

            val entity = eventMapper.toEntity(event).copy(updatedAt = nowUtc())
            eventDao.updateEvent(entity)

            enqueueSync(SyncQueueEntity(
                entityType = "event",
                operation = "update",
                entityId = event.id,
                payloadJson = eventMapper.toUpdateRequest(event).toString()
            ))

            Logger.d(TAG, "Event updated locally: ${event.id}")
            Result.Success(event)
        } catch (e: Exception) {
            Logger.e(TAG, "Error updating event ${event.id}", e)
            Result.Error(e)
        }
    }

    override suspend fun deleteEvent(eventId: String): Result<Unit> {
        return try {
            eventDao.softDeleteEvent(eventId)

            enqueueSync(SyncQueueEntity(
                entityType = "event",
                operation = "delete",
                entityId = eventId,
                payloadJson = null
            ))

            Logger.d(TAG, "Event deleted locally: $eventId")
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error deleting event $eventId", e)
            Result.Error(e)
        }
    }

    override suspend fun restoreEvent(eventId: String): Result<Event> {
        return try {
            eventDao.restoreEvent(eventId)

            val entity = eventDao.getEvent(eventId)
            if (entity != null) {
                enqueueSync(SyncQueueEntity(
                    entityType = "event",
                    operation = "update",
                    entityId = eventId,
                    payloadJson = eventMapper.toUpdateRequest(eventMapper.toDomain(entity)).toString()
                ))
                Result.Success(eventMapper.toDomain(entity))
            } else {
                Result.Error(IllegalStateException("Event not found"))
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Error restoring event $eventId", e)
            Result.Error(e)
        }
    }

    // ─── Sync Operations ───────────────────────────────────────────

    override suspend fun syncWithServer(): Result<Unit> {
        return try {
            val token = tokenProvider.getAccessToken()
            if (token == null) {
                return Result.Error(IllegalStateException("Not authenticated"))
            }

            val unsynced = eventDao.getUnsyncedEvents()
            if (unsynced.isNotEmpty()) {
                // Push unsynced events
                unsynced.forEach { entity ->
                    when (entity.syncStatus) {
                        "pending" -> {
                            // Push to server
                            val dto = eventMapper.toDto(entity)
                            // Would call eventApi.createEvent(dto) or updateEvent
                        }
                        else -> { /* handled */ }
                    }
                }
            }

            // Pull latest from server
            val response = eventApi.getEvents()
            response.data?.let { dtos ->
                dtos.forEach { dto ->
                    val entity = eventMapper.toEntity(dto)
                    eventDao.insertEvent(entity)
                }
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Sync error", e)
            Result.Error(e)
        }
    }

    // ─── Private Helpers ──────────────────────────────────────────

    private fun getUserId(): String {
        // This should come from auth state
        return "test_user_id"
    }

    private suspend fun enqueueSync(item: SyncQueueEntity) {
        syncQueueDao.enqueue(item)
    }

    private fun validateEvent(event: Event) {
        require(event.name.isNotBlank()) { "Event name cannot be empty" }
        require(event.name.length <= 255) { "Event name must be <= 255 characters" }
        require(event.status in listOf("planned", "ongoing", "completed", "cancelled")) {
            "Invalid event status"
        }
        if (event.startDateTime != null && event.endDateTime != null) {
            require(event.endDateTime > event.startDateTime) {
                "End time must be after start time"
            }
        }
        if (event.budget != null && event.budget < 0) {
            require(event.budget >= 0) { "Budget cannot be negative" }
        }
        if (event.maxAttendees != null && event.maxAttendees < 0) {
            require(event.maxAttendees >= 0) { "Max attendees cannot be negative" }
        }
        // Additional validation as needed
    }
}