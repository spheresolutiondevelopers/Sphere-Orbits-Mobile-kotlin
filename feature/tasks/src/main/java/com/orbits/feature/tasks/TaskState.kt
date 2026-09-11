package com.orbits.feature.tasks

import com.orbits.domain.tasks.Task

/**
 * UI state for the tasks feature.
 */
data class TasksUiState(
    val isLoading: Boolean = true,
    val tasks: List<Task> = emptyList(),
    val filteredTasks: List<Task> = emptyList(),
    val selectedFilter: TaskFilter = TaskFilter.ALL,
    val searchQuery: String = "",
    val selectedTaskId: String? = null,
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false,
    val isCreateMode: Boolean = false,
    val isEditMode: Boolean = false,
    val editingTask: Task? = null,
    
    // Counts for filters
    val categoryCounts: Map<String, Int> = emptyMap(),
    val priorityCounts: Map<String, Int> = emptyMap(),
    val dateCounts: Map<String, Int> = emptyMap(),
    val progressCounts: Map<String, Int> = emptyMap()
)

/**
 * Task filter options.
 */
enum class TaskFilter(val displayName: String) {
    ALL("All"),
    TODAY("Today"),
    UPCOMING("Upcoming"),
    OVERDUE("Overdue"),
    COMPLETED("Completed"),
    IN_PROGRESS("In Progress"),
    HIGH_PRIORITY("High Priority"),
    CRITICAL("Critical")
}

/**
 * Task sort options.
 */
enum class TaskSort(val displayName: String) {
    DUE_DATE_ASC("Due Date (Earliest)"),
    DUE_DATE_DESC("Due Date (Latest)"),
    PRIORITY_DESC("Priority (Highest)"),
    CREATED_DESC("Created (Newest)"),
    CREATED_ASC("Created (Oldest)"),
    TITLE_ASC("Title (A-Z)"),
    TITLE_DESC("Title (Z-A)")
}

/**
 * Task form UI state.
 */
data class TaskFormUiState(
    val id: String? = null,
    val title: String = "",
    val description: String = "",
    val taskType: String = "general",
    val priorityLevel: String = "medium",
    val dueDate: String? = null,
    val dueTime: String? = null,
    val startDate: String? = null,
    val startTime: String? = null,
    val endDate: String? = null,
    val endTime: String? = null,
    val locationName: String? = null,
    val locationAddress: String? = null,
    val tags: String = "",
    val notes: String = "",
    val categoryId: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val titleError: String? = null,
    val dueDateError: String? = null,
    val isEditMode: Boolean = false
) {
    val isFormValid: Boolean
        get() = title.isNotBlank() && titleError == null && dueDateError == null
}
