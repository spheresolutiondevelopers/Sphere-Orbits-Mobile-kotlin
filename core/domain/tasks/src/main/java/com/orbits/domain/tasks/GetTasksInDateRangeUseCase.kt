package com.orbits.domain.tasks

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to get tasks in a date range.
 */
class GetTasksInDateRangeUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {

    /**
     * Execute the use case.
     * @param startDate Start date in ISO format (YYYY-MM-DD)
     * @param endDate End date in ISO format (YYYY-MM-DD)
     * @return Flow emitting the filtered list of tasks
     */
    operator fun invoke(startDate: String, endDate: String): Flow<List<Task>> {
        require(startDate.isNotBlank()) { "Start date cannot be empty" }
        require(endDate.isNotBlank()) { "End date cannot be empty" }
        require(startDate <= endDate) { "Start date must be before or equal to end date" }

        return taskRepository.getTasksInDateRange(startDate, endDate)
    }
}