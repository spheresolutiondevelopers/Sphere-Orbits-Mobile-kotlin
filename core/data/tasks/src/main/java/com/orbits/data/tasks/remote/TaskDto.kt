/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.data.tasks.remote

import kotlinx.serialization.Serializable

/**
 * Task DTO — API response/request model.
 * ISOLATED in :core:data:tasks. API schema changes only recompile this module.
 */
@Serializable
internal data class TaskDto(
    val id: String,
    val userId: String,
    val title: String,
    val description: String? = null,
    val taskType: String = "general",
    val priorityLevel: String = "medium",
    val status: String = "pending",
    val completionPercentage: Int = 0,
    val dueDate: String? = null,
    val dueTime: String? = null,
    val startDate: String? = null,
    val startTime: String? = null,
    val endDate: String? = null,
    val endTime: String? = null,
    val locationName: String? = null,
    val locationAddress: String? = null,
    val tags: String? = null,
    val notes: String? = null,
    val categoryId: String? = null,
    val isDeleted: Boolean = false,
    val createdAt: String,
    val updatedAt: String,
    val completedAt: String? = null
)

@Serializable
internal data class CreateTaskRequest(
    val title: String,
    val description: String? = null,
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
    val tags: String? = null,
    val notes: String? = null,
    val categoryId: String? = null,
    val estimatedDuration: Int? = null
)

@Serializable
internal data class UpdateTaskRequest(
    val title: String? = null,
    val description: String? = null,
    val taskType: String? = null,
    val priorityLevel: String? = null,
    val status: String? = null,
    val completionPercentage: Int? = null,
    val dueDate: String? = null,
    val dueTime: String? = null,
    val startDate: String? = null,
    val startTime: String? = null,
    val endDate: String? = null,
    val endTime: String? = null,
    val locationName: String? = null,
    val locationAddress: String? = null,
    val tags: String? = null,
    val notes: String? = null,
    val categoryId: String? = null
)

@Serializable
internal data class SubtaskDto(
    val id: String,
    val taskId: String,
    val title: String,
    val description: String? = null,
    val status: String = "pending",
    val priority: String = "medium",
    val dueDate: String? = null,
    val dueTime: String? = null,
    val completedAt: String? = null,
    val subtaskOrder: Int = 0,
    val isDeleted: Boolean = false,
    val createdAt: String,
    val updatedAt: String
)