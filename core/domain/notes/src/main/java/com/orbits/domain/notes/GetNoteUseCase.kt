package com.orbits.domain.notes

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to get a single note by ID.
 */
class GetNoteUseCase @Inject constructor(
    private val notesRepository: NotesRepository
) {

    /**
     * Execute the use case.
     * @param noteId The ID of the note to retrieve
     * @return Result containing the note, or error
     */
    suspend operator fun invoke(noteId: String): Result<Note> {
        if (noteId.isBlank()) {
            return Result.Error(IllegalArgumentException("Note ID cannot be empty"))
        }
        return notesRepository.getNote(noteId)
    }
}
