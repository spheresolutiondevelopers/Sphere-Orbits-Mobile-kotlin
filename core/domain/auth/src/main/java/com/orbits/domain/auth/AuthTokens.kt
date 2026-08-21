package com.orbits.domain.auth

/**
 * Authentication tokens.
 */
data class AuthTokens(
    val accessToken: String,
    val refreshToken: String? = null,
    val expiresAt: String
) {
    /**
     * Check if the access token is expired.
     */
    fun isExpired(): Boolean {
        return try {
            val expiry = java.time.Instant.parse(expiresAt)
            expiry.isBefore(java.time.Instant.now())
        } catch (e: Exception) {
            true
        }
    }

    /**
     * Get the time until expiry in seconds.
     */
    fun getSecondsUntilExpiry(): Long {
        return try {
            val expiry = java.time.Instant.parse(expiresAt)
            val now = java.time.Instant.now()
            java.time.Duration.between(now, expiry).seconds.coerceAtLeast(0)
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Check if the token is about to expire (within 5 minutes).
     */
    fun isAboutToExpire(): Boolean {
        return getSecondsUntilExpiry() < 300 // 5 minutes
    }
}