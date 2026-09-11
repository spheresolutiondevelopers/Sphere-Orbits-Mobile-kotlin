package com.orbits.domain.chat

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to get a single conversation by ID.
 */
class GetConversationUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {

    /**
     * Execute the use case.
     * @param conversationId The ID of the conversation to retrieve
     * @return Result containing the conversation, or error
     */
    suspend operator fun invoke(conversationId: String): Result<Conversation> {
        if (conversationId.isBlank()) {
            return Result.Error(IllegalArgumentException("Conversation ID cannot be empty"))
        }
        return chatRepository.getConversation(conversationId)
    }
}
