package com.orbits.data.notes.mappers

import com.orbits.core.common.extensions.nowUtc
import com.orbits.data.notes.local.NoteEntity
import com.orbits.data.notes.remote.NoteDto
import com.orbits.data.notes.remote.CreateNoteRequest
import com.orbits.data.notes.remote.UpdateNoteRequest
import com.orbits.domain.notes.Note
import javax.inject.Inject

internal class NoteMapper @Inject constructor() {

    // ─── Entity ↔ Domain ──────────────────────────────────────────

    fun toDomain(entity: NoteEntity): Note {
        return Note(
            id = entity.id,
            userId = entity.userId,
            title = entity.title,
            content = entity.content,
            contentFormat = entity.contentFormat,
            tags = if (entity.tags.isNullOrBlank()) {
                emptyList()
            } else {
                entity.tags.split(",").map { it.trim() }.filter { it.isNotBlank() }
            },
            isPinned = entity.isPinned,
            isArchived = entity.isArchived,
            color = entity.color,
            taskId = entity.taskId,
            eventId = entity.eventId,
            appointmentId = entity.appointmentId,
            meetingId = entity.meetingId,
            reminderAt = entity.reminderAt,
            isDeleted = entity.isDeleted,
            deletedAt = entity.deletedAt,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toEntity(domain: Note): NoteEntity {
        return NoteEntity(
            id = domain.id,
            userId = domain.userId,
            title = domain.title,
            content = domain.content,
            contentFormat = domain.contentFormat,
            tags = if (domain.tags.isEmpty()) null else domain.tags.joinToString(","),
            isPinned = domain.isPinned,
            isArchived = domain.isArchived,
            color = domain.color,
            taskId = domain.taskId,
            eventId = domain.eventId,
            appointmentId = domain.appointmentId,
            meetingId = domain.meetingId,
            reminderAt = domain.reminderAt,
            isDeleted = domain.isDeleted,
            deletedAt = domain.deletedAt,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            syncedAt = null,
            syncStatus = "synced"
        )
    }

    // ─── DTO ↔ Entity ─────────────────────────────────────────────

    fun toEntity(dto: NoteDto): NoteEntity {
        return NoteEntity(
            id = dto.id,
            userId = dto.userId,
            title = dto.title,
            content = dto.content,
            contentFormat = dto.contentFormat,
            tags = if (dto.tags.isEmpty()) null else dto.tags.joinToString(","),
            isPinned = dto.isPinned,
            isArchived = dto.isArchived,
            color = dto.color,
            taskId = dto.taskId,
            eventId = dto.eventId,
            appointmentId = dto.appointmentId,
            meetingId = dto.meetingId,
            reminderAt = dto.reminderAt,
            isDeleted = dto.isDeleted,
            deletedAt = dto.deletedAt,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,
            syncedAt = null,
            syncStatus = "synced"
        )
    }

    fun toDto(entity: NoteEntity): NoteDto {
        return NoteDto(
            id = entity.id,
            userId = entity.userId,
            title = entity.title,
            content = entity.content,
            contentFormat = entity.contentFormat,
            tags = if (entity.tags.isNullOrBlank()) {
                emptyList()
            } else {
                entity.tags.split(",").map { it.trim() }.filter { it.isNotBlank() }
            },
            isPinned = entity.isPinned,
            isArchived = entity.isArchived,
            color = entity.color,
            taskId = entity.taskId,
            eventId = entity.eventId,
            appointmentId = entity.appointmentId,
            meetingId = entity.meetingId,
            reminderAt = entity.reminderAt,
            isDeleted = entity.isDeleted,
            deletedAt = entity.deletedAt,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    // ─── DTO ↔ Domain ─────────────────────────────────────────────

    fun toDomain(dto: NoteDto): Note {
        return Note(
            id = dto.id,
            userId = dto.userId,
            title = dto.title,
            content = dto.content,
            contentFormat = dto.contentFormat,
            tags = dto.tags,
            isPinned = dto.isPinned,
            isArchived = dto.isArchived,
            color = dto.color,
            taskId = dto.taskId,
            eventId = dto.eventId,
            appointmentId = dto.appointmentId,
            meetingId = dto.meetingId,
            reminderAt = dto.reminderAt,
            isDeleted = dto.isDeleted,
            deletedAt = dto.deletedAt,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    // ─── Create/Update Requests ────────────────────────────────────

    fun toCreateRequest(domain: Note): CreateNoteRequest {
        return CreateNoteRequest(
            title = domain.title,
            content = domain.content,
            contentFormat = domain.contentFormat,
            tags = domain.tags,
            isPinned = domain.isPinned,
            isArchived = domain.isArchived,
            color = domain.color,
            taskId = domain.taskId,
            eventId = domain.eventId,
            appointmentId = domain.appointmentId,
            meetingId = domain.meetingId,
            reminderAt = domain.reminderAt
        )
    }

    fun toUpdateRequest(domain: Note): UpdateNoteRequest {
        return UpdateNoteRequest(
            title = domain.title,
            content = domain.content,
            contentFormat = domain.contentFormat,
            tags = domain.tags,
            isPinned = domain.isPinned,
            isArchived = domain.isArchived,
            color = domain.color,
            taskId = domain.taskId,
            eventId = domain.eventId,
            appointmentId = domain.appointmentId,
            meetingId = domain.meetingId,
            reminderAt = domain.reminderAt
        )
    }

    // ─── List Mappings ─────────────────────────────────────────────

    fun toDomainList(entities: List<NoteEntity>): List<Note> {
        return entities.map { toDomain(it) }
    }

    fun toEntityList(domains: List<Note>): List<NoteEntity> {
        return domains.map { toEntity(it) }
    }

    fun toDomainListFromDto(dtos: List<NoteDto>): List<Note> {
        return dtos.map { toDomain(it) }
    }

    fun toEntityListFromDto(dtos: List<NoteDto>): List<NoteEntity> {
        return dtos.map { toEntity(it) }
    }
}