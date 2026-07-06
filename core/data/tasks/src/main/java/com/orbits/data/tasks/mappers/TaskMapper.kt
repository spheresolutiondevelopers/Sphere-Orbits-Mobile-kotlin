/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.data.tasks.mappers

import com.orbits.data.tasks.local.TaskEntity
import com.orbits.data.tasks.local.SubtaskEntity
import com.orbits.data.tasks.remote.TaskDto
import com.orbits.data.tasks.remote.SubtaskDto
import com.orbits.data.tasks.remote.CreateTaskRequest
import com.orbits.data.tasks.remote.UpdateTaskRequest
import com.orbits.domain.tasks.Task
import com.orbits.domain.tasks.Subtask
import com.orbits.core.common.extensions.nowUtc
import javax.inject.Inject

/**
 * Task Mapper — converts between Entity, DTO, and Domain models.
 * ISOLATED in :core:data:tasks. Changes only recompile this module.
 */
internal class TaskMapper @Inject constructor() {

    // ─── Entity ↔ Domain ─────────────────────────────────────────

    fun toDomain(entity: TaskEntity): Task {
        return Task(
            id = entity.id,
            userId = entity.userId,
            title = entity.title,
            description = entity.description,
            taskType = entity.taskType,
            priorityLevel = entity.priorityLevel,
            status = entity.status,
            completionPercentage = entity.completionPercentage,
            dueDate = entity.dueDate,
            dueTime = entity.dueTime,
            startDate = entity.startDate,
            startTime = entity.startTime,
            endDate = entity.endDate,
            endTime = entity.endTime,
            locationName = entity.locationName,
            locationAddress = entity.locationAddress,
            tags = entity.tags?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
            notes = entity.notes,
            categoryId = entity.categoryId,
            isDeleted = entity.isDeleted,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            completedAt = entity.completedAt
        )
    }

    fun toEntity(domain: Task): TaskEntity {
        return TaskEntity(
            id = domain.id,
            userId = domain.userId,
            title = domain.title,
            description = domain.description,
            taskType = domain.taskType,
            priorityLevel = domain.priorityLevel,
            status = domain.status,
            completionPercentage = domain.completionPercentage,
            dueDate = domain.dueDate,
            dueTime = domain.dueTime,
            startDate = domain.startDate,
            startTime = domain.startTime,
            endDate = domain.endDate,
            endTime = domain.endTime,
            locationName = domain.locationName,
            locationAddress = domain.locationAddress,
            tags = domain.tags.joinToString(","),
            notes = domain.notes,
            categoryId = domain.categoryId,
            isDeleted = domain.isDeleted,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            syncedAt = null,
            syncStatus = "synced",
            completedAt = domain.completedAt
        )
    }

    // ─── DTO ↔ Entity ──────────────────────────────────────────────

    fun toEntity(dto: TaskDto): TaskEntity {
        return TaskEntity(
            id = dto.id,
            userId = dto.userId,
            title = dto.title,
            description = dto.description,
            taskType = dto.taskType,
            priorityLevel = dto.priorityLevel,
            status = dto.status,
            completionPercentage = dto.completionPercentage,
            dueDate = dto.dueDate,
            dueTime = dto.dueTime,
            startDate = dto.startDate,
            startTime = dto.startTime,
            endDate = dto.endDate,
            endTime = dto.endTime,
            locationName = dto.locationName,
            locationAddress = dto.locationAddress,
            tags = dto.tags,
            notes = dto.notes,
            categoryId = dto.categoryId,
            isDeleted = dto.isDeleted,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,
            syncedAt = null,
            syncStatus = "synced",
            completedAt = dto.completedAt
        )
    }

    fun toDto(entity: TaskEntity): TaskDto {
        return TaskDto(
            id = entity.id,
            userId = entity.userId,
            title = entity.title,
            description = entity.description,
            taskType = entity.taskType,
            priorityLevel = entity.priorityLevel,
            status = entity.status,
            completionPercentage = entity.completionPercentage,
            dueDate = entity.dueDate,
            dueTime = entity.dueTime,
            startDate = entity.startDate,
            startTime = entity.startTime,
            endDate = entity.endDate,
            endTime = entity.endTime,
            locationName = entity.locationName,
            locationAddress = entity.locationAddress,
            tags = entity.tags,
            notes = entity.notes,
            categoryId = entity.categoryId,
            isDeleted = entity.isDeleted,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            completedAt = entity.completedAt
        )
    }

    // ─── Create/Update Requests ────────────────────────────────────

    fun toCreateRequest(domain: Task): CreateTaskRequest {
        return CreateTaskRequest(
            title = domain.title,
            description = domain.description,
            taskType = domain.taskType,
            priorityLevel = domain.priorityLevel,
            dueDate = domain.dueDate,
            dueTime = domain.dueTime,
            startDate = domain.startDate,
            startTime = domain.startTime,
            endDate = domain.endDate,
            endTime = domain.endTime,
            locationName = domain.locationName,
            locationAddress = domain.locationAddress,
            tags = domain.tags.joinToString(","),
            notes = domain.notes,
            categoryId = domain.categoryId
        )
    }

