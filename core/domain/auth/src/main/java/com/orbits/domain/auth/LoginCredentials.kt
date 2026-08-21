package com.orbits.domain.auth

/**
 * Login credentials.
 */
data class LoginCredentials(
    val email: String,
    val password: String
) {
    init {
        require(email.isNotBlank()) { "Email cannot be empty" }
        require(password.isNotBlank()) { "Password cannot be empty" }
        require(password.length >= 8) { "Password must be at least 8 characters" }
    }

    /**
     * Validate email format.
     */
    fun isValidEmail(): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return emailRegex.matches(email)
    }

    /**
     * Get the email domain.
     */
    fun getEmailDomain(): String? {
        return email.substringAfter("@", "").takeIf { it.isNotBlank() }
    }

    /**
     * Check if this is a social login (OAuth) — password is empty.
     */
    fun isSocialLogin(): Boolean = password.isEmpty()
}