package com.orbits.domain.chat

/**
 * Pure domain model for a chat message.
 * Contains NO Android dependencies — safe for KMP.
 */
data class Message(
    val id: String,
    val conversationId: String,
    val senderUserId: String,
    val content: String,
    val contentType: String = "text", // text, image, file, audio, video, location
    val mediaUrl: String? = null,
    val mediaThumbnailUrl: String? = null,
    val mediaWidth: Int? = null,
    val mediaHeight: Int? = null,
    val mediaSize: Long? = null,
    val fileName: String? = null,
    val fileExtension: String? = null,
    val replyToMessageId: String? = null,
    val sentAt: String,
    val isRead: Boolean = false,
    val readAt: String? = null,
    val isDelivered: Boolean = false,
    val deliveredAt: String? = null,
    val isDeleted: Boolean = false,
    val createdAt: String,
    val updatedAt: String
) {

    /**
     * Check if the message is from the current user.
     * This should be set by the UI layer based on the authenticated user.
     */
    fun isFromCurrentUser(currentUserId: String): Boolean {
        return senderUserId == currentUserId
    }

    /**
     * Check if the message is a text message.
     */
    fun isText(): Boolean = contentType == "text"

    /**
     * Check if the message contains media.
     */
    fun hasMedia(): Boolean = contentType in listOf("image", "video", "audio", "file")

    /**
     * Check if the message is an image.
     */
    fun isImage(): Boolean = contentType == "image"

    /**
     * Check if the message is a video.
     */
    fun isVideo(): Boolean = contentType == "video"

    /**
     * Check if the message is an audio message.
     */
    fun isAudio(): Boolean = contentType == "audio"

    /**
     * Check if the message is a file.
     */
    fun isFile(): Boolean = contentType == "file"

    /**
     * Check if the message is a location.
     */
    fun isLocation(): Boolean = contentType == "location"

    /**
     * Get the message's status label.
     */
    fun getStatusLabel(): String {
        return when {
            isRead -> "Read"
            isDelivered -> "Delivered"
            else -> "Sent"
        }
    }

    /**
     * Get the formatted sent time.
     */
    fun getFormattedTime(): String {
        return try {
            val instant = java.time.Instant.parse(sentAt)
            val time = instant.atZone(java.time.ZoneId.systemDefault())
            java.time.format.DateTimeFormatter.ofPattern("h:mm a")
                .format(time)
        } catch (e: Exception) {
            sentAt
        }
    }

    /**
     * Get the formatted sent date.
     */
    fun getFormattedDate(): String {
        return try {
            val instant = java.time.Instant.parse(sentAt)
            val date = instant.atZone(java.time.ZoneId.systemDefault())
            java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")
                .format(date)
        } catch (e: Exception) {
            sentAt
        }
    }

    /**
     * Get the formatted sent date and time.
     */
    fun getFormattedDateTime(): String {
        return try {
            val instant = java.time.Instant.parse(sentAt)
            val zoned = instant.atZone(java.time.ZoneId.systemDefault())
            java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy h:mm a")
                .format(zoned)
        } catch (e: Exception) {
            sentAt
        }
    }

    /**
     * Get the file size as a human-readable string.
     */
    fun getFormattedFileSize(): String? {
        if (mediaSize == null) return null
        return when {
            mediaSize < 1024 -> "${mediaSize} B"
            mediaSize < 1024 * 1024 -> "${mediaSize / 1024} KB"
            else -> "${mediaSize / (1024 * 1024)} MB"
        }
    }

    /**
     * Check if the message has a reply.
     */
    fun hasReply(): Boolean = replyToMessageId != null

    /**
     * Get the content preview (truncated for display).
     */
    fun getContentPreview(maxLength: Int = 50): String {
        return if (content.length > maxLength) {
            content.substring(0, maxLength) + "..."
        } else {
            content
        }
    }

    /**
     * Get the message type display name.
     */
    fun getContentTypeDisplayName(): String {
        return when (contentType) {
            "text" -> "Text"
            "image" -> "Image"
            "video" -> "Video"
            "audio" -> "Audio"
            "file" -> "File"
            "location" -> "Location"
            else -> contentType.replaceFirstChar { it.uppercase() }
        }
    }
}
