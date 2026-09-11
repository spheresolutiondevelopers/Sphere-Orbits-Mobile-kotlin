package com.orbits.domain.notes

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to add tags to a note.
 */
class AddTagsUseCase @Inject constructor(
    private val notesRepository: NotesRepository
) {

    /**
     * Execute the use case.
     * @param noteId The ID of the note
     * @param tags The tags to add
     * @return Result containing the updated note, or error
     */
    suspend operator fun invoke(noteId: String, tags: List<String>): Result<Note> {
        if (noteId.isBlank()) {
            return Result.Error(IllegalArgumentException("Note ID cannot be empty"))
        }
        if (tags.isEmpty()) {
            return Result.Error(IllegalArgumentException("At least one tag required"))
        }

        // Validate tags
        tags.forEach { tag ->
            if (!NoteTag(tag).isValid()) {
                return Result.Error(
                    IllegalArgumentException(
                        "Invalid tag: '$tag'. Tags must be 1-50 characters, alphanumeric, underscores, or hyphens"
                    )
                )
            }
        }

        return notesRepository.addTags(noteId, tags)
    }

    /**
     * Add a single tag to a note.
     */
    suspend fun addOne(noteId: String, tag: String): Result<Note> {
        return invoke(noteId, listOf(tag))
    }
}
