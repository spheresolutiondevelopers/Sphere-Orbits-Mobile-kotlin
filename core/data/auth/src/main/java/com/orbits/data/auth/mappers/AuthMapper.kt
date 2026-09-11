package com.orbits.data.auth.mappers

import com.orbits.core.common.extensions.nowUtc
import com.orbits.core.model.UserDto
import com.orbits.data.auth.AuthEntity
import com.orbits.data.auth.TokenEntity
import com.orbits.domain.auth.AuthUser
import com.orbits.domain.auth.AuthTokens
import javax.inject.Inject

internal class AuthMapper @Inject constructor() {

    // ─── DTO → Domain ──────────────────────────────────────────────

    fun toDomain(dto: UserDto): AuthUser {
        return AuthUser(
            id = dto.id,
            email = dto.email,
            username = dto.username,
            displayName = dto.displayName,
            firstName = dto.firstName,
            lastName = dto.lastName,
            phoneNumber = dto.phoneNumber,
            avatarUrl = dto.avatarUrl,
            accountType = dto.accountType.name,
            isLocal = false,
            isActive = dto.isActive,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }

    // ─── Entity ↔ Domain ──────────────────────────────────────────

    fun toDomain(entity: AuthEntity): AuthUser {
        return AuthUser(
            id = entity.id,
            email = entity.email,
            username = entity.username,
            displayName = entity.displayName,
            firstName = entity.firstName,
            lastName = entity.lastName,
            phoneNumber = entity.phoneNumber,
            avatarUrl = entity.avatarUrl,
            accountType = entity.accountType,
            isLocal = entity.isLocal,
            isActive = entity.isActive,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            lastLogin = entity.lastLogin
        )
    }

    fun toEntity(domain: AuthUser): AuthEntity {
        return AuthEntity(
            id = domain.id,
            email = domain.email,
            username = domain.username,
            displayName = domain.displayName,
            firstName = domain.firstName,
            lastName = domain.lastName,
            phoneNumber = domain.phoneNumber,
            avatarUrl = domain.avatarUrl,
            accountType = domain.accountType,
            isLocal = domain.isLocal,
            isActive = domain.isActive,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            lastLogin = domain.lastLogin
        )
    }

    // ─── Token Entity ↔ Domain ────────────────────────────────────

    fun toDomain(entity: TokenEntity): AuthTokens {
        return AuthTokens(
            accessToken = entity.accessToken,
            refreshToken = entity.refreshToken,
            expiresAt = entity.expiresAt
        )
    }

    fun toEntity(domain: AuthTokens, userId: String): TokenEntity {
        return TokenEntity(
            userId = userId,
            accessToken = domain.accessToken,
            refreshToken = domain.refreshToken,
            expiresAt = domain.expiresAt,
            createdAt = nowUtc(),
            updatedAt = nowUtc()
        )
    }

    // ─── DTO → Entity (for LoginResponse) ─────────────────────────

    fun toEntity(user: UserDto, token: String, refreshToken: String, expiresIn: Long): Pair<AuthEntity, TokenEntity> {
        val now = nowUtc()
        val expiresAt = java.time.Instant.now()
            .plusSeconds(expiresIn)
            .toString()

        val authEntity = AuthEntity(
            id = user.id,
            email = user.email,
            username = user.username,
            displayName = user.displayName,
            firstName = user.firstName,
            lastName = user.lastName,
            phoneNumber = user.phoneNumber,
            avatarUrl = user.avatarUrl,
            accountType = user.accountType.name,
            isLocal = false,
            isActive = user.isActive,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt,
            lastLogin = now
        )

        val tokenEntity = TokenEntity(
            userId = user.id,
            accessToken = token,
            refreshToken = refreshToken,
            expiresAt = expiresAt,
            createdAt = now,
            updatedAt = now
        )

        return Pair(authEntity, tokenEntity)
    }
}