package com.orbits.feature.notes

import com.orbits.domain.notes.Note

/**
 * UI state for the notes feature.
 */
data class NotesUiState(
    val isLoading: Boolean = true,
    val notes: List<Note> = emptyList(),
    val filteredNotes: List<Note> = emptyList(),
    val selectedFilter: NoteFilter = NoteFilter.ALL,
    val selectedTag: String? = null,
    val searchQuery: String = "",
    val selectedNoteId: String? = null,
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false,
    val allTags: List<String> = emptyList(),
    val isGridView: Boolean = true
)

/**
 * Note filter options.
 */
enum class NoteFilter(val displayName: String) {
    ALL("All"),
    PINNED("Pinned"),
    ARCHIVED("Archived"),
    RECENT("Recent"),
    TAGS("Tags")
}

/**
 * Note sort options.
 */
enum class NoteSort(val displayName: String) {
    UPDATED_DESC("Last Updated (Newest)"),
    UPDATED_ASC("Last Updated (Oldest)"),
    CREATED_DESC("Created (Newest)"),
    CREATED_ASC("Created (Oldest)"),
    TITLE_ASC("Title (A-Z)"),
    TITLE_DESC("Title (Z-A)")
}

/**
 * Note editor UI state.
 */
data class NoteEditorUiState(
    val id: String? = null,
    val title: String = "",
    val content: String = "",
    val contentFormat: String = "markdown",
    val tags: String = "",
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val color: String? = null,
    val taskId: String? = null,
    val eventId: String? = null,
    val appointmentId: String? = null,
    val meetingId: String? = null,
    val reminderAt: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val isEditMode: Boolean = false,
    val titleError: String? = null,
    val contentError: String? = null,
    val isPreviewMode: Boolean = false,
    val wordCount: Int = 0,
    val characterCount: Int = 0
) {
    val isFormValid: Boolean
        get() = content.isNotBlank() && contentError == null && titleError == null
}
