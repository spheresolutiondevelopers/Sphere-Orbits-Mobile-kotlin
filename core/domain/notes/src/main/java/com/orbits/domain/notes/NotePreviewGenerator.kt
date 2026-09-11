package com.orbits.domain.notes

/**
 * Generates preview text for notes.
 */
object NotePreviewGenerator {

    /**
     * Generate a preview for a note.
     * @param note The note
     * @param maxLength Maximum length of the preview
     * @return Preview text
     */
    fun generatePreview(note: Note, maxLength: Int = 100): String {
        // If the note has a title, use that as the preview
        if (!note.title.isNullOrBlank()) {
            return note.title
        }

        // Otherwise, use the content preview
        return MarkdownHelper.getPlainTextPreview(note.content, maxLength)
    }

    /**
     * Generate a short preview (for list views).
     */
    fun generateShortPreview(note: Note, maxLength: Int = 60): String {
        return generatePreview(note, maxLength)
    }

    /**
     * Generate a medium preview (for card views).
     */
    fun generateMediumPreview(note: Note, maxLength: Int = 120): String {
        return generatePreview(note, maxLength)
    }

    /**
     * Generate a full preview (for detail views).
     */
    fun generateFullPreview(note: Note, maxLength: Int = 300): String {
        return generatePreview(note, maxLength)
    }

    /**
     * Generate a preview with the note's tags appended.
     */
    fun generatePreviewWithTags(note: Note, maxLength: Int = 100): String {
        val preview = generatePreview(note, maxLength)
        if (note.tags.isEmpty()) {
            return preview
        }
        return "$preview [${note.tags.joinToString(", ")}]"
    }

    /**
     * Generate a preview with the note's last updated time.
     */
    fun generatePreviewWithTimestamp(note: Note, maxLength: Int = 100): String {
        val preview = generatePreview(note, maxLength)
        val date = note.getFormattedUpdatedAt()
        return "$preview ($date)"
    }

    /**
     * Generate a preview for search results (highlighting matches).
     * @param note The note
     * @param query The search query
     * @param maxLength Maximum length of the preview
     * @return Preview text with the query highlighted
     */
    fun generateHighlightedPreview(
        note: Note,
        query: String,
        maxLength: Int = 150
    ): String {
        val text = MarkdownHelper.getPlainTextPreview(note.content, maxLength)
        val lowerText = text.lowercase()
        val lowerQuery = query.lowercase()

        val index = lowerText.indexOf(lowerQuery)
        if (index == -1) {
            return text
        }

        // Find the surrounding context
        val start = (index - 30).coerceAtLeast(0)
        val end = (index + query.length + 30).coerceAtMost(text.length)

        val prefix = if (start > 0) "..." else ""
        val suffix = if (end < text.length) "..." else ""

        val context = text.substring(start, end)
        return "$prefix$context$suffix"
    }
}
