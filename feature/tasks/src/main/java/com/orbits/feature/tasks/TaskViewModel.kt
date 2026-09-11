package com.orbits.feature.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbits.core.common.Result
import com.orbits.core.common.extensions.nowUtc
import com.orbits.domain.auth.AuthRepository
import com.orbits.domain.tasks.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val getTaskUseCase: GetTaskUseCase,
    private val createTaskUseCase: CreateTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TasksUiState())
    val state: StateFlow<TasksUiState> = _state.asStateFlow()

    private val _formState = MutableStateFlow(TaskFormUiState())
    val formState: StateFlow<TaskFormUiState> = _formState.asStateFlow()

    private var allTasks: List<Task> = emptyList()
    private var currentFilter = TaskFilter.ALL

    init {
        loadTasks()
    }

    fun handleEvent(event: TaskEvent) {
        when (event) {
            TaskEvent.LoadTasks -> loadTasks()
            TaskEvent.Refresh -> refresh()
            is TaskEvent.SelectFilter -> applyFilter(event.filter)
            is TaskEvent.Search -> search(event.query)
            is TaskEvent.SelectTask -> selectTask(event.taskId)
            TaskEvent.DismissError -> dismissError()
            TaskEvent.NavigateToCreate -> navigateToCreate()
            is TaskEvent.NavigateToEdit -> navigateToEdit(event.task)
            is TaskEvent.NavigateToDetail -> navigateToDetail(event.taskId)
            TaskEvent.NavigateBack -> navigateBack()
            is TaskEvent.CreateTask -> createTask(event.task)
            is TaskEvent.UpdateTask -> updateTask(event.task)
            is TaskEvent.CompleteTask -> completeTask(event.taskId)
            is TaskEvent.DeleteTask -> deleteTask(event.taskId)
            is TaskEvent.RestoreTask -> restoreTask(event.taskId)
            // Form events
            is TaskEvent.FormTitleChanged -> updateFormTitle(event.title)
            is TaskEvent.FormDescriptionChanged -> updateFormDescription(event.description)
            is TaskEvent.FormTaskTypeChanged -> updateFormTaskType(event.taskType)
            is TaskEvent.FormPriorityChanged -> updateFormPriority(event.priority)
            is TaskEvent.FormDueDateChanged -> updateFormDueDate(event.dueDate)
            is TaskEvent.FormDueTimeChanged -> updateFormDueTime(event.dueTime)
            is TaskEvent.FormStartDateChanged -> updateFormStartDate(event.startDate)
            is TaskEvent.FormStartTimeChanged -> updateFormStartTime(event.startTime)
            is TaskEvent.FormEndDateChanged -> updateFormEndDate(event.endDate)
            is TaskEvent.FormEndTimeChanged -> updateFormEndTime(event.endTime)
            is TaskEvent.FormLocationNameChanged -> updateFormLocationName(event.location)
            is TaskEvent.FormLocationAddressChanged -> updateFormLocationAddress(event.address)
            is TaskEvent.FormTagsChanged -> updateFormTags(event.tags)
            is TaskEvent.FormNotesChanged -> updateFormNotes(event.notes)
            is TaskEvent.FormCategoryChanged -> updateFormCategory(event.categoryId)
            TaskEvent.FormSubmit -> submitForm()
            TaskEvent.FormCancel -> cancelForm()
            TaskEvent.FormDismissError -> dismissFormError()
        }
    }

    // ─── Load Operations ─────────────────────────────────────────

    private fun loadTasks() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                getTasksUseCase().collect { tasks ->
                    allTasks = tasks
                    applyFilterToState(currentFilter)
                    _state.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load tasks"
                    )
                }
            }
        }
    }

    private fun refresh() {
        _state.update { it.copy(isRefreshing = true) }
        loadTasks()
        _state.update { it.copy(isRefreshing = false) }
    }

    // ─── Filter & Search ─────────────────────────────────────────

    private fun applyFilter(filter: TaskFilter) {
        currentFilter = filter
        applyFilterToState(filter)
        _state.update { it.copy(selectedFilter = filter) }
    }

    private fun applyFilterToState(filter: TaskFilter) {
        val filtered = when (filter) {
            TaskFilter.ALL -> allTasks.filter { !it.isDeleted }
            TaskFilter.TODAY -> filterTodayTasks()
            TaskFilter.UPCOMING -> filterUpcomingTasks()
            TaskFilter.OVERDUE -> allTasks.filter { it.isOverdue() && !it.isDeleted }
            TaskFilter.COMPLETED -> allTasks.filter { it.isCompleted() && !it.isDeleted }
            TaskFilter.IN_PROGRESS -> allTasks.filter { it.status == "in_progress" && !it.isDeleted }
            TaskFilter.HIGH_PRIORITY -> allTasks.filter { it.priorityLevel == "high" && !it.isDeleted }
            TaskFilter.CRITICAL -> allTasks.filter { it.priorityLevel == "critical" && !it.isDeleted }
        }

        // Calculate counts for UI
        val categoryCounts = allTasks.filter { !it.isDeleted }.groupBy { it.taskType }.mapValues { it.value.size }
        val priorityCounts = allTasks.filter { !it.isDeleted }.groupBy { it.priorityLevel }.mapValues { it.value.size }
        val progressCounts = allTasks.filter { !it.isDeleted }.groupBy { it.status }.mapValues { it.value.size }
        
        // Date counts (simplified)
        val today = LocalDate.now().toString()
        val dateCounts = mapOf(
            "Today" to allTasks.count { it.dueDate == today && !it.isDeleted },
            "Upcoming" to allTasks.count { it.dueDate != null && it.dueDate!! > today && !it.isDeleted }
        )

        // Apply search if there's a query
        val query = _state.value.searchQuery
        val finalFiltered = if (query.isNotBlank()) {
            filtered.filter { task ->
                task.title.contains(query, ignoreCase = true) ||
                        (task.description?.contains(query, ignoreCase = true) ?: false) ||
                        task.tags.any { it.contains(query, ignoreCase = true) }
            }
        } else {
            filtered
        }

        _state.update { 
            it.copy(
                tasks = allTasks.filter { !it.isDeleted },
                filteredTasks = finalFiltered,
                categoryCounts = categoryCounts,
                priorityCounts = priorityCounts,
                progressCounts = progressCounts,
                dateCounts = dateCounts
            ) 
        }
    }

    private fun filterTodayTasks(): List<Task> {
        val today = LocalDate.now().toString()
        return allTasks.filter { task ->
            val dueDate = task.dueDate
            !task.isDeleted &&
                    (dueDate == today ||
                            task.startDate == today ||
                            (dueDate != null && dueDate <= today && !task.isCompleted()))
        }
    }

    private fun filterUpcomingTasks(): List<Task> {
        val today = LocalDate.now().toString()
        val nextWeek = LocalDate.now().plusDays(7).toString()
        return allTasks.filter { task ->
            val dueDate = task.dueDate
            !task.isDeleted &&
                    !task.isCompleted() &&
                    dueDate != null &&
                    dueDate > today &&
                    dueDate <= nextWeek
        }
    }

    private fun search(query: String) {
        _state.update { it.copy(searchQuery = query) }
        applyFilterToState(currentFilter)
    }

    private fun selectTask(taskId: String) {
        _state.update { it.copy(selectedTaskId = taskId) }
    }

    private fun dismissError() {
        _state.update { it.copy(errorMessage = null) }
    }

    // ─── Navigation ──────────────────────────────────────────────

    private fun navigateToCreate() {
        _state.update { it.copy(isCreateMode = true) }
        _formState.value = TaskFormUiState()
    }

    private fun navigateToEdit(task: Task) {
        _state.update { it.copy(isEditMode = true, editingTask = task) }
        _formState.value = TaskFormUiState(
            id = task.id,
            title = task.title,
            description = task.description ?: "",
            taskType = task.taskType,
            priorityLevel = task.priorityLevel,
            dueDate = task.dueDate,
            dueTime = task.dueTime,
            startDate = task.startDate,
            startTime = task.startTime,
            endDate = task.endDate,
            endTime = task.endTime,
            locationName = task.locationName,
            locationAddress = task.locationAddress,
            tags = task.tags.joinToString(", "),
            notes = task.notes ?: "",
            categoryId = task.categoryId,
            isEditMode = true
        )
    }

    private fun navigateToDetail(taskId: String) {
        // Handled by navigation graph
    }

    private fun navigateBack() {
        _state.update {
            it.copy(
                isCreateMode = false,
                isEditMode = false,
                editingTask = null
            )
        }
        _formState.value = TaskFormUiState()
    }

    // ─── CRUD Operations ──────────────────────────────────────────

    private fun createTask(task: Task) {
        _state.update { it.copy(errorMessage = null) }

        viewModelScope.launch {
            val result = createTaskUseCase(
                title = task.title,
                description = task.description,
                taskType = task.taskType,
                priorityLevel = task.priorityLevel,
                dueDate = task.dueDate,
                dueTime = task.dueTime,
                startDate = task.startDate,
                startTime = task.startTime,
                endDate = task.endDate,
                endTime = task.endTime,
                locationName = task.locationName,
                locationAddress = task.locationAddress,
                tags = task.tags,
                notes = task.notes,
                categoryId = task.categoryId
            )

            when (result) {
                is Result.Success -> {
                    loadTasks()
                    _state.update { it.copy(isCreateMode = false) }
                    _formState.value = TaskFormUiState(isSuccess = true)
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Failed to create task"
                        )
                    }
                }
                Result.Loading -> { /* Handled by form state */ }
            }
        }
    }

    private fun updateTask(task: Task) {
        _state.update { it.copy(errorMessage = null) }

        viewModelScope.launch {
            val result = updateTaskUseCase(task)

            when (result) {
                is Result.Success -> {
                    loadTasks()
                    _state.update { it.copy(isEditMode = false, editingTask = null) }
                    _formState.value = TaskFormUiState(isSuccess = true)
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Failed to update task"
                        )
                    }
                }
                Result.Loading -> { /* Handled by form state */ }
            }
        }
    }

    private fun completeTask(taskId: String) {
        viewModelScope.launch {
            val result = completeTaskUseCase(taskId)
            when (result) {
                is Result.Success -> loadTasks()
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Failed to complete task"
                        )
                    }
                }
                Result.Loading -> { /* Ignore */ }
            }
        }
    }

    private fun deleteTask(taskId: String) {
        viewModelScope.launch {
            val result = deleteTaskUseCase(taskId)
            when (result) {
                is Result.Success -> loadTasks()
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "Failed to delete task"
                        )
                    }
                }
                Result.Loading -> { /* Ignore */ }
            }
        }
    }

    private fun restoreTask(taskId: String) {
        // This would call a restore use case if available
        // For now, we just reload
        loadTasks()
    }

    // ─── Form Operations ──────────────────────────────────────────

    private fun updateFormTitle(title: String) {
        _formState.update { state ->
            state.copy(
                title = title,
                titleError = if (title.isNotBlank() && title.length > 255) {
                    "Title must be 255 characters or less"
                } else null
            )
        }
    }

    private fun updateFormDescription(description: String) {
        _formState.update { it.copy(description = description) }
    }

    private fun updateFormTaskType(taskType: String) {
        _formState.update { it.copy(taskType = taskType) }
    }

    private fun updateFormPriority(priority: String) {
        _formState.update { it.copy(priorityLevel = priority) }
    }

    private fun updateFormDueDate(dueDate: String?) {
        _formState.update { state ->
            state.copy(
                dueDate = dueDate,
                dueDateError = if (dueDate != null && dueDate.isNotBlank()) {
                    try {
                        val date = LocalDate.parse(dueDate)
                        if (date.isBefore(LocalDate.now())) {
                            "Due date cannot be in the past"
                        } else null
                    } catch (e: Exception) {
                        "Invalid date format"
                    }
                } else null
            )
        }
    }

    private fun updateFormDueTime(dueTime: String?) {
        _formState.update { it.copy(dueTime = dueTime) }
    }

    private fun updateFormStartDate(startDate: String?) {
        _formState.update { it.copy(startDate = startDate) }
    }

    private fun updateFormStartTime(startTime: String?) {
        _formState.update { it.copy(startTime = startTime) }
    }

    private fun updateFormEndDate(endDate: String?) {
        _formState.update { it.copy(endDate = endDate) }
    }

    private fun updateFormEndTime(endTime: String?) {
        _formState.update { it.copy(endTime = endTime) }
    }

    private fun updateFormLocationName(location: String?) {
        _formState.update { it.copy(locationName = location) }
    }

    private fun updateFormLocationAddress(address: String?) {
        _formState.update { it.copy(locationAddress = address) }
    }

    private fun updateFormTags(tags: String) {
        _formState.update { it.copy(tags = tags) }
    }

    private fun updateFormNotes(notes: String) {
        _formState.update { it.copy(notes = notes) }
    }

    private fun updateFormCategory(categoryId: String?) {
        _formState.update { it.copy(categoryId = categoryId) }
    }

    private fun submitForm() {
        val state = _formState.value

        if (!state.isFormValid) {
            _formState.update { it.copy(errorMessage = "Please fix the errors above") }
            return
        }

        _formState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            val userId = currentUser?.id ?: "unknown"

            val tagsList = state.tags.split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() }

            val task = Task(
                id = state.id ?: UUID.randomUUID().toString(),
                userId = userId,
                title = state.title,
                description = state.description.takeIf { it.isNotBlank() },
                taskType = state.taskType,
                priorityLevel = state.priorityLevel,
                dueDate = state.dueDate,
                dueTime = state.dueTime,
                startDate = state.startDate,
                startTime = state.startTime,
                endDate = state.endDate,
                endTime = state.endTime,
                locationName = state.locationName,
                locationAddress = state.locationAddress,
                tags = tagsList,
                notes = state.notes.takeIf { it.isNotBlank() },
                categoryId = state.categoryId,
                isDeleted = false,
                createdAt = nowUtc(),
                updatedAt = nowUtc(),
                completedAt = null
            )

            if (state.isEditMode) {
                handleEvent(TaskEvent.UpdateTask(task))
            } else {
                handleEvent(TaskEvent.CreateTask(task))
            }
        }
    }

    private fun cancelForm() {
        navigateBack()
    }

    private fun dismissFormError() {
        _formState.update { it.copy(errorMessage = null) }
    }
}
