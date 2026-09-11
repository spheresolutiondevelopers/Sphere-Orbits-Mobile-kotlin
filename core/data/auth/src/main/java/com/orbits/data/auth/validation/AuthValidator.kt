package com.orbits.data.auth.validation

import com.orbits.domain.auth.LoginCredentials
import com.orbits.domain.auth.RegistrationData

object AuthValidator {

    fun validateLogin(credentials: LoginCredentials): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        if (credentials.email.isBlank()) {
            errors.add(ValidationError("email", "Email cannot be empty"))
        } else if (!credentials.email.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))) {
            errors.add(ValidationError("email", "Invalid email format"))
        }

        if (credentials.password.isBlank()) {
            errors.add(ValidationError("password", "Password cannot be empty"))
        } else if (credentials.password.length < 8) {
            errors.add(ValidationError("password", "Password must be at least 8 characters"))
        }

        return ValidationResult(errors.isEmpty(), errors)
    }

    fun validateRegistration(data: RegistrationData): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        if (data.email.isBlank()) {
            errors.add(ValidationError("email", "Email cannot be empty"))
        } else if (!data.email.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))) {
            errors.add(ValidationError("email", "Invalid email format"))
        }

        if (data.password.isBlank()) {
            errors.add(ValidationError("password", "Password cannot be empty"))
        } else if (data.password.length < 8) {
            errors.add(ValidationError("password", "Password must be at least 8 characters"))
        }

        val displayName = data.displayName
        if (displayName != null && displayName.length > 100) {
            errors.add(ValidationError("displayName", "Display name must be 100 characters or less"))
        }

        return ValidationResult(errors.isEmpty(), errors)
    }
}

data class ValidationError(
    val field: String,
    val message: String
)

data class ValidationResult(
    val isValid: Boolean,
    val errors: List<ValidationError>
)