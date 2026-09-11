package com.orbits.domain.chat

import com.orbits.core.common.Result
import com.orbits.core.common.map
import javax.inject.Inject

/**
 * Use case to archive or unarchive a conversation.
 */
class ArchiveConversationUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {

    /**
     * Archive a conversation.
     * @param conversationId The ID of the conversation
     * @return Result indicating success or failure
     */
    suspend fun archive(conversationId: String): Result<Unit> {
        if (conversationId.isBlank()) {
            return Result.Error(IllegalArgumentException("Conversation ID cannot be empty"))
        }
        return chatRepository.archiveConversation(conversationId)
    }

    /**
     * Unarchive a conversation.
     * @param conversationId The ID of the conversation
     * @return Result indicating success or failure
     */
    suspend fun unarchive(conversationId: String): Result<Unit> {
        if (conversationId.isBlank()) {
            return Result.Error(IllegalArgumentException("Conversation ID cannot be empty"))
        }

        val result = chatRepository.getConversation(conversationId)
        return when (result) {
            is Result.Success -> {
                val updated = result.data.copy(
                    isArchived = false,
                    updatedAt = java.time.Instant.now().toString()
                )
                chatRepository.updateConversation(updated).map { }
            }
            is Result.Error -> Result.Error(result.exception)
            Result.Loading -> Result.Loading
        }
    }

    /**
     * Execute the use case (archives by default).
     */
    suspend operator fun invoke(conversationId: String): Result<Unit> {
        return archive(conversationId)
    }
}
