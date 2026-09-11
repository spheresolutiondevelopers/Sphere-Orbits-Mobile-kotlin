package com.orbits.domain.chat

import com.orbits.core.common.Result
import com.orbits.core.common.extensions.nowUtc
import java.util.UUID
import javax.inject.Inject

/**
 * Use case to send a new message.
 */
class SendMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {

    /**
     * Execute the use case.
     * @param conversationId The ID of the conversation
     * @param content The message content
     * @param contentType The message content type (text by default)
     * @param mediaUrl Optional media URL
     * @param mediaThumbnailUrl Optional thumbnail URL
     * @param mediaWidth Optional media width
     * @param mediaHeight Optional media height
     * @param mediaSize Optional media size in bytes
     * @param fileName Optional file name
     * @param fileExtension Optional file extension
     * @param replyToMessageId Optional ID of the message being replied to
     * @param senderUserId The ID of the sender (if null, uses current user)
     * @return Result containing the sent message, or error
     */
    suspend operator fun invoke(
        conversationId: String,
        content: String,
        contentType: String = "text",
        mediaUrl: String? = null,
        mediaThumbnailUrl: String? = null,
        mediaWidth: Int? = null,
        mediaHeight: Int? = null,
        mediaSize: Long? = null,
        fileName: String? = null,
        fileExtension: String? = null,
        replyToMessageId: String? = null,
        senderUserId: String? = null
    ): Result<Message> {
        // Validate
        val validationResult = ChatValidation.validateMessage(
            content = content,
            contentType = contentType,
            mediaUrl = mediaUrl,
            mediaSize = mediaSize,
            fileName = fileName,
            replyToMessageId = replyToMessageId
        )

        if (!validationResult.isValid) {
            return Result.Error(
                IllegalArgumentException(
                    validationResult.errors.joinToString { it.message }
                )
            )
        }

        val now = nowUtc()
        val message = Message(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            senderUserId = senderUserId ?: getCurrentUserId(),
            content = content,
            contentType = contentType,
            mediaUrl = mediaUrl,
            mediaThumbnailUrl = mediaThumbnailUrl,
            mediaWidth = mediaWidth,
            mediaHeight = mediaHeight,
            mediaSize = mediaSize,
            fileName = fileName,
            fileExtension = fileExtension,
            replyToMessageId = replyToMessageId,
            sentAt = now,
            isRead = false,
            readAt = null,
            isDelivered = false,
            deliveredAt = null,
            isDeleted = false,
            createdAt = now,
            updatedAt = now
        )

        return chatRepository.sendMessage(message)
    }

    // This should come from auth state
    private fun getCurrentUserId(): String {
        return "test_user_id"
    }
}
