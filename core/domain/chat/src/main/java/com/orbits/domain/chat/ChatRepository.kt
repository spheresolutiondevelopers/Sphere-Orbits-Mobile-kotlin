package com.orbits.domain.chat

import com.orbits.core.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Chat repository interface.
 * Defines all chat operations.
 * Implemented by :core:data:chat.
 */
interface ChatRepository {

    // ─── Conversations ────────────────────────────────────────────

    /**
     * Get all conversations for the current user.
     */
    fun getConversations(): Flow<List<Conversation>>

    /**
     * Get a single conversation by ID.
     */
    suspend fun getConversation(conversationId: String): Result<Conversation>

    /**
     * Create a new conversation.
     * @param participantIds List of user IDs to add to the conversation
     * @param name Optional conversation name (for groups)
     * @param type Conversation type (direct or group)
     */
    suspend fun createConversation(
        participantIds: List<String>,
        name: String? = null,
        type: String = "direct"
    ): Result<Conversation>

    /**
     * Update an existing conversation.
     */
    suspend fun updateConversation(conversation: Conversation): Result<Conversation>

    /**
     * Archive a conversation.
     */
    suspend fun archiveConversation(conversationId: String): Result<Unit>

    /**
     * Mute a conversation.
     * @param conversationId The ID of the conversation
     * @param until Optional mute expiry time (ISO 8601)
     */
    suspend fun muteConversation(conversationId: String, until: String? = null): Result<Unit>

    /**
     * Unmute a conversation.
     */
    suspend fun unmuteConversation(conversationId: String): Result<Unit>

    // ─── Messages ──────────────────────────────────────────────────

    /**
     * Get messages for a conversation.
     */
    fun getMessages(conversationId: String): Flow<List<Message>>

    /**
     * Get the total unread count for all conversations.
     */
    suspend fun getUnreadCount(): Flow<Int>

    /**
     * Get the unread count for a specific conversation.
     */
    suspend fun getUnreadCountForConversation(conversationId: String): Flow<Int>

    /**
     * Send a new message.
     */
    suspend fun sendMessage(message: Message): Result<Message>

    /**
     * Send a message via WebSocket (real-time).
     */
    suspend fun sendMessageViaWebSocket(message: Message): Result<Message>

    /**
     * Mark a message as read.
     */
    suspend fun markMessageRead(messageId: String): Result<Unit>

    /**
     * Mark all messages in a conversation as read.
     */
    suspend fun markConversationRead(conversationId: String): Result<Unit>

    /**
     * Delete a message.
     */
    suspend fun deleteMessage(messageId: String): Result<Unit>

    // ─── Participants ─────────────────────────────────────────────

    /**
     * Get participants for a conversation.
     */
    suspend fun getParticipants(conversationId: String): Result<List<ConversationParticipant>>

    /**
     * Add participants to a conversation.
     */
    suspend fun addParticipants(conversationId: String, userIds: List<String>): Result<Unit>

    /**
     * Remove a participant from a conversation.
     */
    suspend fun removeParticipant(conversationId: String, userId: String): Result<Unit>

    // ─── Typing Indicators ────────────────────────────────────────

    /**
     * Send a typing indicator.
     */
    suspend fun sendTypingIndicator(conversationId: String, isTyping: Boolean): Result<Unit>

    // ─── Sync ──────────────────────────────────────────────────────

    /**
     * Synchronize chat data with the server.
     */
    suspend fun syncWithServer(): Result<Unit>
}
