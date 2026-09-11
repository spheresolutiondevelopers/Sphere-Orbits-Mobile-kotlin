package com.orbits.domain.chat

/**
 * Chat validation rules.
 * Mirrors server-side validation exactly.
 */
object ChatValidation {

    fun validateMessage(
        content: String,
        contentType: String = "text",
        mediaUrl: String? = null,
        mediaSize: Long? = null,
        fileName: String? = null,
        replyToMessageId: String? = null
    ): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        // Content validation
        if (content.isBlank()) {
            errors.add(ValidationError("content", "Message content cannot be empty"))
        }

        // Content type validation
        if (contentType !in listOf("text", "image", "file", "audio", "video", "location")) {
            errors.add(ValidationError("contentType", "Invalid content type"))
        }

        // Content length validation
        when (contentType) {
            "text" -> {
                if (content.length > 5000) {
                    errors.add(ValidationError("content", "Text message must be 5000 characters or less"))
                }
            }
            "image", "video" -> {
                if (mediaUrl == null || mediaUrl.isBlank()) {
                    errors.add(ValidationError("mediaUrl", "Media URL required for image/video messages"))
                }
            }
            "file" -> {
                if (fileName == null || fileName.isBlank()) {
                    errors.add(ValidationError("fileName", "File name required for file messages"))
                }
                if (mediaSize != null && mediaSize > 100 * 1024 * 1024) {
                    errors.add(ValidationError("mediaSize", "File size cannot exceed 100MB"))
                }
            }
        }

        // Reply validation
        if (replyToMessageId != null && replyToMessageId.isBlank()) {
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

    fun validateParticipantRole(role: String): Boolean {
        return role in listOf("admin", "member", "viewer")
    }

    fun validateConversationType(type: String): Boolean {
        return type in listOf("direct", "group")
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
