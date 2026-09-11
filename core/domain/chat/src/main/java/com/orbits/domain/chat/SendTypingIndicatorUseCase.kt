package com.orbits.domain.chat

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to send a typing indicator.
 */
class SendTypingIndicatorUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {

    /**
     * Execute the use case.
     * @param conversationId The ID of the conversation
     * @param isTyping Whether the user is typing
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(conversationId: String, isTyping: Boolean): Result<Unit> {
        if (conversationId.isBlank()) {
            return Result.Error(IllegalArgumentException("Conversation ID cannot be empty"))
        }
        return chatRepository.sendTypingIndicator(conversationId, isTyping)
    }

    /**
     * Convenience method to start typing.
     */
    suspend fun startTyping(conversationId: String): Result<Unit> {
        return invoke(conversationId, true)
    }

    /**
     * Convenience method to stop typing.
     */
    suspend fun stopTyping(conversationId: String): Result<Unit> {
        return invoke(conversationId, false)
    }
}
