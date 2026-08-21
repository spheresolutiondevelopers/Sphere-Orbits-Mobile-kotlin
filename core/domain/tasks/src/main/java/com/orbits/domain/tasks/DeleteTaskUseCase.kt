package com.orbits.domain.tasks

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to delete a task.
 */
class DeleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {

    /**
     * Execute the use case.
     * @param taskId The ID of the task to delete
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(taskId: String): Result<Unit> {
        if (taskId.isBlank()) {
            return Result.Error(IllegalArgumentException("Task ID cannot be empty"))
        }
        return taskRepository.deleteTask(taskId)
    }
}