package com.orbits.domain.chat

import com.orbits.core.common.Result
import com.orbits.core.common.extensions.nowUtc
import javax.inject.Inject

/**
 * Use case to update an existing conversation.
 */
class UpdateConversationUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {

    /**
     * Execute the use case.
     * @param conversation The updated conversation
     * @return Result containing the updated conversation, or error
     */
    suspend operator fun invoke(conversation: Conversation): Result<Conversation> {
        // Validate
        val validationResult = ChatValidation.validateConversation(conversation)
        if (!validationResult.isValid) {
            return Result.Error(
                IllegalArgumentException(
                    validationResult.errors.joinToString { it.message }
                )
            )
        }
        return chatRepository.updateConversation(conversation)
    }

    /**
     * Update the conversation name.
     */
    suspend fun updateName(conversationId: String, name: String): Result<Conversation> {
        val result = chatRepository.getConversation(conversationId)
        return when (result) {
            is Result.Success -> {
                val updated = result.data.copy(
                    name = name,
                    updatedAt = nowUtc()
                )
                chatRepository.updateConversation(updated)
            }
            is Result.Error -> Result.Error(result.exception)
            Result.Loading -> Result.Loading
        }
    }

    /**
     * Update the conversation avatar.
     */
    suspend fun updateAvatar(conversationId: String, avatarUrl: String): Result<Conversation> {
        val result = chatRepository.getConversation(conversationId)
        return when (result) {
            is Result.Success -> {
                val updated = result.data.copy(
                    avatarUrl = avatarUrl,
                    updatedAt = nowUtc()
                )
                chatRepository.updateConversation(updated)
            }
            is Result.Error -> Result.Error(result.exception)
            Result.Loading -> Result.Loading
        }
    }
}
