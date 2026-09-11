package com.orbits.feature.tasks

import com.orbits.domain.tasks.Task

/**
 * UI events for the tasks feature.
 */
sealed class TaskEvent {
    // ─── List Events ─────────────────────────────────────────────

    data object LoadTasks : TaskEvent()
    data object Refresh : TaskEvent()
    data class SelectFilter(val filter: TaskFilter) : TaskEvent()
    data class Search(val query: String) : TaskEvent()
    data class SelectTask(val taskId: String) : TaskEvent()
    data object DismissError : TaskEvent()
    data object NavigateToCreate : TaskEvent()
    data class NavigateToEdit(val task: Task) : TaskEvent()
    data class NavigateToDetail(val taskId: String) : TaskEvent()
    data object NavigateBack : TaskEvent()

    // ─── CRUD Events ─────────────────────────────────────────────

    data class CreateTask(val task: Task) : TaskEvent()
    data class UpdateTask(val task: Task) : TaskEvent()
    data class CompleteTask(val taskId: String) : TaskEvent()
    data class DeleteTask(val taskId: String) : TaskEvent()
    data class RestoreTask(val taskId: String) : TaskEvent()

    // ─── Form Events ─────────────────────────────────────────────

    data class FormTitleChanged(val title: String) : TaskEvent()
    data class FormDescriptionChanged(val description: String) : TaskEvent()
    data class FormTaskTypeChanged(val taskType: String) : TaskEvent()
    data class FormPriorityChanged(val priority: String) : TaskEvent()
    data class FormDueDateChanged(val dueDate: String?) : TaskEvent()
    data class FormDueTimeChanged(val dueTime: String?) : TaskEvent()
    data class FormStartDateChanged(val startDate: String?) : TaskEvent()
    data class FormStartTimeChanged(val startTime: String?) : TaskEvent()
    data class FormEndDateChanged(val endDate: String?) : TaskEvent()
    data class FormEndTimeChanged(val endTime: String?) : TaskEvent()
    data class FormLocationNameChanged(val location: String?) : TaskEvent()
    data class FormLocationAddressChanged(val address: String?) : TaskEvent()
    data class FormTagsChanged(val tags: String) : TaskEvent()
    data class FormNotesChanged(val notes: String) : TaskEvent()
    data class FormCategoryChanged(val categoryId: String?) : TaskEvent()
    data object FormSubmit : TaskEvent()
    data object FormCancel : TaskEvent()
    data object FormDismissError : TaskEvent()
}
