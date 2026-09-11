package com.orbits.domain.notes

/**
 * Pure domain model for a note.
 * Contains NO Android dependencies — safe for KMP.
 *
 * Notes are rich-text documents that can be linked to tasks, events,
 * appointments, or meetings. They support tags, pinning, archiving,
 * and soft delete.
 */
data class Note(
    val id: String,
    val userId: String,
    val title: String? = null,
    val content: String,
    val contentFormat: String = "markdown", // markdown, plain, html
    val tags: List<String> = emptyList(),
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val color: String? = null, // Hex color for note background
    val taskId: String? = null,
    val eventId: String? = null,
    val appointmentId: String? = null,
    val meetingId: String? = null,
    val reminderAt: String? = null,
    val isDeleted: Boolean = false,
    val deletedAt: String? = null,
    val createdAt: String,
    val updatedAt: String
) {

    /**
     * Check if the note has a title.
     */
    fun hasTitle(): Boolean = !title.isNullOrBlank()

    /**
     * Get the note's display title.
     * Returns the title if present, otherwise a preview of the content.
     */
    fun getDisplayTitle(): String {
        if (!title.isNullOrBlank()) return title
        val preview = content.take(50).replace("\n", " ")
        return if (preview.length < 50) preview else "$preview..."
    }

    /**
     * Get the content preview (truncated for display).
     */
    fun getContentPreview(maxLength: Int = 150): String {
        val plainText = content.replace(Regex("<[^>]*>"), "").replace("\n", " ")
        return if (plainText.length > maxLength) {
            plainText.substring(0, maxLength) + "..."
        } else {
            plainText
        }
    }


    /**
     * Check if the note is linked to a task.
     */
    fun isLinkedToTask(): Boolean = taskId != null

    /**
     * Check if the note is linked to an event.
     */
    fun isLinkedToEvent(): Boolean = eventId != null

    /**
     * Check if the note is linked to an appointment.
     */
    fun isLinkedToAppointment(): Boolean = appointmentId != null

    /**
     * Check if the note is linked to a meeting.
     */
    fun isLinkedToMeeting(): Boolean = meetingId != null

    /**
     * Check if the note has a reminder set.
     */
    fun hasReminder(): Boolean = reminderAt != null

    /**
     * Check if the note has a color.
     */
    fun hasColor(): Boolean = color != null

    /**
     * Check if the note is empty (no content).
     */
    fun isEmpty(): Boolean = content.isBlank()

    /**
     * Get the note's status label.
     */
    fun getStatusLabel(): String {
        return when {
            isDeleted -> "Deleted"
            isArchived -> "Archived"
            isPinned -> "Pinned"
            else -> "Active"
        }
    }

    /**
     * Get the note's linked entity type (if any).
     */
    fun getLinkedEntityType(): String? {
        return when {
            taskId != null -> "task"
            eventId != null -> "event"
            appointmentId != null -> "appointment"
            meetingId != null -> "meeting"
            else -> null
        }
    }

    /**
     * Get the note's linked entity ID (if any).
     */
    fun getLinkedEntityId(): String? {
        return taskId ?: eventId ?: appointmentId ?: meetingId
    }

    /**
     * Get the formatted creation date.
     */
    fun getFormattedCreatedAt(): String {
        return try {
            val instant = java.time.Instant.parse(createdAt)
            val date = instant.atZone(java.time.ZoneId.systemDefault())
            java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")
                .format(date)
        } catch (e: Exception) {
            createdAt
        }
    }

    /**
     * Get the formatted updated date.
     */
    fun getFormattedUpdatedAt(): String {
        return try {
            val instant = java.time.Instant.parse(updatedAt)
            val date = instant.atZone(java.time.ZoneId.systemDefault())
            java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' h:mm a")
                .format(date)
        } catch (e: Exception) {
            updatedAt
        }
    }

    /**
     * Get the formatted reminder date.
     */
    fun getFormattedReminderAt(): String? {
        if (reminderAt == null) return null
        return try {
            val instant = java.time.Instant.parse(reminderAt)
            val date = instant.atZone(java.time.ZoneId.systemDefault())
            java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy h:mm a")
                .format(date)
        } catch (e: Exception) {
            reminderAt
        }
    }

    /**
     * Check if the note has a tag.
     */
    fun hasTag(tag: String): Boolean = tags.contains(tag)

    /**
     * Get the note's word count.
     */
    fun getWordCount(): Int {
        return content.replace(Regex("<[^>]*>"), "")
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .size
    }

    /**
     * Get the note's character count.
     */
    fun getCharacterCount(): Int {
        return content.replace(Regex("<[^>]*>"), "").length
    }

    /**
     * Get the note's estimated read time in minutes.
     */
    fun getEstimatedReadTime(): Int {
        val words = getWordCount()
        return when {
            words < 200 -> 1
            words < 500 -> 2
            words < 1000 -> 3
            words < 1500 -> 4
            else -> (words / 300) + 1
        }
    }

    /**
     * Check if the note has been modified.
     */
    fun isModified(): Boolean {
        return createdAt != updatedAt
    }

    /**
     * Get the note's tags as a comma-separated string.
     */
    fun getTagsAsString(): String {
        return tags.joinToString(", ")
    }

    /**
     * Get the note's color as a readable name.
     */
    fun getColorName(): String? {
        if (color == null) return null
        return when (color.uppercase()) {
            "#FF6B6B" -> "Red"
            "#FF9F43" -> "Orange"
            "#FECA57" -> "Yellow"
            "#48DBFB" -> "Cyan"
            "#0ABDE3" -> "Blue"
            "#10AC84" -> "Green"
            "#A29BFE" -> "Purple"
            "#FD79A8" -> "Pink"
            "#636E72" -> "Gray"
            else -> "Custom"
        }
    }
}
