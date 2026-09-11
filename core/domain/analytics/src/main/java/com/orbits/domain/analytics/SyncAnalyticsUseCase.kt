package com.orbits.domain.analytics

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to synchronize analytics events with the server.
 */
class SyncAnalyticsUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {

    /**
     * Execute the use case.
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(): Result<Unit> {
        return analyticsRepository.syncAnalytics()
    }
}
