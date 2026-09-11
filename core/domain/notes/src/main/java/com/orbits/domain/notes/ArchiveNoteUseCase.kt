package com.orbits.domain.notes

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to archive or unarchive a note.
 */
class ArchiveNoteUseCase @Inject constructor(
    private val notesRepository: NotesRepository
) {

    /**
     * Archive a note.
     * @param noteId The ID of the note to archive
     * @return Result containing the archived note, or error
     */
    suspend fun archive(noteId: String): Result<Note> {
        if (noteId.isBlank()) {
            return Result.Error(IllegalArgumentException("Note ID cannot be empty"))
        }
        return notesRepository.archiveNote(noteId, archived = true)
    }

    /**
     * Unarchive a note.
     * @param noteId The ID of the note to unarchive
     * @return Result containing the unarchived note, or error
     */
    suspend fun unarchive(noteId: String): Result<Note> {
        if (noteId.isBlank()) {
            return Result.Error(IllegalArgumentException("Note ID cannot be empty"))
        }
        return notesRepository.archiveNote(noteId, archived = false)
    }

    /**
     * Execute the use case.
     * @param noteId The ID of the note
     * @param archived Whether to archive or unarchive
     * @return Result containing the updated note, or error
     */
    suspend operator fun invoke(noteId: String, archived: Boolean): Result<Note> {
        if (noteId.isBlank()) {
            return Result.Error(IllegalArgumentException("Note ID cannot be empty"))
        }
        return notesRepository.archiveNote(noteId, archived)
    }
}
