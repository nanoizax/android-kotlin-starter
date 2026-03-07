package com.sonholab.androidstarter.features.auth.domain.repository

import com.sonholab.androidstarter.core.domain.Result
import com.sonholab.androidstarter.features.auth.domain.model.User

interface AuthRepository {

    /**
     * Authenticates the user with [email] and [password].
     * On success, persists the tokens locally and returns the [User] domain model.
     */
    suspend fun login(email: String, password: String): Result<User>

    /**
     * Invalidates the server session and clears all locally stored tokens.
     */
    suspend fun logout(): Result<Unit>
}
