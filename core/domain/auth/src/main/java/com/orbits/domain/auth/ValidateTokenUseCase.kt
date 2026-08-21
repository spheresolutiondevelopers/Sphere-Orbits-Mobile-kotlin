package com.orbits.domain.auth

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to validate authentication token.
 */
class ValidateTokenUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    /**
     * Validate if the current user is authenticated.
     * @return true if authenticated, false otherwise
     */
    suspend fun isAuthenticated(): Boolean {
        return authRepository.isAuthenticated()
    }

    /**
     * Validate and return the current user.
     * @return AuthUser if authenticated, null otherwise
     */
    suspend fun getCurrentUser(): AuthUser? {
        return authRepository.getCurrentUser()
    }

    /**
     * Execute with result wrapper.
     * @return Result containing AuthUser on success, or error
     */
    suspend operator fun invoke(): Result<AuthUser> {
        return try {
            val user = authRepository.getCurrentUser()
            if (user != null && authRepository.isAuthenticated()) {
                Result.Success(user)
            } else {
                Result.Error(IllegalStateException("User not authenticated"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}