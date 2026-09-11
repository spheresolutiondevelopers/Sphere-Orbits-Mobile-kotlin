package com.orbits.domain.notes

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to update multiple notes at once.
 */
class BulkUpdateNotesUseCase @Inject constructor(
    private val notesRepository: NotesRepository
) {

    /**
     * Bulk update tags.
     * @param noteIds The IDs of the notes to update
     * @param tags The tags to set (replaces all existing tags)
     * @return Result indicating success or failure
     */
    suspend fun updateTags(noteIds: List<String>, tags: List<String>): Result<Unit> {
        if (noteIds.isEmpty()) {
            return Result.Error(IllegalArgumentException("At least one note ID required"))
        }
        return notesRepository.bulkUpdate(noteIds, tags = tags)
    }

    /**
     * Bulk pin notes.
     * @param noteIds The IDs of the notes to pin
     * @param pinned Whether to pin or unpin
     * @return Result indicating success or failure
     */
    suspend fun updatePin(noteIds: List<String>, pinned: Boolean): Result<Unit> {
        if (noteIds.isEmpty()) {
            return Result.Error(IllegalArgumentException("At least one note ID required"))
        }
        return notesRepository.bulkUpdate(noteIds, isPinned = pinned)
    }

    /**
     * Bulk archive notes.
     * @param noteIds The IDs of the notes to archive
     * @param archived Whether to archive or unarchive
     * @return Result indicating success or failure
     */
    suspend fun updateArchive(noteIds: List<String>, archived: Boolean): Result<Unit> {
        if (noteIds.isEmpty()) {
            return Result.Error(IllegalArgumentException("At least one note ID required"))
        }
        return notesRepository.bulkUpdate(noteIds, isArchived = archived)
    }

    /**
     * Combined bulk update.
     * @param noteIds The IDs of the notes to update
     * @param tags Optional tags to set
     * @param pinned Optional pin status
     * @param archived Optional archive status
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(
        noteIds: List<String>,
        tags: List<String>? = null,
        pinned: Boolean? = null,
        archived: Boolean? = null
    ): Result<Unit> {
        if (noteIds.isEmpty()) {
            return Result.Error(IllegalArgumentException("At least one note ID required"))
        }

        // Validate tags if provided
        tags?.forEach { tag ->
            if (!NoteTag(tag).isValid()) {
                return Result.Error(
                    IllegalArgumentException(
                        "Invalid tag: '$tag'. Tags must be 1-50 characters, alphanumeric, underscores, or hyphens"
                    )
                )
            }
        }

        return notesRepository.bulkUpdate(noteIds, tags, pinned, archived)
    }
}
