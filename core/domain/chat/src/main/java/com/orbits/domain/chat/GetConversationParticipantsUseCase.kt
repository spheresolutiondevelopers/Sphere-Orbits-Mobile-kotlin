package com.orbits.domain.chat

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to get participants for a conversation.
 */
class GetConversationParticipantsUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {

    /**
     * Execute the use case.
     * @param conversationId The ID of the conversation
     * @return Result containing the list of participants, or error
     */
    suspend operator fun invoke(conversationId: String): Result<List<ConversationParticipant>> {
        if (conversationId.isBlank()) {
            return Result.Error(IllegalArgumentException("Conversation ID cannot be empty"))
        }
        return chatRepository.getParticipants(conversationId)
    }
}
