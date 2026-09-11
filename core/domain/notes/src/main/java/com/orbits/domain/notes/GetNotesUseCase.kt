package com.orbits.domain.notes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case to get all notes for the current user.
 */
class GetNotesUseCase @Inject constructor(
    private val notesRepository: NotesRepository
) {

    /**
     * Execute the use case.
     * @return Flow emitting the list of notes
     */
    operator fun invoke(): Flow<List<Note>> {
        return notesRepository.getNotes()
    }

    /**
     * Get only pinned notes.
     */
    fun pinned(): Flow<List<Note>> {
        return notesRepository.getNotes().map { notes ->
            notes.filter { it.isPinned }
        }
    }

    /**
     * Get only active notes (not archived, not deleted).
     */
    fun active(): Flow<List<Note>> {
        return notesRepository.getNotes().map { notes ->
            notes.filter { !it.isArchived && !it.isDeleted }
        }
    }

    /**
     * Get only archived notes.
     */
    fun archived(): Flow<List<Note>> {
        return notesRepository.getNotes().map { notes ->
            notes.filter { it.isArchived && !it.isDeleted }
        }
    }
}
