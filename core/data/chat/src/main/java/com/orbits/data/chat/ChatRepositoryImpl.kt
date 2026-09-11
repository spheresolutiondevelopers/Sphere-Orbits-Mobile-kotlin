package com.orbits.data.chat

import com.orbits.core.common.Result
import com.orbits.core.common.Logger
import com.orbits.core.common.TokenProvider
import com.orbits.core.common.extensions.nowUtc
import com.orbits.core.database.dao.MessageDao
import com.orbits.core.database.dao.SyncQueueDao
import com.orbits.core.network.api.ChatApi
import com.orbits.core.network.error.ApiErrorParser
import com.orbits.data.sync.SyncQueueEntity
import com.orbits.data.chat.MessageEntity
import com.orbits.data.chat.ConversationEntity
import com.orbits.data.chat.ConversationParticipantEntity
import com.orbits.data.chat.mappers.MessageMapper
import com.orbits.data.chat.mappers.ConversationMapper
import com.orbits.data.chat.mappers.ConversationParticipantMapper
import com.orbits.data.chat.remote.WebSocketMessage
import com.orbits.data.chat.remote.WebSocketMessagePayload
import com.orbits.domain.chat.Message
import com.orbits.domain.chat.Conversation
import com.orbits.domain.chat.ConversationParticipant
import com.orbits.domain.chat.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ChatRepositoryImpl @Inject constructor(
    private val messageDao: MessageDao,
    private val syncQueueDao: SyncQueueDao,
    private val chatApi: ChatApi,
    private val webSocketClient: WebSocketClient,
    private val messageMapper: MessageMapper,
    private val conversationMapper: ConversationMapper,
    private val participantMapper: ConversationParticipantMapper,
    private val tokenProvider: TokenProvider
) : ChatRepository {

    companion object {
        private const val TAG = "ChatRepository"
    }

    // ─── Conversations ────────────────────────────────────────────

    override fun getConversations(): Flow<List<Conversation>> {
        return messageDao.getConversationsForUser(getUserId())
            .map { entities -> conversationMapper.toDomainList(entities) }
    }

    override suspend fun getConversation(conversationId: String): Result<Conversation> {
        return try {
            val entity = messageDao.getConversation(conversationId)
            if (entity != null) {
                Result.Success(conversationMapper.toDomain(entity))
            } else {
                Result.Error(IllegalStateException("Conversation not found: $conversationId"))
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Error fetching conversation $conversationId", e)
            Result.Error(e)
        }
    }

    override suspend fun createConversation(
        participantIds: List<String>,
        name: String?,
        type: String
    ): Result<Conversation> {
        return try {
            // Validate participants
            if (participantIds.isEmpty()) {
                return Result.Error(IllegalArgumentException("At least one participant required"))
            }

            // Check if conversation already exists (for direct messages)
            if (type == "direct" && participantIds.size == 1) {
                // Check for existing conversation
                val userConversations = messageDao.getConversationsForUser(getUserId()).first()
                val otherParticipantId = participantIds.first()

                for (conv in userConversations) {
                    if (conv.type == "direct") {
                        val participants = messageDao.getConversationParticipants(conv.id)
                        if (participants.any { it.userId == otherParticipantId }) {
                            return Result.Success(conversationMapper.toDomain(conv))
                        }
                    }
                }
            }

            val conversation = Conversation(
                id = java.util.UUID.randomUUID().toString(),
                type = type,
                name = name,
                isArchived = false,
                isMuted = false,
                mutedUntil = null,
                createdAt = nowUtc(),
                updatedAt = nowUtc()
            )

            val conversationEntity = conversationMapper.toEntity(conversation)
            messageDao.insertConversation(conversationEntity)

            // Add participants
            val allParticipantIds = (participantIds + getUserId()).distinct()
            allParticipantIds.forEach { userId ->
                val participant = ConversationParticipant(
                    id = java.util.UUID.randomUUID().toString(),
                    conversationId = conversation.id,
                    userId = userId,
                    role = if (userId == getUserId()) "admin" else "member",
                    joinedAt = nowUtc(),
                    leftAt = null,
                    lastReadMessageId = null,
                    createdAt = nowUtc(),
                    updatedAt = nowUtc()
                )
                messageDao.insertConversationParticipant(
                    participantMapper.toEntity(participant)
                )
            }

            enqueueSync(SyncQueueEntity(
                entityType = "conversation",
                operation = "create",
                entityId = conversation.id,
                payloadJson = conversationMapper.toCreateRequest(
                    conversation, participantIds
                ).toString()
            ))

            Logger.d(TAG, "Conversation created locally: ${conversation.id}")
            Result.Success(conversation)
        } catch (e: Exception) {
            Logger.e(TAG, "Error creating conversation", e)
            Result.Error(e)
        }
    }

    override suspend fun updateConversation(conversation: Conversation): Result<Conversation> {
        return try {
            val existing = messageDao.getConversation(conversation.id)
            if (existing == null) {
                return Result.Error(IllegalStateException("Conversation not found: ${conversation.id}"))
            }

            val entity = conversationMapper.toEntity(conversation).copy(updatedAt = nowUtc())
            messageDao.updateConversation(entity)

            enqueueSync(SyncQueueEntity(
                entityType = "conversation",
                operation = "update",
                entityId = conversation.id,
                payloadJson = conversationMapper.toUpdateRequest(conversation).toString()
            ))

            Logger.d(TAG, "Conversation updated locally: ${conversation.id}")
            Result.Success(conversation)
        } catch (e: Exception) {
            Logger.e(TAG, "Error updating conversation ${conversation.id}", e)
            Result.Error(e)
        }
    }

    override suspend fun archiveConversation(conversationId: String): Result<Unit> {
        return try {
            val conversation = messageDao.getConversation(conversationId)
            if (conversation == null) {
                return Result.Error(IllegalStateException("Conversation not found: $conversationId"))
            }

            val updated = conversation.copy(
                isArchived = true,
                updatedAt = nowUtc()
            )
            messageDao.updateConversation(updated)

            enqueueSync(SyncQueueEntity(
                entityType = "conversation",
                operation = "update",
                entityId = conversationId,
                payloadJson = """{"isArchived":true}"""
            ))

            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error archiving conversation $conversationId", e)
            Result.Error(e)
        }
    }

    override suspend fun muteConversation(conversationId: String, until: String?): Result<Unit> {
        return try {
            val conversation = messageDao.getConversation(conversationId)
            if (conversation == null) {
                return Result.Error(IllegalStateException("Conversation not found: $conversationId"))
            }

            val updated = conversation.copy(
                isMuted = true,
                mutedUntil = until,
                updatedAt = nowUtc()
            )
            messageDao.updateConversation(updated)

            enqueueSync(SyncQueueEntity(
                entityType = "conversation",
                operation = "update",
                entityId = conversationId,
                payloadJson = """{"isMuted":true,"mutedUntil":"$until"}"""
            ))

            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error muting conversation $conversationId", e)
            Result.Error(e)
        }
    }

    override suspend fun unmuteConversation(conversationId: String): Result<Unit> {
        return try {
            val conversation = messageDao.getConversation(conversationId)
            if (conversation == null) {
                return Result.Error(IllegalStateException("Conversation not found: $conversationId"))
            }

            val updated = conversation.copy(
                isMuted = false,
                mutedUntil = null,
                updatedAt = nowUtc()
            )
            messageDao.updateConversation(updated)

            enqueueSync(SyncQueueEntity(
                entityType = "conversation",
                operation = "update",
                entityId = conversationId,
                payloadJson = """{"isMuted":false,"mutedUntil":null}"""
            ))

            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error unmuting conversation $conversationId", e)
            Result.Error(e)
        }
    }

    // ─── Messages ──────────────────────────────────────────────────

    override fun getMessages(conversationId: String): Flow<List<Message>> {
        return messageDao.getMessagesForConversationFlow(conversationId)
            .map { entities ->
                messageMapper.toDomainList(entities)
                    .filter { !it.isDeleted }
            }
    }

    override suspend fun getUnreadCount(): Flow<Int> {
        return messageDao.getTotalUnreadCountForUser(getUserId())
    }

    override suspend fun getUnreadCountForConversation(conversationId: String): Flow<Int> {
        return messageDao.getUnreadCountForConversation(conversationId, getUserId())
    }

    override suspend fun sendMessage(message: Message): Result<Message> {
        return try {
            validateMessage(message)

            // Save locally first
            val entity = messageMapper.toEntity(message).copy(
                sentAt = nowUtc(),
                isRead = false,
                isDelivered = false,
                updatedAt = nowUtc()
            )
            messageDao.insertMessage(entity)

            // Update conversation last message
            updateConversationLastMessage(message.conversationId, message)

            // Send via WebSocket if connected
            val sentViaWebSocket = sendViaWebSocket(message)

            // Enqueue sync if not sent via WebSocket
            if (!sentViaWebSocket) {
                enqueueSync(SyncQueueEntity(
                    entityType = "message",
                    operation = "create",
                    entityId = message.id,
                    payloadJson = messageMapper.toSendRequest(message).toString()
                ))
            }

            Logger.d(TAG, "Message sent locally: ${message.id}")
            Result.Success(message)
        } catch (e: Exception) {
            Logger.e(TAG, "Error sending message", e)
            Result.Error(e)
        }
    }

    override suspend fun sendMessageViaWebSocket(message: Message): Result<Message> {
        return try {
            validateMessage(message)

            if (!webSocketClient.isConnected()) {
                return Result.Error(IllegalStateException("WebSocket not connected"))
            }

            val sent = sendViaWebSocket(message)
            if (sent) {
                // Save locally after successful WebSocket send
                val entity = messageMapper.toEntity(message).copy(
                    sentAt = nowUtc(),
                    isRead = false,
                    isDelivered = true,
                    deliveredAt = nowUtc(),
                    updatedAt = nowUtc()
                )
                messageDao.insertMessage(entity)
                updateConversationLastMessage(message.conversationId, message)

                Logger.d(TAG, "Message sent via WebSocket: ${message.id}")
                Result.Success(message)
            } else {
                Result.Error(IllegalStateException("Failed to send via WebSocket"))
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Error sending message via WebSocket", e)
            Result.Error(e)
        }
    }

    override suspend fun markMessageRead(messageId: String): Result<Unit> {
        return try {
            messageDao.markMessagesRead(listOf(messageId))

            // Send read receipt via WebSocket
            sendReadReceipt(messageId)

            enqueueSync(SyncQueueEntity(
                entityType = "message",
                operation = "update",
                entityId = messageId,
                payloadJson = """{"isRead":true,"readAt":"${nowUtc()}"}"""
            ))

            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error marking message $messageId read", e)
            Result.Error(e)
        }
    }

    override suspend fun markConversationRead(conversationId: String): Result<Unit> {
        return try {
            messageDao.markConversationRead(conversationId, getUserId())

            enqueueSync(SyncQueueEntity(
                entityType = "conversation",
                operation = "update",
                entityId = conversationId,
                payloadJson = """{"lastReadMessageId":"read"}"""
            ))

            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error marking conversation $conversationId read", e)
            Result.Error(e)
        }
    }

    override suspend fun deleteMessage(messageId: String): Result<Unit> {
        return try {
            messageDao.deleteMessage(messageId)

            enqueueSync(SyncQueueEntity(
                entityType = "message",
                operation = "delete",
                entityId = messageId,
                payloadJson = null
            ))

            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error deleting message $messageId", e)
            Result.Error(e)
        }
    }

    // ─── Participants ─────────────────────────────────────────────

    override suspend fun getParticipants(conversationId: String): Result<List<ConversationParticipant>> {
        return try {
            val entities = messageDao.getConversationParticipants(conversationId)
            Result.Success(participantMapper.toDomainList(entities))
        } catch (e: Exception) {
            Logger.e(TAG, "Error fetching participants for conversation $conversationId", e)
            Result.Error(e)
        }
    }

    override suspend fun addParticipants(
        conversationId: String,
        userIds: List<String>
    ): Result<Unit> {
        return try {
            userIds.forEach { userId ->
                val participant = ConversationParticipant(
                    id = java.util.UUID.randomUUID().toString(),
                    conversationId = conversationId,
                    userId = userId,
                    role = "member",
                    joinedAt = nowUtc(),
                    leftAt = null,
                    lastReadMessageId = null,
                    createdAt = nowUtc(),
                    updatedAt = nowUtc()
                )
                messageDao.insertConversationParticipant(
                    participantMapper.toEntity(participant)
                )
            }

            enqueueSync(SyncQueueEntity(
                entityType = "conversation",
                operation = "update",
                entityId = conversationId,
                payloadJson = """{"addParticipants":${userIds}}"""
            ))

            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error adding participants to conversation $conversationId", e)
            Result.Error(e)
        }
    }

    override suspend fun removeParticipant(conversationId: String, userId: String): Result<Unit> {
        return try {
            messageDao.removeConversationParticipant(conversationId, userId)

            enqueueSync(SyncQueueEntity(
                entityType = "conversation",
                operation = "update",
                entityId = conversationId,
                payloadJson = """{"removeParticipant":"$userId"}"""
            ))

            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error removing participant $userId from conversation $conversationId", e)
            Result.Error(e)
        }
    }

    // ─── Typing Indicators ────────────────────────────────────────

    override suspend fun sendTypingIndicator(conversationId: String, isTyping: Boolean): Result<Unit> {
        return try {
            if (!webSocketClient.isConnected()) {
                return Result.Error(IllegalStateException("WebSocket not connected"))
            }

            val typingMessage = WebSocketMessage(
                type = "typing",
                data = """
                    {"conversationId":"$conversationId","userId":"${getUserId()}","isTyping":$isTyping}
                """.trimIndent()
            )

            val sent = webSocketClient.sendMessage(typingMessage)
            if (sent) {
                Result.Success(Unit)
            } else {
                Result.Error(IllegalStateException("Failed to send typing indicator"))
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Error sending typing indicator", e)
            Result.Error(e)
        }
    }

    // ─── Sync ──────────────────────────────────────────────────────

    override suspend fun syncWithServer(): Result<Unit> {
        return try {
            val token = tokenProvider.getAccessToken()
            if (token == null) {
                return Result.Error(IllegalStateException("Not authenticated"))
            }

            // Pull conversations from server
            val conversationsResponse = chatApi.getConversations()
            conversationsResponse.items.forEach { dto ->
                val entity = conversationMapper.toEntity(dto)
                messageDao.insertConversation(entity)
            }

            // Sync unsynced messages
            // (Implementation would push pending messages to server)

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

    private suspend fun sendViaWebSocket(message: Message): Boolean {
        if (!webSocketClient.isConnected()) {
            return false
        }

        return try {
            val wsMessage = WebSocketMessage(
                type = "message",
                data = """
                    {"messageId":"${message.id}","conversationId":"${message.conversationId}","content":"${message.content}"}
                """.trimIndent()
            )
            webSocketClient.sendMessage(wsMessage)
        } catch (e: Exception) {
            Logger.e(TAG, "Error sending message via WebSocket", e)
            false
        }
    }

    private suspend fun sendReadReceipt(messageId: String) {
        if (!webSocketClient.isConnected()) {
            return
        }

        try {
            val receiptMessage = WebSocketMessage(
                type = "read",
                data = """
                    {"messageId":"$messageId","userId":"${getUserId()}","readAt":"${nowUtc()}"}
                """.trimIndent()
            )
            webSocketClient.sendMessage(receiptMessage)
        } catch (e: Exception) {
            Logger.e(TAG, "Error sending read receipt", e)
        }
    }

    private suspend fun updateConversationLastMessage(
        conversationId: String,
        message: Message
    ) {
        val conversation = messageDao.getConversation(conversationId)
        if (conversation != null) {
            val updated = conversation.copy(
                lastMessageId = message.id,
                lastMessageContent = message.content.take(100),
                lastMessageSentAt = message.sentAt,
                updatedAt = nowUtc()
            )
            messageDao.updateConversation(updated)
        }
    }

    private fun validateMessage(message: Message) {
        require(message.content.isNotBlank()) { "Message content cannot be empty" }
        require(message.contentType in listOf("text", "image", "file", "audio", "video", "location")) {
            "Invalid content type"
        }
        if (message.contentType == "text") {
            require(message.content.length <= 5000) {
                "Text message must be <= 5000 characters"
            }
        }
    }
}