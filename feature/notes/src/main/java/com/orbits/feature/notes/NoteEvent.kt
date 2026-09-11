package com.orbits.feature.notes

import com.orbits.domain.notes.Note

/**
 * UI events for the notes feature.
 */
sealed class NoteEvent {
    // ─── List Events ─────────────────────────────────────────────

    data object LoadNotes : NoteEvent()
    data object Refresh : NoteEvent()
    data object ToggleViewMode : NoteEvent()
    data class SelectFilter(val filter: NoteFilter) : NoteEvent()
    data class SelectTag(val tag: String?) : NoteEvent()
    data class Search(val query: String) : NoteEvent()
    data class SelectNote(val noteId: String?) : NoteEvent()
    data class NavigateToNoteDetail(val noteId: String) : NoteEvent()
    data object NavigateToCreate : NoteEvent()
    data object DismissError : NoteEvent()

    // ─── CRUD Events ─────────────────────────────────────────────

    data class CreateNote(val note: Note) : NoteEvent()
    data class UpdateNote(val note: Note) : NoteEvent()
    data class DeleteNote(val noteId: String) : NoteEvent()
    data class PermanentDeleteNote(val noteId: String) : NoteEvent()
    data class RestoreNote(val noteId: String) : NoteEvent()

    // ─── Pin/Archive Events ──────────────────────────────────────

    data class PinNote(val noteId: String, val pinned: Boolean) : NoteEvent()
    data class ArchiveNote(val noteId: String, val archived: Boolean) : NoteEvent()

    // ─── Tag Events ──────────────────────────────────────────────

    data class AddTags(val noteId: String, val tags: List<String>) : NoteEvent()
    data class RemoveTags(val noteId: String, val tags: List<String>) : NoteEvent()

    // ─── Bulk Events ─────────────────────────────────────────────

    data class BulkDelete(val noteIds: List<String>, val permanent: Boolean) : NoteEvent()
    data class BulkUpdate(val noteIds: List<String>, val tags: List<String>? = null, val pinned: Boolean? = null, val archived: Boolean? = null) : NoteEvent()

    // ─── Form Events ─────────────────────────────────────────────

    data class FormTitleChanged(val title: String) : NoteEvent()
    data class FormContentChanged(val content: String) : NoteEvent()
    data class FormContentFormatChanged(val format: String) : NoteEvent()
    data class FormTagsChanged(val tags: String) : NoteEvent()
    data class FormIsPinnedToggled(val pinned: Boolean) : NoteEvent()
    data class FormIsArchivedToggled(val archived: Boolean) : NoteEvent()
    data class FormColorChanged(val color: String?) : NoteEvent()
    data class FormTaskIdChanged(val taskId: String?) : NoteEvent()
    data class FormEventIdChanged(val eventId: String?) : NoteEvent()
    data class FormAppointmentIdChanged(val appointmentId: String?) : NoteEvent()
    data class FormMeetingIdChanged(val meetingId: String?) : NoteEvent()
    data class FormReminderAtChanged(val reminderAt: String?) : NoteEvent()
    data class FormTogglePreview(val isPreview: Boolean) : NoteEvent()
    data object FormSubmit : NoteEvent()
    data object FormCancel : NoteEvent()
    data object FormDismissError : NoteEvent()
    data object FormReset : NoteEvent()
}
