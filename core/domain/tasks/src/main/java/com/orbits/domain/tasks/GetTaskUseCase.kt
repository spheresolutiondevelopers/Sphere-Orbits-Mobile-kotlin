package com.orbits.domain.tasks

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to get a single task by ID.
 */
class GetTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {

    /**
     * Execute the use case.
     * @param taskId The ID of the task to retrieve
     * @return Result containing the task, or error
     */
    suspend operator fun invoke(taskId: String): Result<Task> {
        if (taskId.isBlank()) {
            return Result.Error(IllegalArgumentException("Task ID cannot be empty"))
        }
        return taskRepository.getTask(taskId)
    }
}