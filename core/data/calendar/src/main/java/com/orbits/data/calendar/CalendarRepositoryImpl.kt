package com.orbits.data.calendar

import com.orbits.core.common.Result
import com.orbits.core.common.Logger
import com.orbits.core.common.TokenProvider
import com.orbits.core.common.extensions.nowUtc
import com.orbits.core.database.dao.CalendarEventDao
import com.orbits.core.database.dao.SyncQueueDao
import com.orbits.core.network.api.CalendarApi
import com.orbits.core.network.error.ApiErrorParser
import com.orbits.data.calendar.local.CalendarEventEntity
import com.orbits.data.calendar.mappers.CalendarEventMapper
import com.orbits.data.calendar.remote.GoogleCalendarClient
import com.orbits.data.calendar.remote.OutlookCalendarClient
import com.orbits.data.sync.SyncQueueEntity
import com.orbits.domain.calendar.CalendarEvent
import com.orbits.domain.calendar.CalendarRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class CalendarRepositoryImpl @Inject constructor(
    private val calendarEventDao: CalendarEventDao,
    private val syncQueueDao: SyncQueueDao,
    private val calendarApi: CalendarApi,
    private val googleCalendarClient: GoogleCalendarClient,
    private val outlookCalendarClient: OutlookCalendarClient,
    private val calendarEventMapper: CalendarEventMapper,
    private val tokenProvider: TokenProvider
) : CalendarRepository {

    companion object {
        private const val TAG = "CalendarRepository"
    }

    // ─── Read Operations ──────────────────────────────────────────

    override fun getEventsInDateRange(startDate: String, endDate: String): Flow<List<CalendarEvent>> {
        return calendarEventDao.getEventsInDateRange(getUserId(), startDate, endDate)
            .map { entities -> calendarEventMapper.toDomainList(entities) }
    }

    override fun getEventsForDate(date: String): Flow<List<CalendarEvent>> {
        return calendarEventDao.getEventsForDate(getUserId(), date)
            .map { entities -> calendarEventMapper.toDomainList(entities) }
    }

    override fun getEventsByStatus(status: String): Flow<List<CalendarEvent>> {
        return calendarEventDao.getEventsByStatus(getUserId(), status)
            .map { entities -> calendarEventMapper.toDomainList(entities) }
    }

    override suspend fun getEvent(eventId: String): Result<CalendarEvent> {
        return try {
            val entity = calendarEventDao.getEvent(eventId)
            if (entity != null) {
                Result.Success(calendarEventMapper.toDomain(entity))
            } else {
                Result.Error(IllegalStateException("Event not found: $eventId"))
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Error fetching event $eventId", e)
            Result.Error(e)
        }
    }

    // ─── Write Operations ─────────────────────────────────────────

    override suspend fun createEvent(event: CalendarEvent): Result<CalendarEvent> {
        return try {
            validateEvent(event)

            val entity = calendarEventMapper.toEntity(event)
            calendarEventDao.insertEvent(entity)

            enqueueSync(SyncQueueEntity(
                entityType = "calendar_event",
                operation = "create",
                entityId = event.id,
                payloadJson = calendarEventMapper.toCreateRequest(event).toString()
            ))

            // If external sync is needed (Google/Outlook), we'll handle it in sync worker
            Logger.d(TAG, "Calendar event created locally: ${event.id}")
            Result.Success(event)
        } catch (e: Exception) {
            Logger.e(TAG, "Error creating calendar event", e)
            Result.Error(e)
        }
    }

    override suspend fun updateEvent(event: CalendarEvent): Result<CalendarEvent> {
        return try {
            validateEvent(event)

            val entity = calendarEventMapper.toEntity(event).copy(updatedAt = nowUtc())
            calendarEventDao.updateEvent(entity)

            enqueueSync(SyncQueueEntity(
                entityType = "calendar_event",
                operation = "update",
                entityId = event.id,
                payloadJson = calendarEventMapper.toUpdateRequest(event).toString()
            ))

            Logger.d(TAG, "Calendar event updated locally: ${event.id}")
            Result.Success(event)
        } catch (e: Exception) {
            Logger.e(TAG, "Error updating calendar event ${event.id}", e)
            Result.Error(e)
        }
    }

    override suspend fun deleteEvent(eventId: String): Result<Unit> {
        return try {
            calendarEventDao.softDeleteEvent(eventId)

            enqueueSync(SyncQueueEntity(
                entityType = "calendar_event",
                operation = "delete",
                entityId = eventId,
                payloadJson = null
            ))

            Logger.d(TAG, "Calendar event deleted locally: $eventId")
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error deleting calendar event $eventId", e)
            Result.Error(e)
        }
    }

    // ─── External Sync ────────────────────────────────────────────

    override suspend fun syncWithGoogleCalendar(accessToken: String): Result<Unit> {
        return try {
            val result = googleCalendarClient.syncEvents(accessToken)
            result.onSuccess { events ->
                // Update local database with Google events
                events.forEach { event ->
                    val existing = calendarEventDao.getEventByExternalId(getUserId(), event.externalEventId ?: "")
                    if (existing != null) {
                        // Update existing
                        val updated = event.copy(
                            userId = getUserId(),
                            updatedAt = nowUtc()
                        )
                        calendarEventDao.updateEvent(updated)
                    } else {
                        // Insert new
                        val newEvent = event.copy(userId = getUserId())
                        calendarEventDao.insertEvent(newEvent)
                    }
                }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error syncing with Google Calendar", e)
            Result.Error(e)
        }
    }

    override suspend fun syncWithOutlookCalendar(accessToken: String): Result<Unit> {
        return try {
            val result = outlookCalendarClient.syncEvents(accessToken)
            result.onSuccess { events ->
                events.forEach { event ->
                    val existing = calendarEventDao.getEventByExternalId(getUserId(), event.externalEventId ?: "")
                    if (existing != null) {
                        val updated = event.copy(
                            userId = getUserId(),
                            updatedAt = nowUtc()
                        )
                        calendarEventDao.updateEvent(updated)
                    } else {
                        val newEvent = event.copy(userId = getUserId())
                        calendarEventDao.insertEvent(newEvent)
                    }
                }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error syncing with Outlook Calendar", e)
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

    private fun validateEvent(event: CalendarEvent) {
        require(event.title.isNotBlank()) { "Event title cannot be empty" }
        require(event.title.length <= 255) { "Event title must be <= 255 characters" }
        require(event.startDateTime < event.endDateTime) { "End time must be after start time" }
        require(event.reminderMinutesBefore in 0..1440) { "Reminder minutes must be 0-1440" }
        // Additional validation as needed
    }
}