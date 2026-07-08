package com.orbits.data.auth.remote

import com.orbits.core.model.UserDto
import kotlinx.serialization.Serializable

@Serializable
internal data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
internal data class RegisterRequest(
    val email: String,
    val password: String,
    val displayName: String? = null,
    val firstName: String? = null,
    val lastName: String? = null
)

@Serializable
internal data class LoginResponse(
    val user: UserDto,
    val token: String,
    val refreshToken: String,
    val expiresIn: Long
)

@Serializable
internal data class RefreshTokenRequest(
    val refreshToken: String
)

@Serializable
internal data class RefreshTokenResponse(
    val token: String,
    val expiresIn: Long
)

@Serializable
internal data class LogoutResponse(
    val success: Boolean,
    val message: String? = null
)