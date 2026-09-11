package com.orbits.data.auth

import com.orbits.core.common.Result
import com.orbits.core.common.Logger
import com.orbits.core.common.TokenProvider
import com.orbits.core.common.extensions.nowUtc
import com.orbits.core.database.dao.UserDao
import com.orbits.core.network.api.AuthApi
import com.orbits.core.network.error.ApiErrorParser
import com.orbits.core.model.LoginRequest
import com.orbits.core.model.RegisterRequest
import com.orbits.core.model.RefreshTokenRequest
import com.orbits.data.auth.AuthEntity
import com.orbits.data.auth.TokenEntity
import com.orbits.data.auth.mappers.AuthMapper
import com.orbits.domain.auth.AuthRepository
import com.orbits.domain.auth.AuthUser
import com.orbits.domain.auth.AuthTokens
import com.orbits.domain.auth.LoginCredentials
import com.orbits.domain.auth.RegistrationData
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
internal class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val authApi: AuthApi,
    private val tokenManager: TokenManager,
    private val authMapper: AuthMapper,
    private val oAuthClient: OAuthClient
) : AuthRepository, TokenProvider by tokenManager {

    companion object {
        private const val TAG = "AuthRepository"
    }

    // ─── Read ──────────────────────────────────────────────────────

    override suspend fun getCurrentUser(): AuthUser? {
        // For now, we get the first active user (simplified)
        val users = userDao.getActiveUsers()
        val user = users.firstOrNull()
        return user?.let { authMapper.toDomain(it) }
    }

    override suspend fun getUser(userId: String): AuthUser? {
        return userDao.getAuth(userId)?.let { authMapper.toDomain(it) }
    }

    override fun observeCurrentUser(): Flow<AuthUser?> {
        return userDao.getActiveUsersFlow()
            .map { users -> users.firstOrNull() }
            .map { entity -> entity?.let { authMapper.toDomain(entity) } }
    }

    override suspend fun isAuthenticated(): Boolean {
        val user = getCurrentUser()
        if (user?.isLocal == true) return true

        val token = tokenManager.getAccessToken()
        return token != null && !tokenManager.isTokenExpired()
    }

    // ─── Auth Operations ──────────────────────────────────────────

    override suspend fun login(credentials: LoginCredentials): Result<AuthUser> {
        return try {
            val request = LoginRequest(
                email = credentials.email,
                password = credentials.password
            )
            val response = authApi.login(request)

            // Clear existing local guest user if present
            val currentUser = getCurrentUser()
            if (currentUser?.isLocal == true) {
                userDao.deleteAuth(currentUser.id)
                userDao.deleteTokens(currentUser.id)
            }

            // Save user and tokens locally
            val (userEntity, tokenEntity) = authMapper.toEntity(
                user = response.user,
                token = response.token,
                refreshToken = response.refreshToken,
                expiresIn = response.expiresIn
            )
            userDao.insertAuth(userEntity)
            userDao.insertTokens(tokenEntity)

            // Save tokens in DataStore for TokenManager
            tokenManager.saveTokens(
                accessToken = response.token,
                refreshToken = response.refreshToken,
                expiresAt = tokenEntity.expiresAt
            )

            Logger.d(TAG, "User logged in: ${response.user.email}")
            Result.Success(authMapper.toDomain(response.user))
        } catch (e: Exception) {
            Logger.e(TAG, "Login error", e)
            Result.Error(e)
        }
    }

    override suspend fun createLocalUser(data: RegistrationData?): Result<AuthUser> {
        return try {
            val now = nowUtc()
            val localUser = AuthUser(
                id = "local_${java.util.UUID.randomUUID()}",
                email = data?.email ?: "local@sphere.local",
                displayName = data?.displayName ?: (if (data != null) "${data.firstName} ${data.lastName}".trim() else "Local User"),
                firstName = data?.firstName,
                lastName = data?.lastName,
                isLocal = true,
                isActive = true,
                createdAt = now,
                updatedAt = now
            )

            userDao.insertAuth(authMapper.toEntity(localUser))

            Logger.d(TAG, "Local user created: ${localUser.email}")
            Result.Success(localUser)
        } catch (e: Exception) {
            Logger.e(TAG, "Error creating local user", e)
            Result.Error(e)
        }
    }

    override suspend fun register(data: RegistrationData): Result<AuthUser> {
        return try {
            val request = RegisterRequest(
                email = data.email,
                password = data.password,
                displayName = data.displayName,
                firstName = data.firstName,
                lastName = data.lastName
            )
            val user = authApi.register(request)

            // Clear existing local guest user if present
            val currentUser = getCurrentUser()
            if (currentUser?.isLocal == true) {
                userDao.deleteAuth(currentUser.id)
                userDao.deleteTokens(currentUser.id)
            }

            // Save user locally (not logged in yet)
            val userEntity = authMapper.toEntity(authMapper.toDomain(user))
            userDao.insertAuth(userEntity)

            Logger.d(TAG, "User registered: ${user.email}")
            Result.Success(authMapper.toDomain(user))
        } catch (e: Exception) {
            Logger.e(TAG, "Registration error", e)
            Result.Error(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            // Call logout API (optional)
            authApi.logout()

            // Clear local data
            val currentUser = getCurrentUser()
            if (currentUser != null) {
                userDao.deactivateUser(currentUser.id)
                userDao.deleteTokens(currentUser.id)
                tokenManager.clearTokens()
                // Optionally revoke OAuth tokens
                oAuthClient.signOutGoogle()
            }

            Logger.d(TAG, "User logged out")
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Logout error", e)
            Result.Error(e)
        }
    }

    override suspend fun refreshAccessToken(): Result<AuthTokens> {
        return try {
            val refreshToken = tokenManager.getRefreshToken()
            if (refreshToken == null) {
                return Result.Error(IllegalStateException("No refresh token available"))
            }

            val request = RefreshTokenRequest(refreshToken)
            val response = authApi.refreshToken(request)

            // Update tokens locally
            val currentUser = getCurrentUser()
            if (currentUser != null) {
                val tokenEntity = TokenEntity(
                    userId = currentUser.id,
                    accessToken = response.token,
                    refreshToken = refreshToken, // Keep existing refresh token
                    expiresAt = java.time.Instant.now()
                        .plusSeconds(response.expiresIn)
                        .toString(),
                    createdAt = nowUtc(),
                    updatedAt = nowUtc()
                )
                userDao.insertTokens(tokenEntity)
                tokenManager.saveTokens(
                    accessToken = response.token,
                    refreshToken = refreshToken,
                    expiresAt = tokenEntity.expiresAt
                )
            }

            val tokens = AuthTokens(
                accessToken = response.token,
                refreshToken = refreshToken,
                expiresAt = java.time.Instant.now()
                    .plusSeconds(response.expiresIn)
                    .toString()
            )
            Result.Success(tokens)
        } catch (e: Exception) {
            Logger.e(TAG, "Token refresh error", e)
            Result.Error(e)
        }
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return try {
            // Delete user locally (hard delete)
            val currentUser = getCurrentUser()
            if (currentUser != null) {
                userDao.deleteAuth(currentUser.id)
                userDao.deleteTokens(currentUser.id)
                tokenManager.clearTokens()
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e(TAG, "Account deletion error", e)
            Result.Error(e)
        }
    }
}