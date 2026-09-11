package com.orbits.domain.chat

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to create a new conversation.
 */
class CreateConversationUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {

    /**
     * Execute the use case for a direct message.
     * @param participantId The user ID of the other participant
     * @return Result containing the created conversation, or error
     */
    suspend fun direct(participantId: String): Result<Conversation> {
        if (participantId.isBlank()) {
            return Result.Error(IllegalArgumentException("Participant ID cannot be empty"))
        }
        return chatRepository.createConversation(
            participantIds = listOf(participantId),
            type = "direct"
        )
    }

    /**
     * Execute the use case for a group chat.
     * @param participantIds The user IDs of the participants
     * @param name The group name
     * @return Result containing the created conversation, or error
     */
    suspend fun group(participantIds: List<String>, name: String): Result<Conversation> {
        if (participantIds.isEmpty()) {
            return Result.Error(IllegalArgumentException("At least one participant required"))
        }
        if (name.isBlank()) {
            return Result.Error(IllegalArgumentException("Group name cannot be empty"))
        }
        return chatRepository.createConversation(
            participantIds = participantIds,
            name = name,
            type = "group"
        )
    }

    /**
     * Execute the use case with custom parameters.
     */
    suspend operator fun invoke(
        participantIds: List<String>,
        name: String? = null,
        type: String = "direct"
    ): Result<Conversation> {
        if (participantIds.isEmpty()) {
            return Result.Error(IllegalArgumentException("At least one participant required"))
        }
        if (type == "group" && (name == null || name.isBlank())) {
            return Result.Error(IllegalArgumentException("Group name cannot be empty"))
        }
        return chatRepository.createConversation(participantIds, name, type)
    }
}
