package com.orbits.domain.chat

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get the total unread count.
 */
class GetUnreadCountUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {

    /**
     * Execute the use case.
     * @return Flow emitting the total unread count
     */
    suspend operator fun invoke(): Flow<Int> {
        return chatRepository.getUnreadCount()
    }

    /**
     * Execute the use case for a specific conversation.
     * @param conversationId The ID of the conversation
     * @return Flow emitting the unread count for the conversation
     */
    suspend fun forConversation(conversationId: String): Flow<Int> {
        require(conversationId.isNotBlank()) { "Conversation ID cannot be empty" }
        return chatRepository.getUnreadCountForConversation(conversationId)
    }
}
