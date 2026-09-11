package com.orbits.domain.notes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case to get notes by a specific tag.
 */
class GetNotesByTagUseCase @Inject constructor(
    private val notesRepository: NotesRepository
) {

    /**
     * Execute the use case.
     * @param tag The tag to filter by
     * @return Flow emitting the list of notes with the tag
     */
    operator fun invoke(tag: String): Flow<List<Note>> {
        require(tag.isNotBlank()) { "Tag cannot be empty" }
        return notesRepository.getNotesByTag(tag)
    }

    /**
     * Get notes by multiple tags (AND condition).
     * @param tags The tags to filter by (all must be present)
     * @return Flow emitting the list of notes with all tags
     */
    fun byAllTags(tags: List<String>): Flow<List<Note>> {
        require(tags.isNotEmpty()) { "At least one tag required" }

        // Start with the first tag and filter down
        var result: Flow<List<Note>> = notesRepository.getNotesByTag(tags.first())

        for (i in 1 until tags.size) {
            val tag = tags[i]
            result = result.map { notes ->
                notes.filter { note -> note.tags.contains(tag) }
            }
        }

        return result
    }

    /**
     * Get notes by any of the tags (OR condition).
     */
    fun byAnyTag(tags: List<String>): Flow<List<Note>> {
        require(tags.isNotEmpty()) { "At least one tag required" }

        // Start with the first tag and merge results
        var result: Flow<List<Note>> = notesRepository.getNotesByTag(tags.first())

        for (i in 1 until tags.size) {
            val tag = tags[i]
            result = combine(
                result,
                notesRepository.getNotesByTag(tag)
            ) { existing, new ->
                (existing + new).distinctBy { it.id }
            }
        }

        return result
    }
}
