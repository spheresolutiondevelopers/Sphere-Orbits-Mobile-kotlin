package com.orbits.domain.tasks

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get tasks by status.
 */
class GetTasksByStatusUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {

    /**
     * Execute the use case.
     * @param status The status to filter by (pending, in_progress, completed, cancelled, deferred)
     * @return Flow emitting the filtered list of tasks
     */
    operator fun invoke(status: String): Flow<List<Task>> {
        require(status.isNotBlank()) { "Status cannot be empty" }
        require(status in listOf("pending", "in_progress", "completed", "cancelled", "deferred")) {
            "Invalid status. Must be: pending, in_progress, completed, cancelled, or deferred"
        }
        return taskRepository.getTasksByStatus(status)
    }
}