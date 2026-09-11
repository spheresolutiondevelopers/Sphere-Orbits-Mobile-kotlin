package com.orbits.domain.chat

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to synchronize chat data with the server.
 */
class SyncChatUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {

    /**
     * Execute the use case.
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(): Result<Unit> {
        return chatRepository.syncWithServer()
    }
}
