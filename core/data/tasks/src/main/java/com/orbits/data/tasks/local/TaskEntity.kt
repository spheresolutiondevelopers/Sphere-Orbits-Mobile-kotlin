/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.data.tasks.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.orbits.data.tasks.remote.TaskDto

/**
 * Task Entity — Room database representation.
 * ISOLATED in :core:data:tasks. Changes here ONLY recompile this module.
 * 
 * All fields mirror the TaskDto but with database-specific annotations.
 */
@Entity(
    tableName = "tasks",
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["user_id", "status"]),
        Index(value = ["due_date"]),
        Index(value = ["updated_at"]),
        Index(value = ["is_deleted"]),
        Index(value = ["user_id", "status", "due_date"])
    ]
)
internal data class TaskEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "task_type")
    val taskType: String = "general",

    @ColumnInfo(name = "priority_level")
    val priorityLevel: String = "medium",

    @ColumnInfo(name = "status")
    val status: String = "pending",

    @ColumnInfo(name = "completion_percentage")
    val completionPercentage: Int = 0,

    @ColumnInfo(name = "due_date")
    val dueDate: String? = null,

    @ColumnInfo(name = "due_time")
    val dueTime: String? = null,

    @ColumnInfo(name = "start_date")
    val startDate: String? = null,

    @ColumnInfo(name = "start_time")
    val startTime: String? = null,

    @ColumnInfo(name = "end_date")
    val endDate: String? = null,

    @ColumnInfo(name = "end_time")
    val endTime: String? = null,

    @ColumnInfo(name = "location_name")
    val locationName: String? = null,

    @ColumnInfo(name = "location_address")
    val locationAddress: String? = null,

    @ColumnInfo(name = "tags")
    val tags: String? = null,

    @ColumnInfo(name = "notes")
    val notes: String? = null,

    @ColumnInfo(name = "category_id")
    val categoryId: String? = null,

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "updated_at")
    val updatedAt: String,

    @ColumnInfo(name = "synced_at")
    val syncedAt: String? = null,

    @ColumnInfo(name = "sync_status")
    val syncStatus: String = "synced",

    @ColumnInfo(name = "completed_at")
    val completedAt: String? = null
)

/**
 * Subtask Entity — child of TaskEntity.
 * Changes here only recompile :core:data:tasks.
 */
@Entity(
    tableName = "subtasks",
    indices = [
        Index(value = ["task_id"]),
        Index(value = ["task_id", "status"])
    ]
)
internal data class SubtaskEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "task_id")
    val taskId: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "status")
    val status: String = "pending",

    @ColumnInfo(name = "priority")
    val priority: String = "medium",

    @ColumnInfo(name = "due_date")
    val dueDate: String? = null,

    @ColumnInfo(name = "due_time")
    val dueTime: String? = null,

    @ColumnInfo(name = "completed_at")
    val completedAt: String? = null,

    @ColumnInfo(name = "subtask_order")
    val subtaskOrder: Int = 0,

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "updated_at")
    val updatedAt: String
)