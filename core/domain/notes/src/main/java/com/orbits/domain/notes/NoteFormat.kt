package com.orbits.domain.notes

/**
 * Note content formats.
 */
enum class NoteFormat(val displayName: String) {
    MARKDOWN("Markdown"),
    PLAIN("Plain Text"),
    HTML("HTML");

    companion object {
        fun fromString(value: String): NoteFormat? {
            return values().find { it.name.equals(value, ignoreCase = true) }
        }

        fun fromDisplayName(value: String): NoteFormat? {
            return values().find { it.displayName.equals(value, ignoreCase = true) }
        }
    }
}
