package com.orbits.domain.tasks

/**
 * Pure domain model for a task.
 * Contains NO Android dependencies — safe for KMP.
 */
data class Task(
    val id: String,
    val userId: String,
    val title: String,
    val description: String? = null,
    val taskType: String = "general", // general, meeting, reminder, deadline, event
    val priorityLevel: String = "medium", // low, medium, high, critical
    val status: String = "pending", // pending, in_progress, completed, cancelled, deferred
    val completionPercentage: Int = 0,
    val dueDate: String? = null,
    val dueTime: String? = null,
    val startDate: String? = null,
    val startTime: String? = null,
    val endDate: String? = null,
    val endTime: String? = null,
    val locationName: String? = null,
    val locationAddress: String? = null,
    val tags: List<String> = emptyList(),
    val notes: String? = null,
    val categoryId: String? = null,
    val isDeleted: Boolean = false,
    val createdAt: String,
    val updatedAt: String,
    val completedAt: String? = null
) {

    /**
     * Check if the task is completed.
     */
    fun isCompleted(): Boolean = status == "completed"

    /**
     * Check if the task is overdue.
     */
    fun isOverdue(): Boolean {
        if (isCompleted()) return false
        if (dueDate == null) return false
        return try {
            val due = java.time.LocalDate.parse(dueDate)
            val now = java.time.LocalDate.now()
            due.isBefore(now)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Check if the task is urgent (high or critical priority).
     */
    fun isUrgent(): Boolean = priorityLevel in listOf("high", "critical")

    /**
     * Get the task's due date as a formatted string.
     */
    fun getFormattedDueDate(): String? {
        if (dueDate == null) return null
        return try {
            val date = java.time.LocalDate.parse(dueDate)
            java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")
                .format(date)
        } catch (e: Exception) {
            dueDate
        }
    }

    /**
     * Get the task's priority level as a numeric value.
     */
    fun getPriorityValue(): Int {
        return when (priorityLevel) {
            "low" -> 1
            "medium" -> 2
            "high" -> 3
            "critical" -> 4
            else -> 0
        }
    }

    /**
     * Get the task's status label.
     */
    fun getStatusLabel(): String {
        return when (status) {
            "pending" -> "Pending"
            "in_progress" -> "In Progress"
            "completed" -> "Completed"
            "cancelled" -> "Cancelled"
            "deferred" -> "Deferred"
            else -> status.replaceFirstChar { it.uppercase() }
        }
    }
}