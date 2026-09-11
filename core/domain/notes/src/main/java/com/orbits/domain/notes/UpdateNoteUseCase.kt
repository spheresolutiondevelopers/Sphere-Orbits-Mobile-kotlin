package com.orbits.domain.notes

import com.orbits.core.common.Result
import com.orbits.core.common.extensions.nowUtc
import javax.inject.Inject

/**
 * Use case to update an existing note.
 */
class UpdateNoteUseCase @Inject constructor(
    private val notesRepository: NotesRepository
) {

    /**
     * Execute the use case.
     * @param note The updated note
     * @return Result containing the updated note, or error
     */
    suspend operator fun invoke(note: Note): Result<Note> {
        // Validate note
        val validationResult = NoteValidation.validate(
            content = note.content,
            title = note.title,
            contentFormat = note.contentFormat,
            tags = note.tags,
            color = note.color,
            taskId = note.taskId,
            eventId = note.eventId,
            appointmentId = note.appointmentId,
            meetingId = note.meetingId,
            reminderAt = note.reminderAt
        )

        if (!validationResult.isValid) {
            return Result.Error(
                IllegalArgumentException(
                    validationResult.errors.joinToString { it.message }
                )
            )
        }

        // Validate at most one linked entity
        val linkedCount = listOf(
            note.taskId != null,
            note.eventId != null,
            note.appointmentId != null,
            note.meetingId != null
        ).count { it }

        if (linkedCount > 1) {
            return Result.Error(
                IllegalArgumentException("Note can only be linked to one entity at a time")
            )
        }

        return notesRepository.updateNote(note)
    }

    /**
     * Update only the note's content.
     */
    suspend fun updateContent(noteId: String, content: String): Result<Note> {
        val note = notesRepository.getNote(noteId)
        if (note is Result.Error) {
            return Result.Error(note.exception)
        }
        val noteData = (note as Result.Success).data
        val updated = noteData.copy(
            content = content,
            updatedAt = nowUtc()
        )
        return notesRepository.updateNote(updated)
    }

    /**
     * Update only the note's title.
     */
    suspend fun updateTitle(noteId: String, title: String): Result<Note> {
        val note = notesRepository.getNote(noteId)
        if (note is Result.Error) {
            return Result.Error(note.exception)
        }
        val noteData = (note as Result.Success).data
        val updated = noteData.copy(
            title = title,
            updatedAt = nowUtc()
        )
        return notesRepository.updateNote(updated)
    }
}