    fun toUpdateRequest(domain: Task): UpdateTaskRequest {
        return UpdateTaskRequest(
            title = domain.title,
            description = domain.description,
            taskType = domain.taskType,
            priorityLevel = domain.priorityLevel,
            status = domain.status,
            completionPercentage = domain.completionPercentage,
            dueDate = domain.dueDate,
            dueTime = domain.dueTime,
            startDate = domain.startDate,
            startTime = domain.startTime,
            endDate = domain.endDate,
            endTime = domain.endTime,
            locationName = domain.locationName,
            locationAddress = domain.locationAddress,
            tags = domain.tags.joinToString(","),
            notes = domain.notes,
            categoryId = domain.categoryId
        )
    }

    // ─── DTO ↔ Domain ──────────────────────────────────────────────

    fun toDomain(dto: TaskDto): Task {
        return Task(
            id = dto.id,
            userId = dto.userId,
            title = dto.title,
            description = dto.description,
            taskType = dto.taskType,
            priorityLevel = dto.priorityLevel,
            status = dto.status,
            completionPercentage = dto.completionPercentage,
            dueDate = dto.dueDate,
            dueTime = dto.dueTime,
            startDate = dto.startDate,
            startTime = dto.startTime,
            endDate = dto.endDate,
            endTime = dto.endTime,
            locationName = dto.locationName,
            locationAddress = dto.locationAddress,
            tags = dto.tags?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
            notes = dto.notes,
            categoryId = dto.categoryId,
            isDeleted = dto.isDeleted,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,
            completedAt = dto.completedAt
        )
    }

    // ─── List Mappings ─────────────────────────────────────────────

    fun toDomainList(entities: List<TaskEntity>): List<Task> {
        return entities.map { toDomain(it) }
    }

    fun toEntityList(domains: List<Task>): List<TaskEntity> {
        return domains.map { toEntity(it) }
    }

    fun toDomainListFromDto(dtos: List<TaskDto>): List<Task> {
        return dtos.map { toDomain(it) }
    }

    fun toEntityListFromDto(dtos: List<TaskDto>): List<TaskEntity> {
        return dtos.map { toEntity(it) }
    }
}

/**
 * Subtask Mapper — follows the same pattern.
 */
internal class SubtaskMapper @Inject constructor() {

    fun toDomain(entity: SubtaskEntity): Subtask {
        return Subtask(
            id = entity.id,
            taskId = entity.taskId,
            title = entity.title,
            description = entity.description,
            status = entity.status,
            priority = entity.priority,
            dueDate = entity.dueDate,
            dueTime = entity.dueTime,
            completedAt = entity.completedAt,
            subtaskOrder = entity.subtaskOrder,
            isDeleted = entity.isDeleted,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toEntity(domain: Subtask): SubtaskEntity {
        return SubtaskEntity(
            id = domain.id,
            taskId = domain.taskId,
            title = domain.title,
            description = domain.description,
            status = domain.status,
            priority = domain.priority,
            dueDate = domain.dueDate,
            dueTime = domain.dueTime,
            completedAt = domain.completedAt,
            subtaskOrder = domain.subtaskOrder,
            isDeleted = domain.isDeleted,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }

    fun toDomain(dto: SubtaskDto): Subtask {
        return Subtask(
            id = dto.id,
            taskId = dto.taskId,
            title = dto.title,
            description = dto.description,
            status = dto.status,
            priority = dto.priority,
            dueDate = dto.dueDate,
            dueTime = dto.dueTime,
            completedAt = dto.completedAt,
            subtaskOrder = dto.subtaskOrder,
            isDeleted = dto.isDeleted,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    fun toEntity(dto: SubtaskDto): SubtaskEntity {
        return SubtaskEntity(
            id = dto.id,
            taskId = dto.taskId,
            title = dto.title,
            description = dto.description,
            status = dto.status,
            priority = dto.priority,
            dueDate = dto.dueDate,
            dueTime = dto.dueTime,
            completedAt = dto.completedAt,
            subtaskOrder = dto.subtaskOrder,
            isDeleted = dto.isDeleted,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    fun toDto(entity: SubtaskEntity): SubtaskDto {
        return SubtaskDto(
            id = entity.id,
            taskId = entity.taskId,
            title = entity.title,
            description = entity.description,
            status = entity.status,
            priority = entity.priority,
            dueDate = entity.dueDate,
            dueTime = entity.dueTime,
            completedAt = entity.completedAt,
            subtaskOrder = entity.subtaskOrder,
            isDeleted = entity.isDeleted,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toDomainList(entities: List<SubtaskEntity>): List<Subtask> {
        return entities.map { toDomain(it) }
    }

    fun toEntityList(domains: List<Subtask>): List<SubtaskEntity> {
        return domains.map { toEntity(it) }
    }
}