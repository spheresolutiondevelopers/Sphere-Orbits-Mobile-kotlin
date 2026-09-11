package com.orbits.domain.chat

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to mute or unmute a conversation.
 */
class MuteConversationUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {

    /**
     * Mute a conversation.
     * @param conversationId The ID of the conversation
     * @param until Optional mute expiry time (ISO 8601). If null, muted indefinitely.
     * @return Result indicating success or failure
     */
    suspend fun mute(conversationId: String, until: String? = null): Result<Unit> {
        if (conversationId.isBlank()) {
            return Result.Error(IllegalArgumentException("Conversation ID cannot be empty"))
        }
        return chatRepository.muteConversation(conversationId, until)
    }

    /**
     * Mute a conversation for a specific duration.
     * @param conversationId The ID of the conversation
     * @param durationMinutes Duration in minutes (15, 30, 60, 120, 480, 1440)
     * @return Result indicating success or failure
     */
    suspend fun muteFor(conversationId: String, durationMinutes: Int): Result<Unit> {
        if (conversationId.isBlank()) {
            return Result.Error(IllegalArgumentException("Conversation ID cannot be empty"))
        }
        if (durationMinutes !in listOf(15, 30, 60, 120, 480, 1440)) {
            return Result.Error(
                IllegalArgumentException(
                    "Invalid duration. Must be: 15, 30, 60, 120, 480, or 1440 minutes"
                )
            )
        }

        val until = java.time.Instant.now()
            .plusSeconds(durationMinutes * 60L)
            .toString()

        return chatRepository.muteConversation(conversationId, until)
    }

    /**
     * Mute a conversation until tomorrow.
     */
    suspend fun muteUntilTomorrow(conversationId: String): Result<Unit> {
        val tomorrow = java.time.LocalDate.now()
            .plusDays(1)
            .atStartOfDay()
            .atZone(java.time.ZoneId.systemDefault())
            .toInstant()
            .toString()

        return mute(conversationId, tomorrow)
    }

    /**
     * Unmute a conversation.
     */
    suspend fun unmute(conversationId: String): Result<Unit> {
        if (conversationId.isBlank()) {
            return Result.Error(IllegalArgumentException("Conversation ID cannot be empty"))
        }
        return chatRepository.unmuteConversation(conversationId)
    }

    /**
     * Execute the use case (mutes by default).
     */
    suspend operator fun invoke(conversationId: String, until: String? = null): Result<Unit> {
        return mute(conversationId, until)
    }
}
