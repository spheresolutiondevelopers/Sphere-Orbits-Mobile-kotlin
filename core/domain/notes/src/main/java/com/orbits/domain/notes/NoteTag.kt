package com.orbits.domain.notes

/**
 * Domain model for a note tag.
 */
data class NoteTag(
    val name: String,
    val count: Int = 0
) {
    /**
     * Get the tag name with the # prefix.
     */
    fun getFormattedName(): String = "#$name"

    /**
     * Check if the tag name is valid.
     */
    fun isValid(): Boolean {
        return name.isNotBlank() && name.length <= 50 && name.matches(Regex("^[A-Za-z0-9_\\-]+$"))
    }

    /**
     * Get the tag as a display string.
     */
    override fun toString(): String = getFormattedName()
}
