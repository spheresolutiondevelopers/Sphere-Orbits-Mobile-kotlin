package com.orbits.data.calendar.mappers

import com.orbits.core.common.extensions.nowUtc
import com.orbits.data.calendar.local.CalendarEventEntity
import com.orbits.data.calendar.remote.CalendarEventDto
import com.orbits.data.calendar.remote.CreateCalendarEventRequest
import com.orbits.data.calendar.remote.UpdateCalendarEventRequest
import com.orbits.domain.calendar.CalendarEvent
import javax.inject.Inject

internal class CalendarEventMapper @Inject constructor() {

    // ─── Entity ↔ Domain ──────────────────────────────────────────

    fun toDomain(entity: CalendarEventEntity): CalendarEvent {
        return CalendarEvent(
            id = entity.id,
            userId = entity.userId,
            title = entity.title,
            description = entity.description,
            startDateTime = entity.startDateTime,
            endDateTime = entity.endDateTime,
            allDayEvent = entity.allDayEvent,
            location = entity.location,
            isVirtual = entity.isVirtual,
            meetingLink = entity.meetingLink,
            meetingPlatform = entity.meetingPlatform,
            status = entity.status,
            reminderMinutesBefore = entity.reminderMinutesBefore,
            isRecurring = entity.isRecurring,
            recurrencePattern = entity.recurrencePattern,
            calendarColor = entity.calendarColor,
            notes = entity.notes,
            externalEventId = entity.externalEventId,
            externalSyncStatus = entity.externalSyncStatus,
            isDeleted = entity.isDeleted,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toEntity(domain: CalendarEvent): CalendarEventEntity {
        return CalendarEventEntity(
            id = domain.id,
            userId = domain.userId,
            title = domain.title,
            description = domain.description,
            startDateTime = domain.startDateTime,
            endDateTime = domain.endDateTime,
            allDayEvent = domain.allDayEvent,
            location = domain.location,
            isVirtual = domain.isVirtual,
            meetingLink = domain.meetingLink,
            meetingPlatform = domain.meetingPlatform,
            status = domain.status,
            reminderMinutesBefore = domain.reminderMinutesBefore,
            isRecurring = domain.isRecurring,
            recurrencePattern = domain.recurrencePattern,
            calendarColor = domain.calendarColor,
            notes = domain.notes,
            externalEventId = domain.externalEventId,
            externalSyncStatus = domain.externalSyncStatus,
            isDeleted = domain.isDeleted,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            syncedAt = null,
            syncStatus = "synced"
        )
    }

    // ─── DTO ↔ Entity ─────────────────────────────────────────────

    fun toEntity(dto: CalendarEventDto): CalendarEventEntity {
        return CalendarEventEntity(
            id = dto.id,
            userId = dto.userId,
            title = dto.title,
            description = dto.description,
            startDateTime = dto.startDateTime,
            endDateTime = dto.endDateTime,
            allDayEvent = dto.allDayEvent,
            location = dto.location,
            isVirtual = dto.isVirtual,
            meetingLink = dto.meetingLink,
            meetingPlatform = dto.meetingPlatform,
            status = dto.status,
            reminderMinutesBefore = dto.reminderMinutesBefore,
            isRecurring = dto.isRecurring,
            recurrencePattern = dto.recurrencePattern,
            calendarColor = dto.calendarColor,
            notes = dto.notes,
            externalEventId = dto.externalEventId,
            externalSyncStatus = dto.externalSyncStatus,
            isDeleted = dto.isDeleted,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,
            syncedAt = null,
            syncStatus = "synced"
        )
    }

    fun toDto(entity: CalendarEventEntity): CalendarEventDto {
        return CalendarEventDto(
            id = entity.id,
            userId = entity.userId,
            title = entity.title,
            description = entity.description,
            startDateTime = entity.startDateTime,
            endDateTime = entity.endDateTime,
            allDayEvent = entity.allDayEvent,
            location = entity.location,
            isVirtual = entity.isVirtual,
            meetingLink = entity.meetingLink,
            meetingPlatform = entity.meetingPlatform,
            status = entity.status,
            reminderMinutesBefore = entity.reminderMinutesBefore,
            isRecurring = entity.isRecurring,
            recurrencePattern = entity.recurrencePattern,
            calendarColor = entity.calendarColor,
            notes = entity.notes,
            externalEventId = entity.externalEventId,
            externalSyncStatus = entity.externalSyncStatus,
            isDeleted = entity.isDeleted,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    // ─── Create/Update Requests ────────────────────────────────────

    fun toCreateRequest(domain: CalendarEvent): CreateCalendarEventRequest {
        return CreateCalendarEventRequest(
            title = domain.title,
            description = domain.description,
            startDateTime = domain.startDateTime,
            endDateTime = domain.endDateTime,
            allDayEvent = domain.allDayEvent,
            location = domain.location,
            isVirtual = domain.isVirtual,
            meetingLink = domain.meetingLink,
            meetingPlatform = domain.meetingPlatform,
            reminderMinutesBefore = domain.reminderMinutesBefore,
            isRecurring = domain.isRecurring,
            recurrencePattern = domain.recurrencePattern,
            calendarColor = domain.calendarColor,
            notes = domain.notes,
            participants = emptyList()
        )
    }

    fun toUpdateRequest(domain: CalendarEvent): UpdateCalendarEventRequest {
        return UpdateCalendarEventRequest(
            title = domain.title,
            description = domain.description,
            startDateTime = domain.startDateTime,
            endDateTime = domain.endDateTime,
            allDayEvent = domain.allDayEvent,
            location = domain.location,
            isVirtual = domain.isVirtual,
            meetingLink = domain.meetingLink,
            meetingPlatform = domain.meetingPlatform,
            reminderMinutesBefore = domain.reminderMinutesBefore,
            isRecurring = domain.isRecurring,
            recurrencePattern = domain.recurrencePattern,
            calendarColor = domain.calendarColor,
            notes = domain.notes,
            status = domain.status
        )
    }

    // ─── List Mappings ─────────────────────────────────────────────

    fun toDomainList(entities: List<CalendarEventEntity>): List<CalendarEvent> {
        return entities.map { toDomain(it) }
    }

    fun toEntityList(domains: List<CalendarEvent>): List<CalendarEventEntity> {
        return domains.map { toEntity(it) }
    }

    fun toDomainListFromDto(dtos: List<CalendarEventDto>): List<CalendarEvent> {
        return dtos.map { toDomain(toEntity(it)) }
    }

    fun toEntityListFromDto(dtos: List<CalendarEventDto>): List<CalendarEventEntity> {
        return dtos.map { toEntity(it) }
    }
}g