package com.orbits.domain.notes

/**
 * Sort options for notes.
 */
enum class NoteSortOption(val displayName: String) {
    UPDATED_DESC("Last Updated (Newest First)"),
    UPDATED_ASC("Last Updated (Oldest First)"),
    CREATED_DESC("Created Date (Newest First)"),
    CREATED_ASC("Created Date (Oldest First)"),
    TITLE_ASC("Title (A-Z)"),
    TITLE_DESC("Title (Z-A)"),
    PINNED_FIRST("Pinned First"),
    MOST_TAGS("Most Tags");

    companion object {
        fun fromString(value: String): NoteSortOption? {
            return values().find { it.name.equals(value, ignoreCase = true) }
        }

        fun fromDisplayName(value: String): NoteSortOption? {
            return values().find { it.displayName.equals(value, ignoreCase = true) }
        }
    }
}
