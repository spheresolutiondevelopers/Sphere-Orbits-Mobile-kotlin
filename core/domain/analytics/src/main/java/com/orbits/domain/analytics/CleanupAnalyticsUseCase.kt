package com.orbits.domain.analytics

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to clean up old analytics events.
 */
class CleanupAnalyticsUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {

    /**
     * Execute the use case.
     * @param daysToKeep Number of days of data to keep (default: 90)
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(daysToKeep: Int = 90): Result<Unit> {
        require(daysToKeep in 1..365) { "Days to keep must be between 1 and 365" }

        return analyticsRepository.cleanupOldEvents(daysToKeep)
    }

    /**
     * Clean up events older than 30 days.
     */
    suspend fun cleanup30Days(): Result<Unit> {
        return invoke(30)
    }

    /**
     * Clean up events older than 90 days.
     */
    suspend fun cleanup90Days(): Result<Unit> {
        return invoke(90)
    }

    /**
     * Clean up events older than 180 days.
     */
    suspend fun cleanup180Days(): Result<Unit> {
        return invoke(180)
    }
}
