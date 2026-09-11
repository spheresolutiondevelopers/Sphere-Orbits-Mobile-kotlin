package com.orbits.domain.notes

import com.orbits.core.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Notes repository interface.
 * Defines all note operations.
 * Implemented by :core:data:notes.
 */
interface NotesRepository {

    // ─── Read Operations ──────────────────────────────────────────

    /**
     * Get all notes for the current user.
     */
    fun getNotes(): Flow<List<Note>>

    /**
     * Search notes by content or title.
     */
    fun searchNotes(query: String): Flow<List<Note>>

    /**
     * Get notes linked to a task.
     */
    fun getNotesForTask(taskId: String): Flow<List<Note>>

    /**
     * Get notes linked to an event.
     */
    fun getNotesForEvent(eventId: String): Flow<List<Note>>

    /**
     * Get notes linked to an appointment.
     */
    fun getNotesForAppointment(appointmentId: String): Flow<List<Note>>

    /**
     * Get notes linked to a meeting.
     */
    fun getNotesForMeeting(meetingId: String): Flow<List<Note>>

    /**
     * Get a single note by ID.
     */
    suspend fun getNote(noteId: String): Result<Note>

    // ─── Write Operations ─────────────────────────────────────────

    /**
     * Create a new note.
     */
    suspend fun createNote(note: Note): Result<Note>

    /**
     * Update an existing note.
     */
    suspend fun updateNote(note: Note): Result<Note>

    /**
     * Delete a note (soft delete).
     */
    suspend fun deleteNote(noteId: String): Result<Unit>

    /**
     * Permanently delete a note.
     */
    suspend fun permanentlyDeleteNote(noteId: String): Result<Unit>

    /**
     * Restore a deleted note.
     */
    suspend fun restoreNote(noteId: String): Result<Note>

    // ─── Pin/Archive Operations ───────────────────────────────────

    /**
     * Pin or unpin a note.
     */
    suspend fun pinNote(noteId: String, pinned: Boolean): Result<Note>

    /**
     * Archive or unarchive a note.
     */
    suspend fun archiveNote(noteId: String, archived: Boolean): Result<Note>

    // ─── Tags ─────────────────────────────────────────────────────

    /**
     * Add tags to a note.
     */
    suspend fun addTags(noteId: String, tags: List<String>): Result<Note>

    /**
     * Remove tags from a note.
     */
    suspend fun removeTags(noteId: String, tags: List<String>): Result<Note>

    /**
     * Get notes by a specific tag.
     */
    fun getNotesByTag(tag: String): Flow<List<Note>>

    /**
     * Get all tags used by the user.
     */
    suspend fun getAllTags(): Result<List<String>>

    // ─── Bulk Operations ──────────────────────────────────────────

    /**
     * Delete multiple notes (soft or permanent).
     */
    suspend fun bulkDelete(noteIds: List<String>, permanent: Boolean): Result<Unit>

    /**
     * Bulk update notes (tags, pin, archive).
     */
    suspend fun bulkUpdate(
        noteIds: List<String>,
        tags: List<String>? = null,
        isPinned: Boolean? = null,
        isArchived: Boolean? = null
    ): Result<Unit>
}
