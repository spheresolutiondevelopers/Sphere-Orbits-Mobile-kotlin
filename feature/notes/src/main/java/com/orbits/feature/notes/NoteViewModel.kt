package com.orbits.feature.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbits.core.common.Result
import com.orbits.core.common.extensions.nowUtc
import com.orbits.domain.auth.AuthRepository
import com.orbits.domain.notes.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val notesRepository: NotesRepository,
    private val authRepository: AuthRepository,
    private val getAllTagsUseCase: GetAllTagsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(NotesUiState())
    val state: StateFlow<NotesUiState> = _state.asStateFlow()

    private val _editorState = MutableStateFlow(NoteEditorUiState())
    val editorState: StateFlow<NoteEditorUiState> = _editorState.asStateFlow()

    private var allNotes: List<Note> = emptyList()
    private var currentFilter = NoteFilter.ALL
    private var selectedTag: String? = null
    private var isSeeding = false

    init {
        loadNotes()
        loadTags()
    }

    fun handleEvent(event: NoteEvent) {
        when (event) {
            NoteEvent.LoadNotes -> loadNotes()
            NoteEvent.Refresh -> refresh()
            NoteEvent.ToggleViewMode -> toggleViewMode()
            is NoteEvent.SelectFilter -> applyFilter(event.filter)
            is NoteEvent.SelectTag -> applyTag(event.tag)
            is NoteEvent.Search -> search(event.query)
            is NoteEvent.SelectNote -> selectNote(event.noteId)
            is NoteEvent.NavigateToNoteDetail -> { /* Navigation handled by NavGraph */ }
            NoteEvent.NavigateToCreate -> navigateToCreate()
            NoteEvent.DismissError -> dismissError()

            is NoteEvent.CreateNote -> createNote(event.note)
            is NoteEvent.UpdateNote -> updateNote(event.note)
            is NoteEvent.DeleteNote -> deleteNote(event.noteId)
            is NoteEvent.PermanentDeleteNote -> permanentDeleteNote(event.noteId)
            is NoteEvent.RestoreNote -> restoreNote(event.noteId)

            is NoteEvent.PinNote -> pinNote(event.noteId, event.pinned)
            is NoteEvent.ArchiveNote -> archiveNote(event.noteId, event.archived)

            is NoteEvent.AddTags -> addTags(event.noteId, event.tags)
            is NoteEvent.RemoveTags -> removeTags(event.noteId, event.tags)

            is NoteEvent.BulkDelete -> bulkDelete(event.noteIds, event.permanent)
            is NoteEvent.BulkUpdate -> bulkUpdate(event.noteIds, event.tags, event.pinned, event.archived)

            is NoteEvent.FormTitleChanged -> updateFormTitle(event.title)
            is NoteEvent.FormContentChanged -> updateFormContent(event.content)
            is NoteEvent.FormContentFormatChanged -> updateFormContentFormat(event.format)
            is NoteEvent.FormTagsChanged -> updateFormTags(event.tags)
            is NoteEvent.FormIsPinnedToggled -> updateFormIsPinned(event.pinned)
            is NoteEvent.FormIsArchivedToggled -> updateFormIsArchived(event.archived)
            is NoteEvent.FormColorChanged -> updateFormColor(event.color)
            is NoteEvent.FormTaskIdChanged -> updateFormTaskId(event.taskId)
            is NoteEvent.FormEventIdChanged -> updateFormEventId(event.eventId)
            is NoteEvent.FormAppointmentIdChanged -> updateFormAppointmentId(event.appointmentId)
            is NoteEvent.FormMeetingIdChanged -> updateFormMeetingId(event.meetingId)
            is NoteEvent.FormReminderAtChanged -> updateFormReminderAt(event.reminderAt)
            is NoteEvent.FormTogglePreview -> togglePreview(event.isPreview)
            NoteEvent.FormSubmit -> submitForm()
            NoteEvent.FormCancel -> cancelForm()
            NoteEvent.FormDismissError -> dismissFormError()
            NoteEvent.FormReset -> resetForm()
        }
    }

    // ─── Load Operations ─────────────────────────────────────────

    private fun loadNotes() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                notesRepository.getNotes().collect { notes ->
                    val nonDeletedNotes = notes.filter { !it.isDeleted }
                    allNotes = nonDeletedNotes
                    
                    if (nonDeletedNotes.isEmpty() && !isSeeding) {
                        createOnboardingNotes()
                    }
                    
                    applyFiltersToState()
                    _state.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load notes"
                    )
                }
            }
        }
    }

    private fun loadTags() {
        viewModelScope.launch {
            try {
                val result = getAllTagsUseCase()
                when (result) {
                    is Result.Success -> {
                        _state.update { it.copy(allTags = result.data) }
                    }
                    else -> { /* Ignore tag loading errors */ }
                }
            } catch (e: Exception) {
                // Ignore tag loading errors
            }
        }
    }

    private fun refresh() {
        _state.update { it.copy(isRefreshing = true) }
        loadNotes()
        loadTags()
        _state.update { it.copy(isRefreshing = false) }
    }

    // ─── Filter & Search ─────────────────────────────────────────

    private fun applyFilter(filter: NoteFilter) {
        currentFilter = filter
        _state.update { it.copy(selectedFilter = filter) }
        applyFiltersToState()
    }

    private fun applyTag(tag: String?) {
        selectedTag = tag
        _state.update { it.copy(selectedTag = tag) }
        applyFiltersToState()
    }

    private fun applyFiltersToState() {
        val filtered = when (currentFilter) {
            NoteFilter.ALL -> allNotes
            NoteFilter.PINNED -> allNotes.filter { it.isPinned }
            NoteFilter.ARCHIVED -> allNotes.filter { it.isArchived }
            NoteFilter.RECENT -> allNotes.sortedByDescending { it.updatedAt }.take(20)
            NoteFilter.TAGS -> allNotes
        }

        val tagFiltered = if (selectedTag != null) {
            filtered.filter { it.tags.contains(selectedTag) }
        } else {
            filtered
        }

        val query = _state.value.searchQuery
        val finalFiltered = if (query.isNotBlank()) {
            tagFiltered.filter { note ->
                note.title?.contains(query, ignoreCase = true) ?: false ||
                note.content.contains(query, ignoreCase = true)
            }
        } else {
            tagFiltered
        }

        _state.update { it.copy(filteredNotes = finalFiltered) }
    }

    private fun toggleViewMode() {
        _state.update { it.copy(isGridView = !it.isGridView) }
    }

    private fun search(query: String) {
        _state.update { it.copy(searchQuery = query) }
        applyFiltersToState()
    }

    private fun selectNote(noteId: String?) {
        if (noteId == null || noteId == "new" || noteId == "null") {
            _editorState.value = NoteEditorUiState()
            return
        }

        viewModelScope.launch {
            _editorState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = notesRepository.getNote(noteId)
            when (result) {
                is Result.Success -> {
                    val note = result.data
                    _editorState.value = NoteEditorUiState(
                        id = note.id,
                        title = note.title ?: "",
                        content = note.content,
                        contentFormat = note.contentFormat,
                        tags = note.tags.joinToString(", "),
                        isPinned = note.isPinned,
                        isArchived = note.isArchived,
                        color = note.color,
                        taskId = note.taskId,
                        eventId = note.eventId,
                        appointmentId = note.appointmentId,
                        meetingId = note.meetingId,
                        reminderAt = note.reminderAt,
                        isEditMode = true,
                        isLoading = false
                    )
                }
                is Result.Error -> {
                    _editorState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = result.exception.message ?: "Failed to load note"
                        ) 
                    }
                }
                Result.Loading -> { }
            }
        }
    }

    private fun dismissError() {
        _state.update { it.copy(errorMessage = null) }
    }

    // ─── Navigation ──────────────────────────────────────────────

    private fun navigateToCreate() {
        _editorState.value = NoteEditorUiState()
    }

    private fun cancelForm() {
        _editorState.value = NoteEditorUiState()
    }

    private fun resetForm() {
        _editorState.value = NoteEditorUiState()
    }

    // ─── CRUD Operations ─────────────────────────────────────────

    private fun createNote(note: Note) {
        _state.update { it.copy(errorMessage = null) }

        viewModelScope.launch {
            val result = notesRepository.createNote(note)
            when (result) {
                is Result.Success -> {
                    _editorState.value = NoteEditorUiState(isSuccess = true)
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Failed to create note"
                        )
                    }
                    _editorState.update { it.copy(errorMessage = result.exception.message, isLoading = false) }
                }
                Result.Loading -> { /* Handled by form state */ }
            }
        }
    }

    private fun updateNote(note: Note) {
        _state.update { it.copy(errorMessage = null) }

        viewModelScope.launch {
            val result = notesRepository.updateNote(note)
            when (result) {
                is Result.Success -> {
                    _editorState.value = NoteEditorUiState(isSuccess = true)
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Failed to update note"
                        )
                    }
                    _editorState.update { it.copy(errorMessage = result.exception.message, isLoading = false) }
                }
                Result.Loading -> { /* Handled by form state */ }
            }
        }
    }

    private fun deleteNote(noteId: String) {
        viewModelScope.launch {
            val result = notesRepository.deleteNote(noteId)
            when (result) {
                is Result.Success -> { }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Failed to delete note"
                        )
                    }
                }
                Result.Loading -> { /* Ignore */ }
            }
        }
    }

    private fun permanentDeleteNote(noteId: String) {
        viewModelScope.launch {
            val result = notesRepository.permanentlyDeleteNote(noteId)
            when (result) {
                is Result.Success -> { }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Failed to permanently delete note"
                        )
                    }
                }
                Result.Loading -> { /* Ignore */ }
            }
        }
    }

    private fun restoreNote(noteId: String) {
        viewModelScope.launch {
            val result = notesRepository.restoreNote(noteId)
            when (result) {
                is Result.Success -> { }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Failed to restore note"
                        )
                    }
                }
                Result.Loading -> { /* Ignore */ }
            }
        }
    }

    // ─── Pin/Archive ─────────────────────────────────────────────

    private fun pinNote(noteId: String, pinned: Boolean) {
        viewModelScope.launch {
            val result = notesRepository.pinNote(noteId, pinned)
            when (result) {
                is Result.Success -> { }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Failed to update pin status"
                        )
                    }
                }
                Result.Loading -> { /* Ignore */ }
            }
        }
    }

    private fun archiveNote(noteId: String, archived: Boolean) {
        viewModelScope.launch {
            val result = notesRepository.archiveNote(noteId, archived)
            when (result) {
                is Result.Success -> { }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Failed to update archive status"
                        )
                    }
                }
                Result.Loading -> { /* Ignore */ }
            }
        }
    }

    // ─── Tags ─────────────────────────────────────────────────────

    private fun addTags(noteId: String, tags: List<String>) {
        viewModelScope.launch {
            val result = notesRepository.addTags(noteId, tags)
            when (result) {
                is Result.Success -> {
                    loadTags()
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Failed to add tags"
                        )
                    }
                }
                Result.Loading -> { /* Ignore */ }
            }
        }
    }

    private fun removeTags(noteId: String, tags: List<String>) {
        viewModelScope.launch {
            val result = notesRepository.removeTags(noteId, tags)
            when (result) {
                is Result.Success -> {
                    loadTags()
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Failed to remove tags"
                        )
                    }
                }
                Result.Loading -> { /* Ignore */ }
            }
        }
    }

    // ─── Bulk Operations ─────────────────────────────────────────

    private fun bulkDelete(noteIds: List<String>, permanent: Boolean) {
        viewModelScope.launch {
            val result = notesRepository.bulkDelete(noteIds, permanent)
            when (result) {
                is Result.Success -> { }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Failed to delete notes"
                        )
                    }
                }
                Result.Loading -> { /* Ignore */ }
            }
        }
    }

    private fun bulkUpdate(noteIds: List<String>, tags: List<String>?, pinned: Boolean?, archived: Boolean?) {
        viewModelScope.launch {
            val result = notesRepository.bulkUpdate(noteIds, tags, pinned, archived)
            when (result) {
                is Result.Success -> { }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Failed to update notes"
                        )
                    }
                }
                Result.Loading -> { /* Ignore */ }
            }
        }
    }

    // ─── Form Operations ──────────────────────────────────────────

    private fun updateFormTitle(title: String) {
        _editorState.update { state ->
            state.copy(
                title = title,
                titleError = if (title.isNotBlank() && title.length > 255) {
                    "Title must be 255 characters or less"
                } else null
            )
        }
    }

    private fun updateFormContent(content: String) {
        _editorState.update { state ->
            state.copy(
                content = content,
                wordCount = content.split(Regex("\\s+")).filter { it.isNotBlank() }.size,
                characterCount = content.length,
                contentError = if (content.isNotBlank() && content.length > 100000) {
                    "Content must be 100,000 characters or less"
                } else null
            )
        }
    }

    private fun updateFormContentFormat(format: String) {
        _editorState.update { it.copy(contentFormat = format) }
    }

    private fun updateFormTags(tags: String) {
        _editorState.update { it.copy(tags = tags) }
    }

    private fun updateFormIsPinned(pinned: Boolean) {
        _editorState.update { it.copy(isPinned = pinned) }
    }

    private fun updateFormIsArchived(archived: Boolean) {
        _editorState.update { it.copy(isArchived = archived) }
    }

    private fun updateFormColor(color: String?) {
        _editorState.update { it.copy(color = color) }
    }

    private fun updateFormTaskId(taskId: String?) {
        _editorState.update { it.copy(taskId = taskId) }
    }

    private fun updateFormEventId(eventId: String?) {
        _editorState.update { it.copy(eventId = eventId) }
    }

    private fun updateFormAppointmentId(appointmentId: String?) {
        _editorState.update { it.copy(appointmentId = appointmentId) }
    }

    private fun updateFormMeetingId(meetingId: String?) {
        _editorState.update { it.copy(meetingId = meetingId) }
    }

    private fun updateFormReminderAt(reminderAt: String?) {
        _editorState.update { it.copy(reminderAt = reminderAt) }
    }

    private fun togglePreview(isPreview: Boolean) {
        _editorState.update { it.copy(isPreviewMode = isPreview) }
    }

    private fun submitForm() {
        val state = _editorState.value

        if (!state.isFormValid) {
            _editorState.update { it.copy(errorMessage = "Please fix the errors above") }
            return
        }

        _editorState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            // Important: Use "test_user_id" if current repo implementation requires it
            val userId = currentUser?.id ?: "test_user_id"

            val tagsList = state.tags.split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() }

            val note = Note(
                id = state.id ?: UUID.randomUUID().toString(),
                userId = userId,
                title = state.title.takeIf { it.isNotBlank() },
                content = state.content,
                contentFormat = state.contentFormat,
                tags = tagsList,
                isPinned = state.isPinned,
                isArchived = state.isArchived,
                color = state.color,
                taskId = state.taskId,
                eventId = state.eventId,
                appointmentId = state.appointmentId,
                meetingId = state.meetingId,
                reminderAt = state.reminderAt,
                isDeleted = false,
                deletedAt = null,
                createdAt = nowUtc(),
                updatedAt = nowUtc()
            )

            if (state.isEditMode) {
                handleEvent(NoteEvent.UpdateNote(note))
            } else {
                handleEvent(NoteEvent.CreateNote(note))
            }
        }
    }

    private fun dismissFormError() {
        _editorState.update { it.copy(errorMessage = null) }
    }

    // ─── Helper ──────────────────────────────────────────────────

    private fun createOnboardingNotes() {
        if (isSeeding) return
        isSeeding = true
        
        viewModelScope.launch {
            try {
                // Use "test_user_id" to match hardcoded repository for now
                val userId = authRepository.getCurrentUser()?.id ?: "test_user_id"
                val onboardingNotes = listOf(
                    Note(
                        id = UUID.randomUUID().toString(),
                        userId = userId,
                        title = "Onboarding Steps",
                        content = "• Create your first task\n• Link a note to an event\n• Set up your profile\n• Try the AI Assistant",
                        color = "#FBBF24", // amber
                        tags = listOf("onboarding"),
                        createdAt = nowUtc(),
                        updatedAt = nowUtc()
                    ),
                    Note(
                        id = UUID.randomUUID().toString(),
                        userId = userId,
                        title = "About the App",
                        content = "Sphere Orbit is an intelligent scheduling platform designed to help you manage your tasks, events, and notes seamlessly. Built with a focus on simplicity and productivity.",
                        color = "#A78BFA", // violet
                        tags = listOf("about"),
                        createdAt = nowUtc(),
                        updatedAt = nowUtc()
                    ),
                    Note(
                        id = UUID.randomUUID().toString(),
                        userId = userId,
                        title = "How to Use the App",
                        content = "• Use the '+' button to add new items\n• Long-press on cards for quick actions\n• Toggle between grid and list views using the topbar icons\n• Use search to find anything instantly",
                        color = "#2DD4A0", // teal
                        tags = listOf("guide"),
                        createdAt = nowUtc(),
                        updatedAt = nowUtc()
                    )
                )
                onboardingNotes.forEach { notesRepository.createNote(it) }
            } catch (e: Exception) {
                // Ignore seeding errors
            } finally {
                isSeeding = false
            }
        }
    }
}
