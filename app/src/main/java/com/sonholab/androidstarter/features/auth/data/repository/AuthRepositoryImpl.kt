package com.sonholab.androidstarter.features.auth.data.repository

import com.sonholab.androidstarter.core.domain.Result
import com.sonholab.androidstarter.features.auth.data.local.TokenDataStore
import com.sonholab.androidstarter.features.auth.data.remote.AuthApi
import com.sonholab.androidstarter.features.auth.data.remote.dto.AuthResponse
import com.sonholab.androidstarter.features.auth.data.remote.dto.LoginRequest
import com.sonholab.androidstarter.features.auth.domain.model.User
import com.sonholab.androidstarter.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val tokenDataStore: TokenDataStore,
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> = try {
        val response = authApi.login(LoginRequest(email = email, password = password))
        tokenDataStore.saveToken(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken,
        )
        Result.Success(response.toDomain())
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun logout(): Result<Unit> = try {
        // Attempt server-side session invalidation; clear tokens regardless of outcome.
        try {
            authApi.logout()
        } catch (_: Exception) {
            // Swallow network errors — token is cleared locally below.
        }
        tokenDataStore.clearToken()
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    // -------------------------------------------------------------------------
    // Mappers
    // -------------------------------------------------------------------------

    private fun AuthResponse.toDomain(): User = User(
        id = user.id,
        email = user.email,
        name = user.name,
        avatarUrl = user.avatarUrl,
        role = user.role,
    )
}
