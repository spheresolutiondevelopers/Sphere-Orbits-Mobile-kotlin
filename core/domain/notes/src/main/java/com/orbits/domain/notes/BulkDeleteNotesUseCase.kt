package com.orbits.domain.notes

import com.orbits.core.common.Result
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case to delete multiple notes at once.
 */
class BulkDeleteNotesUseCase @Inject constructor(
    private val notesRepository: NotesRepository
) {

    /**
     * Soft delete multiple notes.
     * @param noteIds The IDs of the notes to delete
     * @return Result indicating success or failure
     */
    suspend fun softDelete(noteIds: List<String>): Result<Unit> {
        if (noteIds.isEmpty()) {
            return Result.Error(IllegalArgumentException("At least one note ID required"))
        }
        return notesRepository.bulkDelete(noteIds, permanent = false)
    }

    /**
     * Permanently delete multiple notes.
     * @param noteIds The IDs of the notes to permanently delete
     * @return Result indicating success or failure
     */
    suspend fun permanentDelete(noteIds: List<String>): Result<Unit> {
        if (noteIds.isEmpty()) {
            return Result.Error(IllegalArgumentException("At least one note ID required"))
        }
        return notesRepository.bulkDelete(noteIds, permanent = true)
    }

    /**
     * Delete all notes matching a tag.
     * @param tag The tag to filter by
     * @param permanent Whether to permanently delete
     * @return Result indicating success or failure
     */
    suspend fun byTag(tag: String, permanent: Boolean = false): Result<Unit> {
        if (tag.isBlank()) {
            return Result.Error(IllegalArgumentException("Tag cannot be empty"))
        }

        val notes = notesRepository.getNotesByTag(tag).first()

        if (notes.isEmpty()) {
            return Result.Success(Unit)
        }

        val noteIds = notes.map { it.id }
        return notesRepository.bulkDelete(noteIds, permanent)
    }

    /**
     * Execute the use case (soft delete by default).
     */
    suspend operator fun invoke(noteIds: List<String>, permanent: Boolean = false): Result<Unit> {
        if (noteIds.isEmpty()) {
            return Result.Error(IllegalArgumentException("At least one note ID required"))
        }
        return notesRepository.bulkDelete(noteIds, permanent)
    }
}
