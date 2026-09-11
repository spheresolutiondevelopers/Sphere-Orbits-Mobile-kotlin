package com.orbits.domain.notes

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to delete a note.
 */
class DeleteNoteUseCase @Inject constructor(
    private val notesRepository: NotesRepository
) {

    /**
     * Soft delete a note.
     * @param noteId The ID of the note to delete
     * @return Result indicating success or failure
     */
    suspend fun softDelete(noteId: String): Result<Unit> {
        if (noteId.isBlank()) {
            return Result.Error(IllegalArgumentException("Note ID cannot be empty"))
        }
        return notesRepository.deleteNote(noteId)
    }

    /**
     * Permanently delete a note.
     * @param noteId The ID of the note to permanently delete
     * @return Result indicating success or failure
     */
    suspend fun permanentDelete(noteId: String): Result<Unit> {
        if (noteId.isBlank()) {
            return Result.Error(IllegalArgumentException("Note ID cannot be empty"))
        }
        return notesRepository.permanentlyDeleteNote(noteId)
    }

    /**
     * Restore a soft-deleted note.
     * @param noteId The ID of the note to restore
     * @return Result containing the restored note, or error
     */
    suspend fun restore(noteId: String): Result<Note> {
        if (noteId.isBlank()) {
            return Result.Error(IllegalArgumentException("Note ID cannot be empty"))
        }
        return notesRepository.restoreNote(noteId)
    }

    /**
     * Execute the use case (soft delete by default).
     */
    suspend operator fun invoke(noteId: String): Result<Unit> {
        return softDelete(noteId)
    }
}
