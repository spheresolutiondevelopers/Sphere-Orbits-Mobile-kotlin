package com.orbits.data.meetings.mappers

import com.orbits.core.common.extensions.nowUtc
import com.orbits.data.meetings.MeetingEntity
import com.orbits.data.meetings.remote.MeetingDto
import com.orbits.data.meetings.remote.ActionItemDto
import com.orbits.data.meetings.remote.MeetingParticipantDto
import com.orbits.data.meetings.remote.CreateMeetingRequest
import com.orbits.data.meetings.remote.UpdateMeetingRequest
import com.orbits.data.meetings.remote.ZoomMeetingRequest
import com.orbits.data.meetings.remote.TeamsMeetingRequest
import com.orbits.data.meetings.remote.TeamsBody
import com.orbits.data.meetings.remote.TeamsDateTime
import com.orbits.data.meetings.MeetingParticipantEntity
import com.orbits.domain.meetings.Meeting
import com.orbits.domain.meetings.MeetingParticipant
import com.orbits.domain.meetings.ActionItem
import javax.inject.Inject

internal class MeetingMapper @Inject constructor() {

    // ─── Entity ↔ Domain ──────────────────────────────────────────

    fun toDomain(entity: MeetingEntity): Meeting {
        return Meeting(
            id = entity.id,
            taskId = entity.taskId,
            organizerUserId = entity.organizerUserId,
            title = entity.title,
            description = entity.description,
            startDateTime = entity.startDateTime,
            endDateTime = entity.endDateTime,
            meetingLink = entity.meetingLink,
            meetingPlatform = entity.meetingPlatform,
            meetingId = entity.meetingId,
            passcode = entity.passcode,
            agenda = entity.agenda,
            minutes = entity.minutes,
            actionItems = entity.actionItems?.let { parseActionItems(it) } ?: emptyList(),
            isRecurring = entity.isRecurring,
            recurrencePattern = entity.recurrencePattern,
            status = entity.status,
            recordingUrl = entity.recordingUrl,
            transcriptUrl = entity.transcriptUrl,
            isDeleted = entity.isDeleted,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toEntity(domain: Meeting): MeetingEntity {
        return MeetingEntity(
            id = domain.id,
            taskId = domain.taskId,
            organizerUserId = domain.organizerUserId,
            title = domain.title,
            description = domain.description,
            startDateTime = domain.startDateTime,
            endDateTime = domain.endDateTime,
            meetingLink = domain.meetingLink,
            meetingPlatform = domain.meetingPlatform,
            meetingId = domain.meetingId,
            passcode = domain.passcode,
            agenda = domain.agenda,
            minutes = domain.minutes,
            actionItems = if (domain.actionItems.isNotEmpty()) {
                // Serialize action items to JSON
                "[]" // Simplified
            } else null,
            isRecurring = domain.isRecurring,
            recurrencePattern = domain.recurrencePattern,
            status = domain.status,
            recordingUrl = domain.recordingUrl,
            transcriptUrl = domain.transcriptUrl,
            isDeleted = domain.isDeleted,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            syncedAt = null,
            syncStatus = "synced"
        )
    }

    // ─── DTO ↔ Entity ─────────────────────────────────────────────

    fun toEntity(dto: MeetingDto): MeetingEntity {
        return MeetingEntity(
            id = dto.id,
            taskId = dto.taskId,
            organizerUserId = dto.organizerUserId,
            title = dto.title,
            description = dto.description,
            startDateTime = dto.startDateTime,
            endDateTime = dto.endDateTime,
            meetingLink = dto.meetingLink,
            meetingPlatform = dto.meetingPlatform,
            meetingId = dto.meetingId,
            passcode = dto.passcode,
            agenda = dto.agenda,
            minutes = dto.minutes,
            actionItems = dto.actionItems?.let { "[]" },
            isRecurring = dto.isRecurring,
            recurrencePattern = dto.recurrencePattern,
            status = dto.status,
            recordingUrl = dto.recordingUrl,
            transcriptUrl = dto.transcriptUrl,
            isDeleted = dto.isDeleted,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,
            syncedAt = null,
            syncStatus = "synced"
        )
    }

    fun toDto(entity: MeetingEntity): MeetingDto {
        return MeetingDto(
            id = entity.id,
            taskId = entity.taskId,
            organizerUserId = entity.organizerUserId,
            title = entity.title,
            description = entity.description,
            startDateTime = entity.startDateTime,
            endDateTime = entity.endDateTime,
            meetingLink = entity.meetingLink,
            meetingPlatform = entity.meetingPlatform,
            meetingId = entity.meetingId,
            passcode = entity.passcode,
            agenda = entity.agenda,
            minutes = entity.minutes,
            actionItems = emptyList(),
            isRecurring = entity.isRecurring,
            recurrencePattern = entity.recurrencePattern,
            status = entity.status,
            recordingUrl = entity.recordingUrl,
            transcriptUrl = entity.transcriptUrl,
            isDeleted = entity.isDeleted,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            participantCount = 0,
            attendedCount = 0
        )
    }

    // ─── DTO ↔ Domain ─────────────────────────────────────────────

    fun toDomain(dto: MeetingDto): Meeting {
        return Meeting(
            id = dto.id,
            taskId = dto.taskId,
            organizerUserId = dto.organizerUserId,
            title = dto.title,
            description = dto.description,
            startDateTime = dto.startDateTime,
            endDateTime = dto.endDateTime,
            meetingLink = dto.meetingLink,
            meetingPlatform = dto.meetingPlatform,
            meetingId = dto.meetingId,
            passcode = dto.passcode,
            agenda = dto.agenda,
            minutes = dto.minutes,
            actionItems = dto.actionItems?.map { toDomain(it) } ?: emptyList(),
            isRecurring = dto.isRecurring,
            recurrencePattern = dto.recurrencePattern,
            status = dto.status,
            recordingUrl = dto.recordingUrl,
            transcriptUrl = dto.transcriptUrl,
            isDeleted = dto.isDeleted,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    // ─── Create/Update Requests ────────────────────────────────────

    fun toCreateRequest(domain: Meeting): CreateMeetingRequest {
        return CreateMeetingRequest(
            taskId = domain.taskId,
            title = domain.title,
            description = domain.description,
            startDateTime = domain.startDateTime,
            endDateTime = domain.endDateTime,
            meetingPlatform = domain.meetingPlatform,
            agenda = domain.agenda,
            isRecurring = domain.isRecurring,
            recurrencePattern = domain.recurrencePattern,
            participants = emptyList()
        )
    }

    fun toUpdateRequest(domain: Meeting): UpdateMeetingRequest {
        return UpdateMeetingRequest(
            title = domain.title,
            description = domain.description,
            startDateTime = domain.startDateTime,
            endDateTime = domain.endDateTime,
            meetingPlatform = domain.meetingPlatform,
            agenda = domain.agenda,
            minutes = domain.minutes,
            actionItems = domain.actionItems.map { toDto(it) },
            isRecurring = domain.isRecurring,
            recurrencePattern = domain.recurrencePattern,
            status = domain.status,
            recordingUrl = domain.recordingUrl,
            transcriptUrl = domain.transcriptUrl
        )
    }

    // ─── Zoom/Teams Conversion ────────────────────────────────────

    fun toZoomRequest(domain: Meeting): ZoomMeetingRequest {
        return ZoomMeetingRequest(
            topic = domain.title,
            type = 2,
            startTime = domain.startDateTime,
            duration = calculateDurationMinutes(domain.startDateTime, domain.endDateTime),
            timezone = java.time.ZoneId.systemDefault().id,
            agenda = domain.agenda,
            password = domain.passcode
        )
    }

    fun toTeamsRequest(domain: Meeting): TeamsMeetingRequest {
        return TeamsMeetingRequest(
            subject = domain.title,
            body = domain.description?.let {
                TeamsBody(content = it)
            },
            start = TeamsDateTime(
                dateTime = domain.startDateTime,
                timeZone = java.time.ZoneId.systemDefault().id
            ),
            end = TeamsDateTime(
                dateTime = domain.endDateTime,
                timeZone = java.time.ZoneId.systemDefault().id
            ),
            isOnlineMeeting = true,
            onlineMeetingProvider = "teamsForBusiness"
        )
    }

    // ─── List Mappings ─────────────────────────────────────────────

    fun toDomainList(entities: List<MeetingEntity>): List<Meeting> {
        return entities.map { toDomain(it) }
    }

    fun toEntityList(domains: List<Meeting>): List<MeetingEntity> {
        return domains.map { toEntity(it) }
    }

    fun toDomainListFromDto(dtos: List<MeetingDto>): List<Meeting> {
        return dtos.map { toDomain(it) }
    }

    // ─── Private Helpers ──────────────────────────────────────────

    private fun calculateDurationMinutes(start: String, end: String): Int {
        return try {
            val startInstant = java.time.Instant.parse(start)
            val endInstant = java.time.Instant.parse(end)
            val durationSeconds = java.time.Duration.between(startInstant, endInstant).seconds
            (durationSeconds / 60).toInt()
        } catch (e: Exception) {
            60 // Default 60 minutes
        }
    }

    private fun parseActionItems(json: String): List<ActionItem> {
        // Simplified parsing
        return emptyList()
    }

    private fun toDomain(dto: ActionItemDto): ActionItem {
        return ActionItem(
            id = dto.id,
            meetingId = dto.meetingId,
            description = dto.description,
            assignedTo = dto.assignedTo,
            dueDate = dto.dueDate,
            status = dto.status,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    private fun toDto(domain: ActionItem): ActionItemDto {
        return ActionItemDto(
            id = domain.id,
            meetingId = domain.meetingId,
            description = domain.description,
            assignedTo = domain.assignedTo,
            dueDate = domain.dueDate,
            status = domain.status,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }
}

/**
 * Meeting Participant Mapper
 */
internal class MeetingParticipantMapper @Inject constructor() {

    fun toDomain(entity: MeetingParticipantEntity): MeetingParticipant {
        return MeetingParticipant(
            id = entity.id,
            meetingId = entity.meetingId,
            userId = entity.userId,
            email = entity.email,
            fullName = entity.fullName,
            invitationStatus = entity.invitationStatus,
            participantRole = entity.participantRole,
            joinedAt = entity.joinedAt,
            leftAt = entity.leftAt,
            durationSeconds = entity.durationSeconds,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toEntity(domain: MeetingParticipant): MeetingParticipantEntity {
        return MeetingParticipantEntity(
            id = domain.id,
            meetingId = domain.meetingId,
            userId = domain.userId,
            email = domain.email,
            fullName = domain.fullName,
            invitationStatus = domain.invitationStatus,
            participantRole = domain.participantRole,
            joinedAt = domain.joinedAt,
            leftAt = domain.leftAt,
            durationSeconds = domain.durationSeconds,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }

    fun toEntity(dto: MeetingParticipantDto): MeetingParticipantEntity {
        return MeetingParticipantEntity(
            id = dto.id ?: java.util.UUID.randomUUID().toString(),
            meetingId = dto.meetingId ?: "",
            userId = dto.userId,
            email = dto.email,
            fullName = dto.fullName,
            invitationStatus = dto.invitationStatus,
            participantRole = dto.participantRole,
            joinedAt = dto.joinedAt,
            leftAt = dto.leftAt,
            durationSeconds = dto.durationSeconds,
            createdAt = dto.createdAt ?: nowUtc(),
            updatedAt = dto.updatedAt ?: nowUtc()
        )
    }

    fun toDto(entity: MeetingParticipantEntity): MeetingParticipantDto {
        return MeetingParticipantDto(
            id = entity.id,
            meetingId = entity.meetingId,
            userId = entity.userId,
            email = entity.email,
            fullName = entity.fullName,
            invitationStatus = entity.invitationStatus,
            participantRole = entity.participantRole,
            joinedAt = entity.joinedAt,
            leftAt = entity.leftAt,
            durationSeconds = entity.durationSeconds,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toDomainList(entities: List<MeetingParticipantEntity>): List<MeetingParticipant> {
        return entities.map { toDomain(it) }
    }

    fun toEntityList(domains: List<MeetingParticipant>): List<MeetingParticipantEntity> {
        return domains.map { toEntity(it) }
    }

    private fun nowUtc(): String {
        return java.time.Instant.now().toString()
    }
}