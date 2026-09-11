package com.orbits.domain.notes

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to pin or unpin a note.
 */
class PinNoteUseCase @Inject constructor(
    private val notesRepository: NotesRepository
) {

    /**
     * Pin a note.
     * @param noteId The ID of the note to pin
     * @return Result containing the pinned note, or error
     */
    suspend fun pin(noteId: String): Result<Note> {
        if (noteId.isBlank()) {
            return Result.Error(IllegalArgumentException("Note ID cannot be empty"))
        }
        return notesRepository.pinNote(noteId, pinned = true)
    }

    /**
     * Unpin a note.
     * @param noteId The ID of the note to unpin
     * @return Result containing the unpinned note, or error
     */
    suspend fun unpin(noteId: String): Result<Note> {
        if (noteId.isBlank()) {
            return Result.Error(IllegalArgumentException("Note ID cannot be empty"))
        }
        return notesRepository.pinNote(noteId, pinned = false)
    }

    /**
     * Execute the use case.
     * @param noteId The ID of the note
     * @param pinned Whether to pin or unpin
     * @return Result containing the updated note, or error
     */
    suspend operator fun invoke(noteId: String, pinned: Boolean): Result<Note> {
        if (noteId.isBlank()) {
            return Result.Error(IllegalArgumentException("Note ID cannot be empty"))
        }
        return notesRepository.pinNote(noteId, pinned)
    }
}
