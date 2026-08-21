package com.orbits.data.chat.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Message Entity — Room database representation.
 * ISOLATED in :core:data:chat. Changes here ONLY recompile this module.
 *
 * Messages are part of conversations. They can be text, images,
 * files, or other types of content.
 */
@Entity(
    tableName = "messages",
    indices = [
        Index(value = ["conversation_id"]),
        Index(value = ["conversation_id", "sent_at"]),
        Index(value = ["sender_user_id"]),
        Index(value = ["is_read"]),
        Index(value = ["created_at"])
    ]
)
internal data class MessageEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "conversation_id")
    val conversationId: String,

    @ColumnInfo(name = "sender_user_id")
    val senderUserId: String,

    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "content_type")
    val contentType: String = "text", // text, image, file, audio, video, location

    @ColumnInfo(name = "media_url")
    val mediaUrl: String? = null,

    @ColumnInfo(name = "media_thumbnail_url")
    val mediaThumbnailUrl: String? = null,

    @ColumnInfo(name = "media_width")
    val mediaWidth: Int? = null,

    @ColumnInfo(name = "media_height")
    val mediaHeight: Int? = null,

    @ColumnInfo(name = "media_size")
    val mediaSize: Long? = null,

    @ColumnInfo(name = "file_name")
    val fileName: String? = null,

    @ColumnInfo(name = "file_extension")
    val fileExtension: String? = null,

    @ColumnInfo(name = "reply_to_message_id")
    val replyToMessageId: String? = null,

    @ColumnInfo(name = "sent_at")
    val sentAt: String,

    @ColumnInfo(name = "is_read")
    val isRead: Boolean = false,

    @ColumnInfo(name = "read_at")
    val readAt: String? = null,

    @ColumnInfo(name = "is_delivered")
    val isDelivered: Boolean = false,

    @ColumnInfo(name = "delivered_at")
    val deliveredAt: String? = null,

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "updated_at")
    val updatedAt: String
)

/**
 * Conversation Entity — represents a chat thread.
 */
@Entity(
    tableName = "conversations",
    indices = [
        Index(value = ["type"]),
        Index(value = ["updated_at"])
    ]
)
internal data class ConversationEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "type")
    val type: String = "direct", // direct, group

    @ColumnInfo(name = "name")
    val name: String? = null,

    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String? = null,

    @ColumnInfo(name = "last_message_id")
    val lastMessageId: String? = null,

    @ColumnInfo(name = "last_message_content")
    val lastMessageContent: String? = null,

    @ColumnInfo(name = "last_message_sent_at")
    val lastMessageSentAt: String? = null,

    @ColumnInfo(name = "is_archived")
    val isArchived: Boolean = false,

    @ColumnInfo(name = "is_muted")
    val isMuted: Boolean = false,

    @ColumnInfo(name = "muted_until")
    val mutedUntil: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "updated_at")
    val updatedAt: String
)

/**
 * Conversation Participant Entity — users in a conversation.
 */
@Entity(
    tableName = "conversation_participants",
    indices = [
        Index(value = ["conversation_id"]),
        Index(value = ["user_id"]),
        Index(value = ["conversation_id", "user_id"], unique = true)
    ]
)
internal data class ConversationParticipantEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "conversation_id")
    val conversationId: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "role")
    val role: String = "member", // admin, member, viewer

    @ColumnInfo(name = "joined_at")
    val joinedAt: String,

    @ColumnInfo(name = "left_at")
    val leftAt: String? = null,

    @ColumnInfo(name = "last_read_message_id")
    val lastReadMessageId: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "updated_at")
    val updatedAt: String
)