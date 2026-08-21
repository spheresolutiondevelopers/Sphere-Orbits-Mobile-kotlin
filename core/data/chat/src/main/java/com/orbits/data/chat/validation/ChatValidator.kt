package com.orbits.data.chat.validation

import com.orbits.domain.chat.Message
import com.orbits.domain.chat.Conversation

object ChatValidator {

    fun validateMessage(message: Message): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        if (message.content.isBlank()) {
            errors.add(ValidationError("content", "Message content cannot be empty"))
        }

        if (message.contentType !in listOf("text", "image", "file", "audio", "video", "location")) {
            errors.add(ValidationError("contentType", "Invalid content type"))
        }

        when (message.contentType) {
            "text" -> {
                if (message.content.length > 5000) {
                    errors.add(ValidationError("content", "Text message must be 5000 characters or less"))
                }
            }
            "image", "video" -> {
                if (message.mediaUrl == null || message.mediaUrl.isBlank()) {
                    errors.add(ValidationError("mediaUrl", "Media URL required for image/video messages"))
                }
            }
            "file" -> {
                if (message.fileName == null || message.fileName.isBlank()) {
                    errors.add(ValidationError("fileName", "File name required for file messages"))
                }
                if (message.mediaSize != null && message.mediaSize > 100 * 1024 * 1024) {
                    errors.add(ValidationError("mediaSize", "File size cannot exceed 100MB"))
                }
            }
        }

        if (message.replyToMessageId != null && message.replyToMessageId.isBlank()) {
            errors.add(ValidationError("replyToMessageId", "Invalid reply reference"))
        }

        return ValidationResult(errors.isEmpty(), errors)
    }

    fun validateConversation(conversation: Conversation): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        if (conversation.type !in listOf("direct", "group")) {
            errors.add(ValidationError("type", "Invalid conversation type"))
        }

        if (conversation.type == "group") {
            if (conversation.name == null || conversation.name.isBlank()) {
                errors.add(ValidationError("name", "Group conversation must have a name"))
            }
            if (conversation.name != null && conversation.name.length > 100) {
                errors.add(ValidationError("name", "Conversation name must be 100 characters or less"))
            }
        }

        if (conversation.avatarUrl != null && !conversation.avatarUrl.startsWith("http")) {
            errors.add(ValidationError("avatarUrl", "Avatar URL must be a valid URL"))
        }

        return ValidationResult(errors.isEmpty(), errors)
    }
}

data class ValidationError(
    val field: String,
    val message: String
)

data class ValidationResult(
    val isValid: Boolean,
    val errors: List<ValidationError>
)