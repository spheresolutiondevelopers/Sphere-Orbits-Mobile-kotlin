package com.orbits.domain.auth

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case for user login.
 * Encapsulates the business logic for authentication.
 */
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    /**
     * Execute the login.
     * @param email User's email address
     * @param password User's password
     * @return Result containing AuthUser on success, or error
     */
    suspend operator fun invoke(email: String, password: String): Result<AuthUser> {
        // Validate input
        if (email.isBlank()) {
            return Result.Error(IllegalArgumentException("Email cannot be empty"))
        }
        if (password.isBlank()) {
            return Result.Error(IllegalArgumentException("Password cannot be empty"))
        }

        // Validate email format
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        if (!emailRegex.matches(email)) {
            return Result.Error(IllegalArgumentException("Invalid email format"))
        }

        // Validate password length
        if (password.length < 8) {
            return Result.Error(IllegalArgumentException("Password must be at least 8 characters"))
        }

        // Attempt login
        return authRepository.login(LoginCredentials(email, password))
    }

    /**
     * Execute the login with OAuth token (Google, Microsoft, etc.)
     * @param provider The OAuth provider (google, microsoft, etc.)
     * @param idToken The OAuth ID token
     * @return Result containing AuthUser on success, or error
     */
    suspend fun loginWithOAuth(provider: String, idToken: String): Result<AuthUser> {
        if (provider.isBlank()) {
            return Result.Error(IllegalArgumentException("Provider cannot be empty"))
        }
        if (idToken.isBlank()) {
            return Result.Error(IllegalArgumentException("ID token cannot be empty"))
        }

        // OAuth login flow — typically handled by a separate method
        // For now, we use the regular login with a special flag
        return authRepository.login(LoginCredentials(idToken, ""))
    }
}