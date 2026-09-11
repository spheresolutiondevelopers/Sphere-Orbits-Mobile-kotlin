package com.orbits.domain.chat

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get messages for a conversation.
 */
class GetMessagesUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {

    /**
     * Execute the use case.
     * @param conversationId The ID of the conversation
     * @return Flow emitting the list of messages
     */
    operator fun invoke(conversationId: String): Flow<List<Message>> {
        require(conversationId.isNotBlank()) { "Conversation ID cannot be empty" }
        return chatRepository.getMessages(conversationId)
    }
}
