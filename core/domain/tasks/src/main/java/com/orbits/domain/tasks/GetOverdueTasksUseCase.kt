package com.orbits.domain.tasks

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get overdue tasks.
 */
class GetOverdueTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {

    /**
     * Execute the use case.
     * @return Flow emitting the list of overdue tasks
     */
    operator fun invoke(): Flow<List<Task>> {
        return taskRepository.getOverdueTasks()
    }
}