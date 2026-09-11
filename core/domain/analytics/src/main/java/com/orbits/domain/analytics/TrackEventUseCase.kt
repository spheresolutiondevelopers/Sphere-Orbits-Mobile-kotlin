package com.orbits.domain.analytics

import com.orbits.core.common.Result
import com.orbits.core.common.extensions.nowUtc
import java.util.UUID
import javax.inject.Inject

/**
 * Use case to track a single analytics event.
 */
class TrackEventUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {

    /**
     * Execute the use case.
     * @param userId The user ID
     * @param eventType The event type
     * @param eventCategory The event category
     * @param eventSubtype Optional subtype
     * @param entityId Optional entity ID
     * @param entityType Optional entity type
     * @param value Optional numeric value
     * @param metadata Optional metadata
     * @param sessionId Optional session ID
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(
        userId: String,
        eventType: String,
        eventCategory: String = "user_action",
        eventSubtype: String? = null,
        entityId: String? = null,
        entityType: String? = null,
        value: Double? = null,
        metadata: Map<String, String> = emptyMap(),
        sessionId: String? = null
    ): Result<Unit> {
        // Validate
        val validationResult = AnalyticsValidation.validateEvent(
            userId = userId,
            eventType = eventType,
            eventCategory = eventCategory,
            eventSubtype = eventSubtype,
            entityId = entityId,
            entityType = entityType,
            value = value,
            metadata = metadata,
            sessionId = sessionId
        )

        if (!validationResult.isValid) {
            return Result.Error(
                IllegalArgumentException(
                    validationResult.errors.joinToString { it.message }
                )
            )
        }

        val now = nowUtc()
        val event = AnalyticsEvent(
            id = UUID.randomUUID().toString(),
            userId = userId,
            eventType = eventType,
            eventCategory = eventCategory,
            eventSubtype = eventSubtype,
            entityId = entityId,
            entityType = entityType,
            value = value,
            metadata = metadata,
            sessionId = sessionId,
            eventTimestamp = now,
            createdAt = now
        )

        return analyticsRepository.trackEvent(event)
    }

    /**
     * Track a task created event.
     */
    suspend fun taskCreated(
        userId: String,
        taskId: String,
        priority: String,
        category: String? = null
    ): Result<Unit> {
        return analyticsRepository.trackTaskCreated(userId, taskId, priority, category)
    }

    /**
     * Track a task completed event.
     */
    suspend fun taskCompleted(
        userId: String,
        taskId: String,
        completionTimeHours: Double,
        priority: String
    ): Result<Unit> {
        return analyticsRepository.trackTaskCompleted(
            userId, taskId, completionTimeHours, priority
        )
    }

    /**
     * Track an appointment created event.
     */
    suspend fun appointmentCreated(
        userId: String,
        appointmentId: String,
        durationMinutes: Int
    ): Result<Unit> {
        return analyticsRepository.trackAppointmentCreated(
            userId, appointmentId, durationMinutes
        )
    }

    /**
     * Track a meeting joined event.
     */
    suspend fun meetingJoined(
        userId: String,
        meetingId: String,
        platform: String
    ): Result<Unit> {
        return analyticsRepository.trackMeetingJoined(userId, meetingId, platform)
    }

    /**
     * Track focus time.
     */
    suspend fun focusTime(
        userId: String,
        durationMinutes: Double,
        taskId: String? = null
    ): Result<Unit> {
        return analyticsRepository.trackFocusTime(userId, durationMinutes, taskId)
    }
}
