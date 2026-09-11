package com.orbits.domain.auth

import com.orbits.core.common.Result
import javax.inject.Inject

/**
 * Use case for user registration.
 * Encapsulates the business logic for creating a new user account.
 */
class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    /**
     * Execute the registration.
     * @param email User's email address
     * @param password User's password
     * @param displayName User's display name (optional)
     * @param firstName User's first name (optional)
     * @param lastName User's last name (optional)
     * @return Result containing AuthUser on success, or error
     */
    suspend operator fun invoke(
        email: String,
        password: String,
        displayName: String? = null,
        firstName: String? = null,
        lastName: String? = null
    ): Result<AuthUser> {
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

        // Attempt registration
        return authRepository.register(
            RegistrationData(
                email = email,
                password = password,
                displayName = displayName,
                firstName = firstName,
                lastName = lastName
            )
        )
    }
}
