package com.orbits.domain.chat

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to add participants to a conversation.
 */
class AddParticipantsUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {

    /**
     * Execute the use case.
     * @param conversationId The ID of the conversation
     * @param userIds The user IDs to add
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(conversationId: String, userIds: List<String>): Result<Unit> {
        if (conversationId.isBlank()) {
            return Result.Error(IllegalArgumentException("Conversation ID cannot be empty"))
        }
        if (userIds.isEmpty()) {
            return Result.Error(IllegalArgumentException("At least one user ID required"))
        }
        return chatRepository.addParticipants(conversationId, userIds)
    }

    /**
     * Add a single participant.
     */
    suspend fun addOne(conversationId: String, userId: String): Result<Unit> {
        return invoke(conversationId, listOf(userId))
    }
}
