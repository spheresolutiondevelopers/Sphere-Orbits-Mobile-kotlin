package com.orbits.domain.notes

/**
 * Helper for markdown operations.
 * Note: Actual markdown parsing is done in the data layer.
 * This is just a domain-level helper.
 */
object MarkdownHelper {

    /**
     * Check if a string appears to be markdown.
     */
    fun isMarkdown(text: String): Boolean {
        if (text.isBlank()) return false

        val markdownPatterns = listOf(
            Regex("^#{1,6}\\s+"), // Headings
            Regex("\\*\\*[^*]+\\*\\*"), // Bold
            Regex("\\*[^*]+\\*"), // Italic
            Regex("\\[.+\\]\\(.+\\)"), // Links
            Regex("^[*-]\\s+"), // List items
            Regex("^>\\s+"), // Blockquotes
            Regex("`[^`]+`"), // Inline code
            Regex("```[\\s\\S]+```"), // Code blocks
            Regex("^\\|.*\\|$") // Tables
        )

        return markdownPatterns.any { pattern ->
            pattern.find(text) != null
        }
    }

    /**
     * Count the number of headings in a markdown string.
     */
    fun countHeadings(text: String): Int {
        if (text.isBlank()) return 0
        return Regex("^#{1,6}\\s+", RegexOption.MULTILINE)
            .findAll(text)
            .count()
    }

    /**
     * Count the number of code blocks in a markdown string.
     */
    fun countCodeBlocks(text: String): Int {
        if (text.isBlank()) return 0
        return Regex("```[\\s\\S]+?```", RegexOption.MULTILINE)
            .findAll(text)
            .count()
    }

    /**
     * Check if a string contains a table in markdown format.
     */
    fun hasTable(text: String): Boolean {
        if (text.isBlank()) return false
        return Regex("^\\|.*\\|$", RegexOption.MULTILINE)
            .find(text) != null
    }

    /**
     * Count the number of list items in a markdown string.
     */
    fun countListItems(text: String): Int {
        if (text.isBlank()) return 0
        return Regex("^[*-]\\s+", RegexOption.MULTILINE)
            .findAll(text)
            .count()
    }

    /**
     * Check if a string is likely plain text (not markdown).
     */
    fun isPlainText(text: String): Boolean {
        return !isMarkdown(text)
    }

    /**
     * Get a plain text preview from markdown.
     * This removes markdown formatting for display purposes.
     * Note: Full markdown parsing is in the data layer.
     */
    fun getPlainTextPreview(text: String, maxLength: Int = 200): String {
        if (text.isBlank()) return ""

        var result = text
            // Remove code blocks
            .replace(Regex("```[\\s\\S]+?```"), "")
            // Remove inline code
            .replace(Regex("`[^`]+`"), "")
            // Remove links (keep text)
            .replace(Regex("\\[([^\\]]+)\\]\\([^)]+\\)"), "$1")
            // Remove images
            .replace(Regex("!\\[([^\\]]*)\\]\\([^)]+\\)"), "")
            // Remove headings
            .replace(Regex("^#{1,6}\\s+", RegexOption.MULTILINE), "")
            // Remove bold/italic
            .replace(Regex("\\*{1,2}([^*]+)\\*{1,2}"), "$1")
            // Remove blockquotes
            .replace(Regex("^>\\s+", RegexOption.MULTILINE), "")
            // Remove list markers
            .replace(Regex("^[*-]\\s+", RegexOption.MULTILINE), "• ")
            // Remove horizontal rules
            .replace(Regex("^---+$", RegexOption.MULTILINE), "")
            // Collapse multiple spaces/newlines
            .replace(Regex("\\s+"), " ")
            .trim()

        if (result.length > maxLength) {
            result = result.substring(0, maxLength) + "..."
        }

        return result
    }
}
