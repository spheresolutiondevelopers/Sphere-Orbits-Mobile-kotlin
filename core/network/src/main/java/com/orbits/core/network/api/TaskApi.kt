/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.core.network.api

import com.orbits.core.model.PaginatedResponse
import com.orbits.core.model.PaginationRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

// DTOs for tasks
@kotlinx.serialization.Serializable
data class TaskDto(
    val id: String,
    val title: String,
    val description: String?,
    val taskType: String,
    val priorityLevel: String,
    val status: String,
    val completionPercentage: Int,
    val dueDate: String?,
    val dueTime: String?,
    val startDate: String?,
    val startTime: String?,
    val endDate: String?,
    val endTime: String?,
    val locationName: String?,
    val locationAddress: String?,
    val tags: String?,
    val notes: String?,
    val categoryId: String?,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String
)

@kotlinx.serialization.Serializable
data class CreateTaskRequest(
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
    val estimatedDuration: Int? = null // in minutes
)

@kotlinx.serialization.Serializable
data class UpdateTaskRequest(
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

interface TaskApi {

    @GET("api/v1/tasks")
    suspend fun getTasks(
        @Query("category") category: String? = null,
        @Query("status") status: String? = null,
        @Query("priority") priority: String? = null,
        @Query("dueDate") dueDate: String? = null,
        @Query("search") search: String? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): PaginatedResponse<TaskDto>

    @GET("api/v1/tasks/{taskId}")
    suspend fun getTask(
        @Path("taskId") taskId: String
    ): TaskDto

    @POST("api/v1/tasks")
    suspend fun createTask(
        @Body request: CreateTaskRequest
    ): TaskDto

    @PUT("api/v1/tasks/{taskId}")
    suspend fun updateTask(
        @Path("taskId") taskId: String,
        @Body request: UpdateTaskRequest
    ): TaskDto

    @DELETE("api/v1/tasks/{taskId}")
    suspend fun deleteTask(
        @Path("taskId") taskId: String
    )

    @POST("api/v1/tasks/{taskId}/complete")
    suspend fun completeTask(
        @Path("taskId") taskId: String
    ): TaskDto

    @POST("api/v1/tasks/{taskId}/restore")
    suspend fun restoreTask(
        @Path("taskId") taskId: String
    ): TaskDto
}