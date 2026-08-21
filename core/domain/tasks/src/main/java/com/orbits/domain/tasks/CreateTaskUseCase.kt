package com.orbits.domain.tasks

import com.orbits.core.common.Result
import com.orbits.core.common.extensions.nowUtc
import java.util.UUID
import javax.inject.Inject

/**
 * Use case to create a new task.
 */
class CreateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {

    /**
     * Execute the use case.
     * @param title Task title
     * @param description Optional description
     * @param taskType Task type
     * @param priorityLevel Priority level
     * @param dueDate Optional due date
     * @param dueTime Optional due time
     * @param startDate Optional start date
     * @param startTime Optional start time
     * @param endDate Optional end date
     * @param endTime Optional end time
     * @param locationName Optional location name
     * @param locationAddress Optional location address
     * @param tags Optional tags
     * @param notes Optional notes
     * @param categoryId Optional category ID
     * @param userId User ID (if null, uses current user)
     * @return Result containing the created task, or error
     */
    suspend operator fun invoke(
        title: String,
        description: String? = null,
        taskType: String = "general",
        priorityLevel: String = "medium",
        dueDate: String? = null,
        dueTime: String? = null,
        startDate: String? = null,
        startTime: String? = null,
        endDate: String? = null,
        endTime: String? = null,
        locationName: String? = null,
        locationAddress: String? = null,
        tags: List<String> = emptyList(),
        notes: String? = null,
        categoryId: String? = null,
        userId: String? = null
    ): Result<Task> {
        // Validate input
        val validationResult = TaskValidation.validate(
            title = title,
            description = description,
            taskType = taskType,
            priorityLevel = priorityLevel,
            dueDate = dueDate,
            dueTime = dueTime,
            startDate = startDate,
            startTime = startTime,
            endDate = endDate,
            endTime = endTime,
            locationName = locationName,
            locationAddress = locationAddress,
            tags = tags,
            notes = notes,
            categoryId = categoryId
        )

        if (!validationResult.isValid) {
            return Result.Error(
                IllegalArgumentException(
                    validationResult.errors.joinToString { it.message }
                )
            )
        }

        // Create task
        val now = nowUtc()
        val task = Task(
            id = UUID.randomUUID().toString(),
            userId = userId ?: getCurrentUserId(),
            title = title,
            description = description,
            taskType = taskType,
            priorityLevel = priorityLevel,
            status = "pending",
            completionPercentage = 0,
            dueDate = dueDate,
            dueTime = dueTime,
            startDate = startDate,
            startTime = startTime,
            endDate = endDate,
            endTime = endTime,
            locationName = locationName,
            locationAddress = locationAddress,
            tags = tags,
            notes = notes,
            categoryId = categoryId,
            isDeleted = false,
            createdAt = now,
            updatedAt = now,
            completedAt = null
        )

        return taskRepository.createTask(task)
    }

    // This should come from auth state
    private fun getCurrentUserId(): String {
        return "test_user_id"
    }
}