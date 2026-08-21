package com.orbits.data.chat.remote

import com.orbits.core.model.UserDto
import kotlinx.serialization.Serializable

@Serializable
internal data class ConversationDto(
    val id: String,
    val type: String, // direct, group
    val name: String? = null,
    val avatarUrl: String? = null,
    val participants: List<UserDto>? = null,
    val lastMessage: MessageDto? = null,
    val unreadCount: Int = 0,
    val isArchived: Boolean = false,
    val isMuted: Boolean = false,
    val mutedUntil: String? = null,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
internal data class MessageDto(
    val id: String,
    val conversationId: String,
    val senderUserId: String,
    val sender: UserDto? = null,
    val content: String,
    val contentType: String = "text",
    val mediaUrl: String? = null,
    val mediaThumbnailUrl: String? = null,
    val mediaWidth: Int? = null,
    val mediaHeight: Int? = null,
    val mediaSize: Long? = null,
    val fileName: String? = null,
    val fileExtension: String? = null,
    val replyToMessageId: String? = null,
    val sentAt: String,
    val isRead: Boolean = false,
    val readAt: String? = null,
    val isDelivered: Boolean = false,
    val deliveredAt: String? = null,
    val isDeleted: Boolean = false,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
internal data class SendMessageRequest(
    val content: String,
    val contentType: String = "text",
    val mediaUrl: String? = null,
    val mediaThumbnailUrl: String? = null,
    val mediaWidth: Int? = null,
    val mediaHeight: Int? = null,
    val mediaSize: Long? = null,
    val fileName: String? = null,
    val fileExtension: String? = null,
    val replyToMessageId: String? = null
)

@Serializable
internal data class CreateConversationRequest(
    val participantIds: List<String>,
    val name: String? = null,
    val avatarUrl: String? = null,
    val type: String = "direct" // direct, group
)

@Serializable
internal data class UpdateConversationRequest(
    val name: String? = null,
    val avatarUrl: String? = null,
    val isArchived: Boolean? = null,
    val isMuted: Boolean? = null,
    val mutedUntil: String? = null
)

@Serializable
internal data class AddParticipantsRequest(
    val participantIds: List<String>
)

@Serializable
internal data class RemoveParticipantRequest(
    val userId: String
)

@Serializable
internal data class TypingIndicatorRequest(
    val conversationId: String,
    val userId: String,
    val isTyping: Boolean
)

@Serializable
internal data class MessageReadReceipt(
    val messageId: String,
    val userId: String,
    val readAt: String
)

@Serializable
internal data class WebSocketMessage(
    val type: String, // message, typing, read, delivered, presence
    val data: String // JSON string of the actual payload
)

@Serializable
internal data class WebSocketMessagePayload(
    val message: MessageDto? = null,
    val conversationId: String? = null,
    val userId: String? = null,
    val isTyping: Boolean? = null,
    val messageId: String? = null,
    val readAt: String? = null,
    val status: String? = null // online, offline, away
)