package com.orbits.domain.tasks

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get upcoming tasks (next 7 days).
 */
class GetUpcomingTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {

    /**
     * Execute the use case.
     * @return Flow emitting the list of upcoming tasks
     */
    operator fun invoke(): Flow<List<Task>> {
        return taskRepository.getUpcomingTasks()
    }
}