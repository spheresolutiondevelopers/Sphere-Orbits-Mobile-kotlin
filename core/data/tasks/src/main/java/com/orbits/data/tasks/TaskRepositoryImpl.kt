/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.data.tasks

import com.orbits.core.common.Result
import com.orbits.core.common.Logger
import com.orbits.core.common.TokenProvider
import com.orbits.core.common.extensions.nowUtc
import com.orbits.core.database.dao.TaskDao
import com.orbits.core.database.dao.SyncQueueDao
import com.orbits.core.network.api.TaskApi
import com.orbits.core.network.error.ApiErrorParser
import com.orbits.data.sync.SyncQueueEntity
import com.orbits.data.tasks.local.TaskEntity
import com.orbits.data.tasks.local.SubtaskEntity
import com.orbits.data.tasks.mappers.TaskMapper
import com.orbits.data.tasks.mappers.SubtaskMapper
import com.orbits.domain.tasks.Task
import com.orbits.domain.tasks.Subtask
import com.orbits.domain.tasks.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Task Repository Implementation.
 * ISOLATED in :core:data:tasks. Changes here ONLY recompile this module.
 *
 * Implements TaskRepository interface from :core:domain:tasks.
 * All database and network operations are internal to this module.
 */
@Singleton
internal class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val syncQueueDao: SyncQueueDao,
    private val taskApi: TaskApi,
    private val taskMapper: TaskMapper,
    private val subtaskMapper: SubtaskMapper,
    private val tokenProvider: TokenProvider
) : TaskRepository {

    companion object {
        private const val TAG = "TaskRepository"
    }

    // ─── Read Operations ──────────────────────────────────────────

    override fun getTasks(): Flow<List<Task>> {
        return taskDao.getTasks(getUserId()).map { entities ->
            taskMapper.toDomainList(entities)
        }
    }

    override fun getTasksByStatus(status: String): Flow<List<Task>> {
        return taskDao.getTasksByStatus(getUserId(), status).map { entities ->
            taskMapper.toDomainList(entities)
        }
    }

    override fun getTasksInDateRange(startDate: String, endDate: String): Flow<List<Task>> {
        return taskDao.getTasksInDateRange(getUserId(), startDate, endDate).map { entities ->
            taskMapper.toDomainList(entities)
        }
    }

    override suspend fun getTask(taskId: String): Result<Task> {
        return try {
            val entity = taskDao.getTask(taskId)
            if (entity != null) {
                Result.Success(taskMapper.toDomain(entity))
            } else {
                Result.Error(IllegalStateException("Task not found: $taskId"))
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Error fetching task $taskId", e)
            Result.Error(e)
        }
    }

    override fun getOverdueTasks(): Flow<List<Task>> {
        return taskDao.getOverdueTasks(getUserId()).map { entities ->
            taskMapper.toDomainList(entities)
        }
    }

    override fun getUpcomingTasks(): Flow<List<Task>> {
        return taskDao.getUpcomingTasks(getUserId()).map { entities ->
            taskMapper.toDomainList(entities)
        }
    }

    // ─── Write Operations ─────────────────────────────────────────

    override suspend fun createTask(task: Task): Result<Task> {
        return try {
            validateTask(task)

            // Save locally first (offline-first)
            val entity = taskMapper.toEntity(task)
            taskDao.insertTask(entity)

            // Enqueue sync
            enqueueSync(SyncQueueEntity(
                entityType = "task",
                operation = "create",
                entityId = task.id,
                payloadJson = taskMapper.toCreateRequest(task).toString()
            ))

            Logger.d(TAG, "Task created locally: ${task.id}")
            Result.Success(task)
        } catch (e: Exception) {
            Logger.e(TAG, "Error creating task", e)
            Result.Error(e)
        }
    }

    override suspend fun updateTask(task: Task): Result<Task> {
        return try {
            validateTask(task)

            // Update locally
            val entity = taskMapper.toEntity(task).copy(updatedAt = nowUtc())
            taskDao.updateTask(entity)

            // Enqueue sync
            enqueueSync(SyncQueueEntity(
                entityType = "task",
                operation = "update",
                entityId = task.id,
                payloadJson = taskMapper.toUpdateRequest(task).toString()
            ))

            Logger.d(TAG, "Task updated locally: ${task.id}")
            Result.Success(task)
        } catch (e: Exception) {
            Logger.e(TAG, "Error updating task ${task.id}", e)
            Result.Error(e)
        }
    }

    override suspend fun completeTask(taskId: String): Result<Task> {
        return try {
            // Get current task
            val entity = taskDao.getTask(taskId)
            if (entity == null) {
                return Result.Error(IllegalStateException("Task not found: $taskId"))
            }

            // Mark complete locally
            taskDao.completeTask(taskId)

            // Enqueue sync
            enqueueSync(SyncQueueEntity(
                entityType = "task",
                operation = "update",
                entityId = taskId,
                payloadJson = """{"status":"completed"}"""
            ))

            // Fetch updated task
            val updatedEntity = taskDao.getTask(taskId)
            if (updatedEntity != null) {
                Result.Success(taskMapper.toDomain(updatedEntity))
            } else {
                Result.Error(IllegalStateException("Task not found after completion"))
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Error completing task $taskId", e)
            Result.Error(e)
        }
    }

    override suspend fun deleteTask(taskId: String): Result<Unit> {
        return try {
            // Soft delete locally
            taskDao.softDeleteTask(taskId)

            // Enqueue sync
            enqueueSync(SyncQueueEntity(
                entityType = "task",
                operation = "delete",
                entityId = taskId,
                payloadJson = null
            ))

            Logger.d(TAG, "Task deleted locally: $taskId")
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Error deleting task $taskId", e)
            Result.Error(e)
        }
    }

    override suspend fun restoreTask(taskId: String): Result<Task> {
        return try {
            taskDao.restoreTask(taskId)

            val entity = taskDao.getTask(taskId)
            if (entity != null) {
                enqueueSync(SyncQueueEntity(
                    entityType = "task",
                    operation = "update",
                    entityId = taskId,
                    payloadJson = taskMapper.toUpdateRequest(taskMapper.toDomain(entity)).toString()
                ))
                Result.Success(taskMapper.toDomain(entity))
            } else {
                Result.Error(IllegalStateException("Task not found"))
            }
        } catch (e: Exception) {
            Logger.e(TAG, "Error restoring task $taskId", e)
            Result.Error(e)
        }
    }

    // ─── Subtasks ──────────────────────────────────────────────────

    override suspend fun getSubtasks(taskId: String): Result<List<Subtask>> {
        return try {
            val entities = taskDao.getSubtasksForTask(taskId)
            Result.Success(subtaskMapper.toDomainList(entities))
        } catch (e: Exception) {
            Logger.e(TAG, "Error fetching subtasks for task $taskId", e)
            Result.Error(e)
        }
    }

    override suspend fun createSubtask(subtask: Subtask): Result<Subtask> {
        return try {
            val entity = subtaskMapper.toEntity(subtask)
            taskDao.insertSubtask(entity)

            enqueueSync(SyncQueueEntity(
                entityType = "subtask",
                operation = "create",
                entityId = subtask.id,
                payloadJson = """{"taskId":"${subtask.taskId}","title":"${subtask.title}"}"""
            ))

            Result.Success(subtask)
        } catch (e: Exception) {
            Logger.e(TAG, "Error creating subtask", e)
            Result.Error(e)
        }
    }

    override suspend fun completeSubtask(subtaskId: String): Result<Subtask> {
        return try {
            taskDao.completeSubtask(subtaskId)

            enqueueSync(SyncQueueEntity(
                entityType = "subtask",
                operation = "update",
                entityId = subtaskId,
                payloadJson = """{"status":"completed"}"""
            ))

            // Fetch updated subtask (simplified - would need to query)
            Result.Success(Subtask(
                id = subtaskId,
                taskId = "",
                title = "",
                status = "completed",
                createdAt = nowUtc(),
                updatedAt = nowUtc()
            ))
        } catch (e: Exception) {
            Logger.e(TAG, "Error completing subtask $subtaskId", e)
            Result.Error(e)
        }
    }

    // ─── Sync Operations ───────────────────────────────────────────

    override suspend fun syncWithServer(): Result<Unit> {
        return try {
            val token = tokenProvider.getAccessToken()
            if (token == null) {
                return Result.Error(IllegalStateException("Not authenticated"))
            }

            // Get unsynced tasks
            val unsynced = taskDao.getUnsyncedTasks()
            if (unsynced.isNotEmpty()) {
                // Push each unsynced item
                // This is simplified; full sync would batch operations
                // and handle conflict resolution
            }

            // Pull latest from server
            // This would call taskApi.getTasks() and update local cache

            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Sync error", e)
            Result.Error(e)
        }
    }

    // ─── Private Helpers ──────────────────────────────────────────

    private fun getUserId(): String {
        // This should come from auth state
        // For production, use a real user ID from AuthManager
        return "test_user_id"
    }

    private suspend fun enqueueSync(item: SyncQueueEntity) {
        syncQueueDao.enqueue(item)
    }

    private fun validateTask(task: Task) {
        require(task.title.isNotBlank()) { "Task title cannot be empty" }
        require(task.title.length <= 255) { "Task title must be <= 255 characters" }
        require(task.completionPercentage in 0..100) { "Completion must be 0-100" }
        // Additional validation as needed
    }
}