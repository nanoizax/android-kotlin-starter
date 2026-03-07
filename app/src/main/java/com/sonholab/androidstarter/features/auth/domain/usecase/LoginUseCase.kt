package com.sonholab.androidstarter.features.auth.domain.usecase

import com.sonholab.androidstarter.core.domain.Result
import com.sonholab.androidstarter.features.auth.domain.model.User
import com.sonholab.androidstarter.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    /**
     * Validates the credentials and delegates login to [AuthRepository].
     *
     * Validation rules:
     * - [email] must be a non-empty string containing '@' and a domain part.
     * - [password] must be at least 6 characters.
     *
     * @return [Result.Error] for validation failures, otherwise the result from the repository.
     */
    suspend operator fun invoke(email: String, password: String): Result<User> {
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        if (!isValidEmail(trimmedEmail)) {
            return Result.Error(IllegalArgumentException("Invalid email address."))
        }

        if (!isValidPassword(trimmedPassword)) {
            return Result.Error(
                IllegalArgumentException("Password must be at least 6 characters long."),
            )
        }

        return authRepository.login(trimmedEmail, trimmedPassword)
    }

    private fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false
        val atIndex = email.indexOf('@')
        if (atIndex <= 0) return false
        val domain = email.substring(atIndex + 1)
        return domain.contains('.') && domain.length >= 3
    }

    private fun isValidPassword(password: String): Boolean = password.length >= 6
}
