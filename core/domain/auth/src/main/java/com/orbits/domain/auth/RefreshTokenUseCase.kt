package com.orbits.domain.auth

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case to refresh the authentication token.
 */
class RefreshTokenUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    /**
     * Execute the token refresh.
     * @return Result containing new AuthTokens on success, or error
     */
    suspend operator fun invoke(): Result<AuthTokens> {
        return authRepository.refreshAccessToken()
    }

    /**
     * Check if token refresh is needed.
     * @param tokens Current auth tokens
     * @return true if refresh is needed, false otherwise
     */
    fun isRefreshNeeded(tokens: AuthTokens?): Boolean {
        if (tokens == null) return true
        return tokens.isAboutToExpire()
    }

    /**
     * Refresh token if needed.
     * @param tokens Current auth tokens
     * @return New tokens if refreshed, same tokens if not needed, or error
     */
    suspend fun refreshIfNeeded(tokens: AuthTokens?): Result<AuthTokens> {
        if (!isRefreshNeeded(tokens)) {
            return tokens?.let { Result.Success(it) }
                ?: Result.Error(IllegalStateException("No tokens available"))
        }
        return invoke()
    }
}