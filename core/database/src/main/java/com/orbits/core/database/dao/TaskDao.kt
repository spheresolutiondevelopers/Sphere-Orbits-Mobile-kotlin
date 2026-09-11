/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.orbits.data.tasks.TaskEntity
import com.orbits.data.tasks.SubtaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    // ─── Task Queries ────────────────────────────────────────────

    @Query("SELECT * FROM tasks WHERE id = :taskId AND is_deleted = 0")
    suspend fun getTask(taskId: String): TaskEntity?

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskIncludingDeleted(taskId: String): TaskEntity?

    @Query("SELECT * FROM tasks WHERE user_id = :userId AND is_deleted = 0 ORDER BY due_date ASC")
    fun getTasks(userId: String): Flow<List<TaskEntity>>

    @Query("""
        SELECT * FROM tasks 
        WHERE user_id = :userId 
          AND is_deleted = 0 
          AND status = :status 
        ORDER BY due_date ASC
    """)
    fun getTasksByStatus(userId: String, status: String): Flow<List<TaskEntity>>

    @Query("""
        SELECT * FROM tasks 
        WHERE user_id = :userId 
          AND is_deleted = 0 
          AND due_date >= :startDate 
          AND due_date <= :endDate
        ORDER BY due_date ASC
    """)
    fun getTasksInDateRange(
        userId: String,
        startDate: String,
        endDate: String
    ): Flow<List<TaskEntity>>

    @Query("""
        SELECT * FROM tasks 
        WHERE user_id = :userId 
          AND is_deleted = 0 
          AND due_date < date('now')
          AND status != 'completed'
          AND status != 'cancelled'
        ORDER BY due_date ASC
    """)
    fun getOverdueTasks(userId: String): Flow<List<TaskEntity>>

    @Query("""
        SELECT * FROM tasks 
        WHERE user_id = :userId 
          AND is_deleted = 0 
          AND due_date BETWEEN date('now') AND date('now', '+7 days')
          AND status != 'completed'
          AND status != 'cancelled'
        ORDER BY due_date ASC
    """)
    fun getUpcomingTasks(userId: String): Flow<List<TaskEntity>>

    @Query("SELECT COUNT(*) FROM tasks WHERE user_id = :userId AND is_deleted = 0")
    suspend fun getTaskCount(userId: String): Int

    @Query("""
        SELECT COUNT(*) FROM tasks 
        WHERE user_id = :userId 
          AND is_deleted = 0 
          AND status = 'completed'
          AND completed_at >= datetime('now', '-7 days')
    """)
    fun getCompletedCountLast7Days(userId: String): Flow<Int>

    @Query("""
        SELECT COUNT(*) FROM tasks 
        WHERE user_id = :userId 
          AND is_deleted = 0 
          AND status != 'completed'
          AND status != 'cancelled'
    """)
    fun getActiveCount(userId: String): Flow<Int>

    // ─── Task CRUD ───────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTaskIfNotExists(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("UPDATE tasks SET status = 'completed', completed_at = datetime('now'), updated_at = datetime('now') WHERE id = :taskId")
    suspend fun completeTask(taskId: String)

    @Query("UPDATE tasks SET status = 'pending', completed_at = NULL, updated_at = datetime('now') WHERE id = :taskId")
    suspend fun uncompleteTask(taskId: String)

    @Query("UPDATE tasks SET is_deleted = 1, updated_at = datetime('now') WHERE id = :taskId")
    suspend fun softDeleteTask(taskId: String)

    @Query("UPDATE tasks SET is_deleted = 0, updated_at = datetime('now') WHERE id = :taskId")
    suspend fun restoreTask(taskId: String)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun permanentlyDeleteTask(taskId: String)

    @Query("""
        UPDATE tasks 
        SET synced_at = datetime('now') 
        WHERE id = :taskId
    """)
    suspend fun markTaskSynced(taskId: String)

    @Query("""
        SELECT * FROM tasks 
        WHERE synced_at IS NULL OR synced_at < updated_at
    """)
    suspend fun getUnsyncedTasks(): List<TaskEntity>

    // ─── Subtasks ─────────────────────────────────────────────────

    @Query("SELECT * FROM subtasks WHERE task_id = :taskId AND is_deleted = 0 ORDER BY subtask_order ASC")
    suspend fun getSubtasksForTask(taskId: String): List<SubtaskEntity>

    @Query("SELECT * FROM subtasks WHERE task_id = :taskId AND status = 'pending' AND is_deleted = 0")
    suspend fun getPendingSubtasksForTask(taskId: String): List<SubtaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubtask(subtask: SubtaskEntity)

    @Update
    suspend fun updateSubtask(subtask: SubtaskEntity)

    @Query("UPDATE subtasks SET status = 'completed', completed_at = datetime('now'), updated_at = datetime('now') WHERE id = :subtaskId")
    suspend fun completeSubtask(subtaskId: String)

    @Query("UPDATE subtasks SET is_deleted = 1, updated_at = datetime('now') WHERE id = :subtaskId")
    suspend fun softDeleteSubtask(subtaskId: String)

    @Query("DELETE FROM subtasks WHERE task_id = :taskId")
    suspend fun deleteAllSubtasksForTask(taskId: String)

    // ─── Transaction ─────────────────────────────────────────────

    @Transaction
    suspend fun insertTaskWithSubtasks(task: TaskEntity, subtasks: List<SubtaskEntity>) {
        insertTask(task)
        subtasks.forEach { insertSubtask(it) }
    }

    @Transaction
    suspend fun softDeleteTaskWithSubtasks(taskId: String) {
        softDeleteTask(taskId)
        // Don't soft-delete subtasks automatically; we cascade manually
        // or let them be handled by the data layer.
    }
}