/*
 * Copyright © 2026 Sphere Solution Developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.orbits.core.model

import kotlinx.serialization.Serializable

/**
 * User DTO — globally shared model.
 * Used for authentication responses and user profile data.
 */
@Serializable
data class UserDto(
    val id: String,
    val email: String,
    val username: String? = null,
    val displayName: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    val avatarUrl: String? = null,
    val accountType: AccountType = AccountType.FREE,
    val isActive: Boolean = true,
    val createdAt: String,
    val updatedAt: String
)

/**
 * Account type enum.
 */
enum class AccountType {
    FREE,
    PREMIUM,
    ENTERPRISE,
    ADMIN
}

/**
 * User registration request.
 */
@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val displayName: String? = null,
    val firstName: String? = null,
    val lastName: String? = null
)

/**
 * User login request.
 */
@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * User login response.
 */
@Serializable
data class LoginResponse(
    val user: UserDto,
    val token: String,
    val refreshToken: String,
    val expiresIn: Long
)

/**
 * Token refresh request.
 */
@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)

/**
 * Token refresh response.
 */
@Serializable
data class RefreshTokenResponse(
    val token: String,
    val expiresIn: Long
)