package com.orbits.domain.chat

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get all conversations for the current user.
 */
class GetConversationsUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {

    /**
     * Execute the use case.
     * @return Flow emitting the list of conversations
     */
    operator fun invoke(): Flow<List<Conversation>> {
        return chatRepository.getConversations()
    }
}
