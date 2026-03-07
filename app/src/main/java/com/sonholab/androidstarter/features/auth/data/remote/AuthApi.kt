package com.sonholab.androidstarter.features.auth.data.remote

import com.sonholab.androidstarter.features.auth.data.remote.dto.AuthResponse
import com.sonholab.androidstarter.features.auth.data.remote.dto.LoginRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    /**
     * Authenticates the user with email and password.
     * Returns tokens and user profile on success.
     */
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest,
    ): AuthResponse

    /**
     * Invalidates the current session on the server.
     * The client must clear locally stored tokens regardless of the outcome.
     */
    @POST("auth/logout")
    suspend fun logout()
}
