package com.orbits.domain.appointments

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to sync appointments with external calendar services.
 */
class SyncAppointmentsUseCase @Inject constructor(
    private val appointmentRepository: AppointmentRepository
) {

    /**
     * Sync appointments with Google Calendar.
     * @param accessToken Google OAuth access token
     * @return Result indicating success or failure
     */
    suspend fun syncWithGoogle(accessToken: String): Result<Unit> {
        if (accessToken.isBlank()) {
            return Result.Error(IllegalArgumentException("Access token cannot be empty"))
        }
        return appointmentRepository.syncWithExternalCalendar(accessToken)
    }

    /**
     * Sync appointments with Outlook Calendar.
     * @param accessToken Microsoft OAuth access token
     * @return Result indicating success or failure
     */
    suspend fun syncWithOutlook(accessToken: String): Result<Unit> {
        if (accessToken.isBlank()) {
            return Result.Error(IllegalArgumentException("Access token cannot be empty"))
        }
        return appointmentRepository.syncWithExternalCalendar(accessToken)
    }

    /**
     * Sync with all configured external calendars.
     * @param googleAccessToken Google OAuth access token (optional)
     * @param outlookAccessToken Microsoft OAuth access token (optional)
     * @return Result indicating success or failure
     */
    suspend fun syncAll(
        googleAccessToken: String? = null,
        outlookAccessToken: String? = null
    ): Result<Unit> {
        val errors = mutableListOf<Throwable>()

        googleAccessToken?.let { token ->
            val result = syncWithGoogle(token)
            if (result is Result.Error) {
                errors.add(result.exception)
            }
        }

        outlookAccessToken?.let { token ->
            val result = syncWithOutlook(token)
            if (result is Result.Error) {
                errors.add(result.exception)
            }
        }

        return if (errors.isEmpty()) {
            Result.Success(Unit)
        } else {
            Result.Error(
                IllegalStateException(
                    "Sync errors: ${errors.joinToString { it.message ?: "Unknown error" }}"
                )
            )
        }
    }
}
