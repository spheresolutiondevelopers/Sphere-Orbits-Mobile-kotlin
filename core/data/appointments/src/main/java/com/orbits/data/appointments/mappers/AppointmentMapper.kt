package com.orbits.data.appointments.mappers

import com.orbits.core.common.extensions.nowUtc
import com.orbits.data.appointments.AppointmentEntity
import com.orbits.data.appointments.ParticipantEntity
import com.orbits.data.appointments.remote.AppointmentDto
import com.orbits.data.appointments.remote.ParticipantDto
import com.orbits.data.appointments.remote.CreateAppointmentRequest
import com.orbits.data.appointments.remote.UpdateAppointmentRequest
import com.orbits.domain.appointments.Appointment
import com.orbits.domain.appointments.AppointmentParticipant
import javax.inject.Inject

internal class AppointmentMapper @Inject constructor() {

    // ─── Entity ↔ Domain ──────────────────────────────────────────

    fun toDomain(entity: AppointmentEntity): Appointment {
        return Appointment(
            id = entity.id,
            userId = entity.userId,
            title = entity.title,
            description = entity.description,
            appointmentType = entity.appointmentType,
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

    fun toEntity(domain: Appointment): AppointmentEntity {
        return AppointmentEntity(
            id = domain.id,
            userId = domain.userId,
            title = domain.title,
            description = domain.description,
            appointmentType = domain.appointmentType,
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

    fun toEntity(dto: AppointmentDto): AppointmentEntity {
        return AppointmentEntity(
            id = dto.id,
            userId = dto.userId,
            title = dto.title,
            description = dto.description,
            appointmentType = dto.appointmentType,
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

    fun toDto(entity: AppointmentEntity): AppointmentDto {
        return AppointmentDto(
            id = entity.id,
            userId = entity.userId,
            title = entity.title,
            description = entity.description,
            appointmentType = entity.appointmentType,
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
            updatedAt = entity.updatedAt,
            participantCount = 0
        )
    }

    // ─── DTO ↔ Domain ─────────────────────────────────────────────

    fun toDomain(dto: AppointmentDto): Appointment {
        return Appointment(
            id = dto.id,
            userId = dto.userId,
            title = dto.title,
            description = dto.description,
            appointmentType = dto.appointmentType,
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
            updatedAt = dto.updatedAt
        )
    }

    // ─── Create/Update Requests ────────────────────────────────────

    fun toCreateRequest(domain: Appointment): CreateAppointmentRequest {
        return CreateAppointmentRequest(
            title = domain.title,
            description = domain.description,
            appointmentType = domain.appointmentType,
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

    fun toUpdateRequest(domain: Appointment): UpdateAppointmentRequest {
        return UpdateAppointmentRequest(
            title = domain.title,
            description = domain.description,
            appointmentType = domain.appointmentType,
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

    fun toDomainList(entities: List<AppointmentEntity>): List<Appointment> {
        return entities.map { toDomain(it) }
    }

    fun toEntityList(domains: List<Appointment>): List<AppointmentEntity> {
        return domains.map { toEntity(it) }
    }

    fun toDomainListFromDto(dtos: List<AppointmentDto>): List<Appointment> {
        return dtos.map { toDomain(it) }
    }

    fun toEntityListFromDto(dtos: List<AppointmentDto>): List<AppointmentEntity> {
        return dtos.map { toEntity(it) }
    }
}

/**
 * Participant Mapper
 */
internal class ParticipantMapper @Inject constructor() {

    fun toDomain(entity: ParticipantEntity): AppointmentParticipant {
        return AppointmentParticipant(
            id = entity.id,
            appointmentId = entity.appointmentId,
            userId = entity.userId,
            email = entity.email,
            fullName = entity.fullName,
            invitationStatus = entity.invitationStatus,
            participantRole = entity.participantRole,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toEntity(domain: AppointmentParticipant): ParticipantEntity {
        return ParticipantEntity(
            id = domain.id,
            appointmentId = domain.appointmentId,
            userId = domain.userId,
            email = domain.email,
            fullName = domain.fullName,
            invitationStatus = domain.invitationStatus,
            participantRole = domain.participantRole,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }

    fun toEntity(dto: ParticipantDto): ParticipantEntity {
        return ParticipantEntity(
            id = dto.id ?: java.util.UUID.randomUUID().toString(),
            appointmentId = dto.appointmentId ?: "",
            userId = dto.userId,
            email = dto.email,
            fullName = dto.fullName,
            invitationStatus = dto.invitationStatus,
            participantRole = dto.participantRole,
            createdAt = dto.createdAt ?: nowUtc(),
            updatedAt = dto.updatedAt ?: nowUtc()
        )
    }

    fun toDto(entity: ParticipantEntity): ParticipantDto {
        return ParticipantDto(
            id = entity.id,
            appointmentId = entity.appointmentId,
            userId = entity.userId,
            email = entity.email,
            fullName = entity.fullName,
            invitationStatus = entity.invitationStatus,
            participantRole = entity.participantRole,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toDomain(dto: ParticipantDto): AppointmentParticipant {
        return AppointmentParticipant(
            id = dto.id ?: "",
            appointmentId = dto.appointmentId ?: "",
            userId = dto.userId,
            email = dto.email,
            fullName = dto.fullName,
            invitationStatus = dto.invitationStatus,
            participantRole = dto.participantRole,
            createdAt = dto.createdAt ?: nowUtc(),
            updatedAt = dto.updatedAt ?: nowUtc()
        )
    }

    fun toDomainList(entities: List<ParticipantEntity>): List<AppointmentParticipant> {
        return entities.map { toDomain(it) }
    }

    fun toEntityList(domains: List<AppointmentParticipant>): List<ParticipantEntity> {
        return domains.map { toEntity(it) }
    }

    private fun nowUtc(): String {
        return java.time.Instant.now().toString()
    }
}