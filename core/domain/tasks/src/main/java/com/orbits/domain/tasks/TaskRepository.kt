package com.orbits.domain.tasks

import com.orbits.core.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Task repository interface.
 * Defines all task operations.
 * Implemented by :core:data:tasks.
 */
interface TaskRepository {

    // ─── Read Operations ──────────────────────────────────────────

    /**
     * Get all tasks for the current user.
     */
    fun getTasks(): Flow<List<Task>>

    /**
     * Get tasks by status.
     */
    fun getTasksByStatus(status: String): Flow<List<Task>>

    /**
     * Get tasks in a date range.
     */
    fun getTasksInDateRange(startDate: String, endDate: String): Flow<List<Task>>

    /**
     * Get a single task by ID.
     */
    suspend fun getTask(taskId: String): Result<Task>

    /**
     * Get overdue tasks.
     */
    fun getOverdueTasks(): Flow<List<Task>>

    /**
     * Get upcoming tasks (next 7 days).
     */
    fun getUpcomingTasks(): Flow<List<Task>>

    // ─── Write Operations ─────────────────────────────────────────

    /**
     * Create a new task.
     */
    suspend fun createTask(task: Task): Result<Task>

    /**
     * Update an existing task.
     */
    suspend fun updateTask(task: Task): Result<Task>

    /**
     * Mark a task as complete.
     */
    suspend fun completeTask(taskId: String): Result<Task>

    /**
     * Delete a task (soft delete).
     */
    suspend fun deleteTask(taskId: String): Result<Unit>

    /**
     * Restore a deleted task.
     */
    suspend fun restoreTask(taskId: String): Result<Task>

    // ─── Subtasks ─────────────────────────────────────────────────

    /**
     * Get subtasks for a task.
     */
    suspend fun getSubtasks(taskId: String): Result<List<Subtask>>

    /**
     * Create a new subtask.
     */
    suspend fun createSubtask(subtask: Subtask): Result<Subtask>

    /**
     * Complete a subtask.
     */
    suspend fun completeSubtask(subtaskId: String): Result<Subtask>

    // ─── Sync Operations ──────────────────────────────────────────

    /**
     * Synchronize tasks with the server.
     */
    suspend fun syncWithServer(): Result<Unit>
}