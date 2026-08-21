package com.orbits.domain.tasks

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get all tasks for the current user.
 */
class GetTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {

    /**
     * Execute the use case.
     * @return Flow emitting the list of tasks
     */
    operator fun invoke(): Flow<List<Task>> {
        return taskRepository.getTasks()
    }
}