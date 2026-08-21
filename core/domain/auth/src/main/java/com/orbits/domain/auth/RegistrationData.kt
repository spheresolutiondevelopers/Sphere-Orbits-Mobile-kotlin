package com.orbits.domain.auth

/**
 * Registration data for new user signup.
 */
data class RegistrationData(
    val email: String,
    val password: String,
    val displayName: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null
) {
    init {
        require(email.isNotBlank()) { "Email cannot be empty" }
        require(password.isNotBlank()) { "Password cannot be empty" }
        require(password.length >= 8) { "Password must be at least 8 characters" }
        if (displayName != null) {
            require(displayName.length <= 100) { "Display name must be 100 characters or less" }
        }
        if (firstName != null) {
            require(firstName.length <= 50) { "First name must be 50 characters or less" }
        }
        if (lastName != null) {
            require(lastName.length <= 50) { "Last name must be 50 characters or less" }
        }
        if (phoneNumber != null) {
            require(phoneNumber.replace(Regex("[^0-9]"), "").length >= 10) {
                "Phone number must be at least 10 digits"
            }
        }
    }

    /**
     * Validate email format.
     */
    fun isValidEmail(): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return emailRegex.matches(email)
    }

    /**
     * Get the user's full name.
     */
    fun getFullName(): String {
        return if (firstName != null && lastName != null) {
            "$firstName $lastName"
        } else {
            displayName ?: email
        }
    }
}