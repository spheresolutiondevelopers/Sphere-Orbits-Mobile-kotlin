package com.orbits.domain.chat

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to remove a participant from a conversation.
 */
class RemoveParticipantUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {

    /**
     * Execute the use case.
     * @param conversationId The ID of the conversation
     * @param userId The user ID to remove
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(conversationId: String, userId: String): Result<Unit> {
        if (conversationId.isBlank()) {
            return Result.Error(IllegalArgumentException("Conversation ID cannot be empty"))
        }
        if (userId.isBlank()) {
            return Result.Error(IllegalArgumentException("User ID cannot be empty"))
        }
        return chatRepository.removeParticipant(conversationId, userId)
    }
}
