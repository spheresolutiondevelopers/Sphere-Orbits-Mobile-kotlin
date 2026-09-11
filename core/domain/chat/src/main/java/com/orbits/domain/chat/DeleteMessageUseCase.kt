package com.orbits.domain.chat

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to delete a message.
 */
class DeleteMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {

    /**
     * Execute the use case.
     * @param messageId The ID of the message to delete
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(messageId: String): Result<Unit> {
        if (messageId.isBlank()) {
            return Result.Error(IllegalArgumentException("Message ID cannot be empty"))
        }
        return chatRepository.deleteMessage(messageId)
    }
}
