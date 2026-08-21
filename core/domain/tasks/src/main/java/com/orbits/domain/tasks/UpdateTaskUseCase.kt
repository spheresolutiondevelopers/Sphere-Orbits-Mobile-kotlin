package com.orbits.domain.tasks

import com.orbits.core.common.Result
import com.orbits.core.common.extensions.nowUtc
import javax.inject.Inject

/**
 * Use case to update an existing task.
 */
class UpdateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {

    /**
     * Execute the use case.
     * @param task The updated task
     * @return Result containing the updated task, or error
     */
    suspend operator fun invoke(task: Task): Result<Task> {
        // Validate task
        val validationResult = TaskValidation.validate(
            title = task.title,
            description = task.description,
            taskType = task.taskType,
            priorityLevel = task.priorityLevel,
            dueDate = task.dueDate,
            dueTime = task.dueTime,
            startDate = task.startDate,
            startTime = task.startTime,
            endDate = task.endDate,
            endTime = task.endTime,
            locationName = task.locationName,
            locationAddress = task.locationAddress,
            tags = task.tags,
            notes = task.notes,
            categoryId = task.categoryId
        )

        if (!validationResult.isValid) {
            return Result.Error(
                IllegalArgumentException(
                    validationResult.errors.joinToString { it.message }
                )
            )
        }

        return taskRepository.updateTask(task)
    }
}