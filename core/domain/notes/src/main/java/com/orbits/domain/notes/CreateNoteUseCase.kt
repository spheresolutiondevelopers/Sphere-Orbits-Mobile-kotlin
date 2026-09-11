package com.orbits.domain.notes

import com.orbits.core.common.Result
import com.orbits.core.common.extensions.nowUtc
import java.util.UUID
import javax.inject.Inject

/**
 * Use case to create a new note.
 */
class CreateNoteUseCase @Inject constructor(
    private val notesRepository: NotesRepository
) {

    /**
     * Execute the use case.
     * @param content The note content
     * @param title Optional note title
     * @param contentFormat The content format (markdown by default)
     * @param tags Optional tags
     * @param isPinned Whether the note should be pinned (default false)
     * @param isArchived Whether the note should be archived (default false)
     * @param color Optional hex color
     * @param taskId Optional linked task ID
     * @param eventId Optional linked event ID
     * @param appointmentId Optional linked appointment ID
     * @param meetingId Optional linked meeting ID
     * @param reminderAt Optional reminder date/time
     * @param userId User ID (if null, uses current user)
     * @return Result containing the created note, or error
     */
    suspend operator fun invoke(
        content: String,
        title: String? = null,
        contentFormat: String = "markdown",
        tags: List<String> = emptyList(),
        isPinned: Boolean = false,
        isArchived: Boolean = false,
        color: String? = null,
        taskId: String? = null,
        eventId: String? = null,
        appointmentId: String? = null,
        meetingId: String? = null,
        reminderAt: String? = null,
        userId: String? = null
    ): Result<Note> {
        // Validate input
        val validationResult = NoteValidation.validate(
            content = content,
            title = title,
            contentFormat = contentFormat,
            tags = tags,
            color = color,
            taskId = taskId,
            eventId = eventId,
            appointmentId = appointmentId,
            meetingId = meetingId,
            reminderAt = reminderAt
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
            taskId != null,
            eventId != null,
            appointmentId != null,
            meetingId != null
        ).count { it }

        if (linkedCount > 1) {
            return Result.Error(
                IllegalArgumentException("Note can only be linked to one entity at a time")
            )
        }

        // Create note
        val now = nowUtc()
        val note = Note(
            id = UUID.randomUUID().toString(),
            userId = userId ?: getCurrentUserId(),
            title = title,
            content = content,
            contentFormat = contentFormat,
            tags = tags,
            isPinned = isPinned,
            isArchived = isArchived,
            color = color,
            taskId = taskId,
            eventId = eventId,
            appointmentId = appointmentId,
            meetingId = meetingId,
            reminderAt = reminderAt,
            isDeleted = false,
            deletedAt = null,
            createdAt = now,
            updatedAt = now
        )

        return notesRepository.createNote(note)
    }

    // This should come from auth state
    private fun getCurrentUserId(): String {
        return "test_user_id"
    }
}
