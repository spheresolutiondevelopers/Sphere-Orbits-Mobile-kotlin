package com.orbits.domain.notes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case to search notes.
 */
class SearchNotesUseCase @Inject constructor(
    private val notesRepository: NotesRepository
) {

    /**
     * Execute the use case.
     * @param query The search query
     * @return Flow emitting the list of matching notes
     */
    operator fun invoke(query: String): Flow<List<Note>> {
        require(query.isNotBlank()) { "Search query cannot be empty" }
        return notesRepository.searchNotes(query)
    }

    /**
     * Search notes with tag filtering.
     */
    fun withTags(query: String, tags: List<String>): Flow<List<Note>> {
        require(query.isNotBlank()) { "Search query cannot be empty" }
        return notesRepository.searchNotes(query).map { notes ->
            notes.filter { note ->
                tags.all { tag -> note.tags.contains(tag) }
            }
        }
    }

    /**
     * Search notes with status filtering.
     */
    fun withStatus(query: String, includeArchived: Boolean = false): Flow<List<Note>> {
        require(query.isNotBlank()) { "Search query cannot be empty" }
        return notesRepository.searchNotes(query).map { notes ->
            notes.filter { note ->
                if (includeArchived) true else !note.isArchived
            }
        }
    }
}
