package com.orbits.domain.chat

/**
 * Message content types.
 */
enum class MessageType(val displayName: String, val icon: String) {
    TEXT("Text", "💬"),
    IMAGE("Image", "🖼️"),
    VIDEO("Video", "🎬"),
    AUDIO("Audio", "🎵"),
    FILE("File", "📎"),
    LOCATION("Location", "📍"),
    CONTACT("Contact", "👤"),
    STICKER("Sticker", "😊"),
    GIF("GIF", "🎯"),
    POLL("Poll", "📊");

    companion object {
        fun fromString(value: String): MessageType? {
            return values().find { it.name.equals(value, ignoreCase = true) }
        }

        fun fromDisplayName(value: String): MessageType? {
            return values().find { it.displayName.equals(value, ignoreCase = true) }
        }

        fun fromFileExtension(extension: String): MessageType? {
            return when (extension.lowercase()) {
                "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg" -> IMAGE
                "mp4", "mov", "avi", "mkv", "webm", "m4v" -> VIDEO
                "mp3", "wav", "aac", "flac", "ogg", "m4a" -> AUDIO
                else -> FILE
            }
        }
    }
}
