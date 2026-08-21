package com.orbits.data.chat.mappers

import com.orbits.core.common.extensions.nowUtc
import com.orbits.core.model.UserDto
import com.orbits.data.chat.local.MessageEntity
import com.orbits.data.chat.local.ConversationEntity
import com.orbits.data.chat.local.ConversationParticipantEntity
import com.orbits.data.chat.remote.MessageDto
import com.orbits.data.chat.remote.ConversationDto
import com.orbits.data.chat.remote.SendMessageRequest
import com.orbits.domain.chat.Message
import com.orbits.domain.chat.Conversation
import com.orbits.domain.chat.ConversationParticipant
import javax.inject.Inject

internal class MessageMapper @Inject constructor() {

    // ─── Message: Entity ↔ Domain ────────────────────────────────

    fun toDomain(entity: MessageEntity): Message {
        return Message(
            id = entity.id,
            conversationId = entity.conversationId,
            senderUserId = entity.senderUserId,
            content = entity.content,
            contentType = entity.contentType,
            mediaUrl = entity.mediaUrl,
            mediaThumbnailUrl = entity.mediaThumbnailUrl,
            mediaWidth = entity.mediaWidth,
            mediaHeight = entity.mediaHeight,
            mediaSize = entity.mediaSize,
            fileName = entity.fileName,
            fileExtension = entity.fileExtension,
            replyToMessageId = entity.replyToMessageId,
            sentAt = entity.sentAt,
            isRead = entity.isRead,
            readAt = entity.readAt,
            isDelivered = entity.isDelivered,
            deliveredAt = entity.deliveredAt,
            isDeleted = entity.isDeleted,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toEntity(domain: Message): MessageEntity {
        return MessageEntity(
            id = domain.id,
            conversationId = domain.conversationId,
            senderUserId = domain.senderUserId,
            content = domain.content,
            contentType = domain.contentType,
            mediaUrl = domain.mediaUrl,
            mediaThumbnailUrl = domain.mediaThumbnailUrl,
            mediaWidth = domain.mediaWidth,
            mediaHeight = domain.mediaHeight,
            mediaSize = domain.mediaSize,
            fileName = domain.fileName,
            fileExtension = domain.fileExtension,
            replyToMessageId = domain.replyToMessageId,
            sentAt = domain.sentAt,
            isRead = domain.isRead,
            readAt = domain.readAt,
            isDelivered = domain.isDelivered,
            deliveredAt = domain.deliveredAt,
            isDeleted = domain.isDeleted,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }

    // ─── Message: DTO ↔ Entity ───────────────────────────────────

    fun toEntity(dto: MessageDto): MessageEntity {
        return MessageEntity(
            id = dto.id,
            conversationId = dto.conversationId,
            senderUserId = dto.senderUserId,
            content = dto.content,
            contentType = dto.contentType,
            mediaUrl = dto.mediaUrl,
            mediaThumbnailUrl = dto.mediaThumbnailUrl,
            mediaWidth = dto.mediaWidth,
            mediaHeight = dto.mediaHeight,
            mediaSize = dto.mediaSize,
            fileName = dto.fileName,
            fileExtension = dto.fileExtension,
            replyToMessageId = dto.replyToMessageId,
            sentAt = dto.sentAt,
            isRead = dto.isRead,
            readAt = dto.readAt,
            isDelivered = dto.isDelivered,
            deliveredAt = dto.deliveredAt,
            isDeleted = dto.isDeleted,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    fun toDto(entity: MessageEntity): MessageDto {
        return MessageDto(
            id = entity.id,
            conversationId = entity.conversationId,
            senderUserId = entity.senderUserId,
            sender = null,
            content = entity.content,
            contentType = entity.contentType,
            mediaUrl = entity.mediaUrl,
            mediaThumbnailUrl = entity.mediaThumbnailUrl,
            mediaWidth = entity.mediaWidth,
            mediaHeight = entity.mediaHeight,
            mediaSize = entity.mediaSize,
            fileName = entity.fileName,
            fileExtension = entity.fileExtension,
            replyToMessageId = entity.replyToMessageId,
            sentAt = entity.sentAt,
            isRead = entity.isRead,
            readAt = entity.readAt,
            isDelivered = entity.isDelivered,
            deliveredAt = entity.deliveredAt,
            isDeleted = entity.isDeleted,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    // ─── Message: DTO ↔ Domain ───────────────────────────────────

    fun toDomain(dto: MessageDto): Message {
        return Message(
            id = dto.id,
            conversationId = dto.conversationId,
            senderUserId = dto.senderUserId,
            content = dto.content,
            contentType = dto.contentType,
            mediaUrl = dto.mediaUrl,
            mediaThumbnailUrl = dto.mediaThumbnailUrl,
            mediaWidth = dto.mediaWidth,
            mediaHeight = dto.mediaHeight,
            mediaSize = dto.mediaSize,
            fileName = dto.fileName,
            fileExtension = dto.fileExtension,
            replyToMessageId = dto.replyToMessageId,
            sentAt = dto.sentAt,
            isRead = dto.isRead,
            readAt = dto.readAt,
            isDelivered = dto.isDelivered,
            deliveredAt = dto.deliveredAt,
            isDeleted = dto.isDeleted,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    // ─── Send Request ─────────────────────────────────────────────

    fun toSendRequest(domain: Message): SendMessageRequest {
        return SendMessageRequest(
            content = domain.content,
            contentType = domain.contentType,
            mediaUrl = domain.mediaUrl,
            mediaThumbnailUrl = domain.mediaThumbnailUrl,
            mediaWidth = domain.mediaWidth,
            mediaHeight = domain.mediaHeight,
            mediaSize = domain.mediaSize,
            fileName = domain.fileName,
            fileExtension = domain.fileExtension,
            replyToMessageId = domain.replyToMessageId
        )
    }

    // ─── List Mappings ─────────────────────────────────────────────

    fun toDomainList(entities: List<MessageEntity>): List<Message> {
        return entities.map { toDomain(it) }
    }

    fun toEntityList(domains: List<Message>): List<MessageEntity> {
        return domains.map { toEntity(it) }
    }

    fun toDomainListFromDto(dtos: List<MessageDto>): List<Message> {
        return dtos.map { toDomain(it) }
    }
}

/**
 * Conversation Mapper
 */
internal class ConversationMapper @Inject constructor(
    private val messageMapper: MessageMapper
) {

    // ─── Entity ↔ Domain ──────────────────────────────────────────

    fun toDomain(entity: ConversationEntity): Conversation {
        return Conversation(
            id = entity.id,
            type = entity.type,
            name = entity.name,
            avatarUrl = entity.avatarUrl,
            isArchived = entity.isArchived,
            isMuted = entity.isMuted,
            mutedUntil = entity.mutedUntil,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toEntity(domain: Conversation): ConversationEntity {
        return ConversationEntity(
            id = domain.id,
            type = domain.type,
            name = domain.name,
            avatarUrl = domain.avatarUrl,
            isArchived = domain.isArchived,
            isMuted = domain.isMuted,
            mutedUntil = domain.mutedUntil,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            lastMessageId = null,
            lastMessageContent = null,
            lastMessageSentAt = null
        )
    }

    // ─── DTO ↔ Entity ─────────────────────────────────────────────

    fun toEntity(dto: ConversationDto): ConversationEntity {
        return ConversationEntity(
            id = dto.id,
            type = dto.type,
            name = dto.name,
            avatarUrl = dto.avatarUrl,
            isArchived = dto.isArchived,
            isMuted = dto.isMuted,
            mutedUntil = dto.mutedUntil,
            lastMessageId = dto.lastMessage?.id,
            lastMessageContent = dto.lastMessage?.content,
            lastMessageSentAt = dto.lastMessage?.sentAt,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    fun toDto(entity: ConversationEntity): ConversationDto {
        return ConversationDto(
            id = entity.id,
            type = entity.type,
            name = entity.name,
            avatarUrl = entity.avatarUrl,
            unreadCount = 0,
            isArchived = entity.isArchived,
            isMuted = entity.isMuted,
            mutedUntil = entity.mutedUntil,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            participants = null,
            lastMessage = null
        )
    }

    // ─── DTO ↔ Domain ─────────────────────────────────────────────

    fun toDomain(dto: ConversationDto): Conversation {
        return Conversation(
            id = dto.id,
            type = dto.type,
            name = dto.name,
            avatarUrl = dto.avatarUrl,
            isArchived = dto.isArchived,
            isMuted = dto.isMuted,
            mutedUntil = dto.mutedUntil,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    // ─── Create Request ───────────────────────────────────────────

    fun toCreateRequest(domain: Conversation, participantIds: List<String>): CreateConversationRequest {
        return CreateConversationRequest(
            participantIds = participantIds,
            name = domain.name,
            avatarUrl = domain.avatarUrl,
            type = domain.type
        )
    }

    // ─── Update Request ───────────────────────────────────────────

    fun toUpdateRequest(domain: Conversation): UpdateConversationRequest {
        return UpdateConversationRequest(
            name = domain.name,
            avatarUrl = domain.avatarUrl,
            isArchived = domain.isArchived,
            isMuted = domain.isMuted,
            mutedUntil = domain.mutedUntil
        )
    }

    // ─── List Mappings ─────────────────────────────────────────────

    fun toDomainList(entities: List<ConversationEntity>): List<Conversation> {
        return entities.map { toDomain(it) }
    }

    fun toEntityList(domains: List<Conversation>): List<ConversationEntity> {
        return domains.map { toEntity(it) }
    }

    fun toDomainListFromDto(dtos: List<ConversationDto>): List<Conversation> {
        return dtos.map { toDomain(it) }
    }
}

/**
 * Conversation Participant Mapper
 */
internal class ConversationParticipantMapper @Inject constructor() {

    fun toDomain(entity: ConversationParticipantEntity): ConversationParticipant {
        return ConversationParticipant(
            id = entity.id,
            conversationId = entity.conversationId,
            userId = entity.userId,
            role = entity.role,
            joinedAt = entity.joinedAt,
            leftAt = entity.leftAt,
            lastReadMessageId = entity.lastReadMessageId,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toEntity(domain: ConversationParticipant): ConversationParticipantEntity {
        return ConversationParticipantEntity(
            id = domain.id,
            conversationId = domain.conversationId,
            userId = domain.userId,
            role = domain.role,
            joinedAt = domain.joinedAt,
            leftAt = domain.leftAt,
            lastReadMessageId = domain.lastReadMessageId,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }

    fun toDomainList(entities: List<ConversationParticipantEntity>): List<ConversationParticipant> {
        return entities.map { toDomain(it) }
    }

    fun toEntityList(domains: List<ConversationParticipant>): List<ConversationParticipantEntity> {
        return domains.map { toEntity(it) }
    }
}