package com.orbits.domain.notes

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to remove tags from a note.
 */
class RemoveTagsUseCase @Inject constructor(
    private val notesRepository: NotesRepository
) {

    /**
     * Execute the use case.
     * @param noteId The ID of the note
     * @param tags The tags to remove
     * @return Result containing the updated note, or error
     */
    suspend operator fun invoke(noteId: String, tags: List<String>): Result<Note> {
        if (noteId.isBlank()) {
            return Result.Error(IllegalArgumentException("Note ID cannot be empty"))
        }
        if (tags.isEmpty()) {
            return Result.Error(IllegalArgumentException("At least one tag required"))
        }
        return notesRepository.removeTags(noteId, tags)
    }

    /**
     * Remove a single tag from a note.
     */
    suspend fun removeOne(noteId: String, tag: String): Result<Note> {
        return invoke(noteId, listOf(tag))
    }

    /**
     * Remove all tags from a note.
     */
    suspend fun removeAll(noteId: String): Result<Note> {
        if (noteId.isBlank()) {
            return Result.Error(IllegalArgumentException("Note ID cannot be empty"))
        }

        // Get the note and remove all tags
        val note = notesRepository.getNote(noteId)
        if (note is Result.Error) {
            return Result.Error(note.exception)
        }

        val noteData = (note as Result.Success).data
        val tags = noteData.tags
        if (tags.isEmpty()) {
            return Result.Success(noteData)
        }

        return notesRepository.removeTags(noteId, tags)
    }
}
