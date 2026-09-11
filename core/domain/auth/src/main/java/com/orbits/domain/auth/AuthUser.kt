package com.orbits.domain.auth

/**
 * Pure domain model for an authenticated user.
 * Contains NO Android dependencies — safe for KMP.
 */
data class AuthUser(
    val id: String,
    val email: String,
    val username: String? = null,
    val displayName: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    val avatarUrl: String? = null,
    val accountType: String = "free", // free, premium, enterprise, admin
    val isLocal: Boolean = false,
    val isActive: Boolean = true,
    val createdAt: String,
    val updatedAt: String,
    val lastLogin: String? = null
) {
    /**
     * Get the user's full name.
     */
    fun getFullName(): String {
        return if (firstName != null && lastName != null) {
            "$firstName $lastName"
        } else {
            displayName ?: username ?: email
        }
    }

    /**
     * Check if the user has a premium subscription.
     */
    fun isPremium(): Boolean = accountType in listOf("premium", "enterprise")

    /**
     * Check if the user is an enterprise user.
     */
    fun isEnterprise(): Boolean = accountType == "enterprise"

    /**
     * Check if the user is an admin.
     */
    fun isAdmin(): Boolean = accountType == "admin"
}