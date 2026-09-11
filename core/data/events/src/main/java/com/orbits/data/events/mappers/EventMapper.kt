package com.orbits.data.events.mappers

import com.orbits.core.common.extensions.nowUtc
import com.orbits.data.events.EventEntity
import com.orbits.data.events.EventParticipantEntity
import com.orbits.core.network.api.EventDto
import com.orbits.core.network.api.EventParticipantDto
import com.orbits.core.network.api.CreateEventRequest
import com.orbits.core.network.api.UpdateEventRequest
import com.orbits.domain.events.Event
import com.orbits.domain.events.EventParticipant
import javax.inject.Inject

internal class EventMapper @Inject constructor() {

    // ─── Entity ↔ Domain ──────────────────────────────────────────

    fun toDomain(entity: EventEntity): Event {
        return Event(
            id = entity.id,
            userId = entity.userId,
            categoryId = entity.categoryId,
            taskId = entity.taskId,
            name = entity.name,
            description = entity.description,
            format = entity.format,
            planningNotes = entity.planningNotes,
            startDateTime = entity.startDateTime,
            endDateTime = entity.endDateTime,
            location = entity.location,
            status = entity.status,
            isRecurring = entity.isRecurring,
            recurrencePattern = entity.recurrencePattern,
            budget = entity.budget,
            currency = entity.currency,
            maxAttendees = entity.maxAttendees,
            isPublic = entity.isPublic,
            imageUrl = entity.imageUrl,
            coverPhotoUrl = entity.coverPhotoUrl,
            isDeleted = entity.isDeleted,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toEntity(domain: Event): EventEntity {
        return EventEntity(
            id = domain.id,
            userId = domain.userId,
            categoryId = domain.categoryId,
            taskId = domain.taskId,
            name = domain.name,
            description = domain.description,
            format = domain.format,
            planningNotes = domain.planningNotes,
            startDateTime = domain.startDateTime,
            endDateTime = domain.endDateTime,
            location = domain.location,
            status = domain.status,
            isRecurring = domain.isRecurring,
            recurrencePattern = domain.recurrencePattern,
            budget = domain.budget,
            currency = domain.currency,
            maxAttendees = domain.maxAttendees,
            isPublic = domain.isPublic,
            imageUrl = domain.imageUrl,
            coverPhotoUrl = domain.coverPhotoUrl,
            isDeleted = domain.isDeleted,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            syncedAt = null,
            syncStatus = "synced"
        )
    }

    // ─── DTO ↔ Entity ─────────────────────────────────────────────

    fun toEntity(dto: EventDto): EventEntity {
        return EventEntity(
            id = dto.id,
            userId = dto.userId,
            categoryId = dto.categoryId,
            taskId = dto.taskId,
            name = dto.name,
            description = dto.description,
            format = dto.format,
            planningNotes = dto.planningNotes,
            startDateTime = dto.startDateTime,
            endDateTime = dto.endDateTime,
            location = dto.location,
            status = dto.status,
            isRecurring = dto.isRecurring,
            recurrencePattern = dto.recurrencePattern,
            budget = dto.budget,
            currency = dto.currency,
            maxAttendees = dto.maxAttendees,
            isPublic = dto.isPublic,
            imageUrl = dto.imageUrl,
            coverPhotoUrl = dto.coverPhotoUrl,
            isDeleted = dto.isDeleted,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,
            syncedAt = null,
            syncStatus = "synced"
        )
    }

    fun toDto(entity: EventEntity): EventDto {
        return EventDto(
            id = entity.id,
            userId = entity.userId,
            categoryId = entity.categoryId,
            taskId = entity.taskId,
            name = entity.name,
            description = entity.description,
            format = entity.format,
            planningNotes = entity.planningNotes,
            startDateTime = entity.startDateTime,
            endDateTime = entity.endDateTime,
            location = entity.location,
            status = entity.status,
            isRecurring = entity.isRecurring,
            recurrencePattern = entity.recurrencePattern,
            budget = entity.budget,
            currency = entity.currency,
            maxAttendees = entity.maxAttendees,
            isPublic = entity.isPublic,
            imageUrl = entity.imageUrl,
            coverPhotoUrl = entity.coverPhotoUrl,
            isDeleted = entity.isDeleted,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            participantCount = 0,
            confirmedCount = 0
        )
    }

    // ─── DTO ↔ Domain ─────────────────────────────────────────────

    fun toDomain(dto: EventDto): Event {
        return Event(
            id = dto.id,
            userId = dto.userId,
            categoryId = dto.categoryId,
            taskId = dto.taskId,
            name = dto.name,
            description = dto.description,
            format = dto.format,
            planningNotes = dto.planningNotes,
            startDateTime = dto.startDateTime,
            endDateTime = dto.endDateTime,
            location = dto.location,
            status = dto.status,
            isRecurring = dto.isRecurring,
            recurrencePattern = dto.recurrencePattern,
            budget = dto.budget,
            currency = dto.currency,
            maxAttendees = dto.maxAttendees,
            isPublic = dto.isPublic,
            imageUrl = dto.imageUrl,
            coverPhotoUrl = dto.coverPhotoUrl,
            isDeleted = dto.isDeleted,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    fun toDto(domain: Event): EventDto {
        return EventDto(
            id = domain.id,
            userId = domain.userId,
            categoryId = domain.categoryId,
            taskId = domain.taskId,
            name = domain.name,
            description = domain.description,
            format = domain.format,
            planningNotes = domain.planningNotes,
            startDateTime = domain.startDateTime,
            endDateTime = domain.endDateTime,
            location = domain.location,
            status = domain.status,
            isRecurring = domain.isRecurring,
            recurrencePattern = domain.recurrencePattern,
            budget = domain.budget,
            currency = domain.currency,
            maxAttendees = domain.maxAttendees,
            isPublic = domain.isPublic,
            imageUrl = domain.imageUrl,
            coverPhotoUrl = domain.coverPhotoUrl,
            isDeleted = domain.isDeleted,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            participantCount = 0,
            confirmedCount = 0
        )
    }

    // ─── Create/Update Requests ────────────────────────────────────

    fun toCreateRequest(domain: Event): CreateEventRequest {
        return CreateEventRequest(
            name = domain.name,
            categoryId = domain.categoryId,
            description = domain.description,
            format = domain.format,
            planningNotes = domain.planningNotes,
            startDateTime = domain.startDateTime,
            endDateTime = domain.endDateTime,
            location = domain.location,
            status = domain.status,
            isRecurring = domain.isRecurring,
            recurrencePattern = domain.recurrencePattern,
            budget = domain.budget,
            currency = domain.currency,
            maxAttendees = domain.maxAttendees,
            isPublic = domain.isPublic,
            imageUrl = domain.imageUrl,
            coverPhotoUrl = domain.coverPhotoUrl,
            participants = emptyList()
        )
    }

    fun toUpdateRequest(domain: Event): UpdateEventRequest {
        return UpdateEventRequest(
            name = domain.name,
            categoryId = domain.categoryId,
            description = domain.description,
            format = domain.format,
            planningNotes = domain.planningNotes,
            startDateTime = domain.startDateTime,
            endDateTime = domain.endDateTime,
            location = domain.location,
            status = domain.status,
            isRecurring = domain.isRecurring,
            recurrencePattern = domain.recurrencePattern,
            budget = domain.budget,
            currency = domain.currency,
            maxAttendees = domain.maxAttendees,
            isPublic = domain.isPublic,
            imageUrl = domain.imageUrl,
            coverPhotoUrl = domain.coverPhotoUrl
        )
    }

    // ─── List Mappings ─────────────────────────────────────────────

    fun toDomainList(entities: List<EventEntity>): List<Event> {
        return entities.map { toDomain(it) }
    }

    fun toEntityList(domains: List<Event>): List<EventEntity> {
        return domains.map { toEntity(it) }
    }

    fun toDomainListFromDto(dtos: List<EventDto>): List<Event> {
        return dtos.map { toDomain(it) }
    }

    fun toEntityListFromDto(dtos: List<EventDto>): List<EventEntity> {
        return dtos.map { toEntity(it) }
    }
}

/**
 * Event Participant Mapper
 */
internal class EventParticipantMapper @Inject constructor() {

    // ─── Entity ↔ Domain ──────────────────────────────────────────

    fun toDomain(entity: EventParticipantEntity): EventParticipant {
        return EventParticipant(
            id = entity.id,
            eventId = entity.eventId,
            userId = entity.userId,
            email = entity.email,
            fullName = entity.fullName,
            invitationStatus = entity.invitationStatus,
            participantRole = entity.participantRole,
            plusOnes = entity.plusOnes,
            dietaryRestrictions = entity.dietaryRestrictions,
            specialRequests = entity.specialRequests,
            checkedIn = entity.checkedIn,
            checkInTime = entity.checkInTime,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toEntity(domain: EventParticipant): EventParticipantEntity {
        return EventParticipantEntity(
            id = domain.id,
            eventId = domain.eventId,
            userId = domain.userId,
            email = domain.email,
            fullName = domain.fullName,
            invitationStatus = domain.invitationStatus,
            participantRole = domain.participantRole,
            plusOnes = domain.plusOnes,
            dietaryRestrictions = domain.dietaryRestrictions,
            specialRequests = domain.specialRequests,
            checkedIn = domain.checkedIn,
            checkInTime = domain.checkInTime,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }

    // ─── DTO ↔ Entity ─────────────────────────────────────────────

    fun toEntity(dto: EventParticipantDto): EventParticipantEntity {
        return EventParticipantEntity(
            id = dto.id ?: java.util.UUID.randomUUID().toString(),
            eventId = dto.eventId ?: "",
            userId = dto.userId,
            email = dto.email,
            fullName = dto.fullName,
            invitationStatus = dto.invitationStatus,
            participantRole = dto.participantRole,
            plusOnes = dto.plusOnes,
            dietaryRestrictions = dto.dietaryRestrictions,
            specialRequests = dto.specialRequests,
            checkedIn = dto.checkedIn,
            checkInTime = dto.checkInTime,
            createdAt = dto.createdAt ?: nowUtc(),
            updatedAt = dto.updatedAt ?: nowUtc()
        )
    }

    fun toDto(entity: EventParticipantEntity): EventParticipantDto {
        return EventParticipantDto(
            id = entity.id,
            eventId = entity.eventId,
            userId = entity.userId,
            email = entity.email,
            fullName = entity.fullName,
            invitationStatus = entity.invitationStatus,
            participantRole = entity.participantRole,
            plusOnes = entity.plusOnes,
            dietaryRestrictions = entity.dietaryRestrictions,
            specialRequests = entity.specialRequests,
            checkedIn = entity.checkedIn,
            checkInTime = entity.checkInTime,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toDomain(dto: EventParticipantDto): EventParticipant {
        return EventParticipant(
            id = dto.id ?: "",
            eventId = dto.eventId ?: "",
            userId = dto.userId,
            email = dto.email,
            fullName = dto.fullName,
            invitationStatus = dto.invitationStatus,
            participantRole = dto.participantRole,
            plusOnes = dto.plusOnes,
            dietaryRestrictions = dto.dietaryRestrictions,
            specialRequests = dto.specialRequests,
            checkedIn = dto.checkedIn,
            checkInTime = dto.checkInTime,
            createdAt = dto.createdAt ?: nowUtc(),
            updatedAt = dto.updatedAt ?: nowUtc()
        )
    }

    fun toDomainList(entities: List<EventParticipantEntity>): List<EventParticipant> {
        return entities.map { toDomain(it) }
    }

    fun toEntityList(domains: List<EventParticipant>): List<EventParticipantEntity> {
        return domains.map { toEntity(it) }
    }

    private fun nowUtc(): String {
        return java.time.Instant.now().toString()
    }
}