package com.orbits.domain.tasks

/**
 * Pure domain model for a subtask.
 */
data class Subtask(
    val id: String,
    val taskId: String,
    val title: String,
    val description: String? = null,
    val status: String = "pending", // pending, in_progress, completed, cancelled
    val priority: String = "medium", // low, medium, high
    val dueDate: String? = null,
    val dueTime: String? = null,
    val completedAt: String? = null,
    val subtaskOrder: Int = 0,
    val isDeleted: Boolean = false,
    val createdAt: String,
    val updatedAt: String
) {

    /**
     * Check if the subtask is completed.
     */
    fun isCompleted(): Boolean = status == "completed"

    /**
     * Get the priority value for sorting.
     */
    fun getPriorityValue(): Int {
        return when (priority) {
            "low" -> 1
            "medium" -> 2
            "high" -> 3
            else -> 0
        }
    }
}