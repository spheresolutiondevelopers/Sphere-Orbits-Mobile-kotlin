package com.orbits.domain.analytics

import com.orbits.core.common.Result
import com.orbits.core.common.extensions.nowUtc
import java.util.UUID
import javax.inject.Inject

/**
 * Use case to track multiple analytics events.
 */
class TrackEventsUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {

    /**
     * Execute the use case.
     * @param events The list of event data objects
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(events: List<AnalyticsEvent>): Result<Unit> {
        if (events.isEmpty()) {
            return Result.Error(IllegalArgumentException("At least one event required"))
        }

        // Validate each event
        events.forEach { event ->
            val validationResult = AnalyticsValidation.validateEvent(
                userId = event.userId,
                eventType = event.eventType,
                eventCategory = event.eventCategory,
                eventSubtype = event.eventSubtype,
                entityId = event.entityId,
                entityType = event.entityType,
                value = event.value,
                metadata = event.metadata,
                sessionId = event.sessionId
            )

            if (!validationResult.isValid) {
                return Result.Error(
                    IllegalArgumentException(
                        "Event validation failed: ${validationResult.errors.joinToString { it.message }}"
                    )
                )
            }
        }

        return analyticsRepository.trackEvents(events)
    }

    /**
     * Track a batch of events from a single user.
     */
    suspend fun forUser(
        userId: String,
        events: List<AnalyticsEvent>
    ): Result<Unit> {
        if (userId.isBlank()) {
            return Result.Error(IllegalArgumentException("User ID cannot be empty"))
        }

        val now = nowUtc()
        val enrichedEvents = events.map { event ->
            event.copy(
                userId = userId,
                eventTimestamp = now,
                createdAt = now
            )
        }

        return invoke(enrichedEvents)
    }
}
