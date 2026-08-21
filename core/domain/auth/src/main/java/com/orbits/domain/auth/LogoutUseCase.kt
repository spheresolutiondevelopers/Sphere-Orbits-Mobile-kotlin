package com.orbits.domain.auth

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case for user logout.
 */
class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    /**
     * Execute the logout.
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.logout()
    }
}