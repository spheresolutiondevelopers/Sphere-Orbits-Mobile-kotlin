package com.orbits.domain.tasks

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to mark a task as complete.
 */
class CompleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {

    /**
     * Execute the use case.
     * @param taskId The ID of the task to complete
     * @return Result containing the completed task, or error
     */
    suspend operator fun invoke(taskId: String): Result<Task> {
        if (taskId.isBlank()) {
            return Result.Error(IllegalArgumentException("Task ID cannot be empty"))
        }
        return taskRepository.completeTask(taskId)
    }
}