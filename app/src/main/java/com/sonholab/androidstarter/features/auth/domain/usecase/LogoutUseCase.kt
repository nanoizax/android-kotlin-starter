package com.sonholab.androidstarter.features.auth.domain.usecase

import com.sonholab.androidstarter.core.domain.Result
import com.sonholab.androidstarter.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    /**
     * Delegates logout to [AuthRepository].
     * Always clears local tokens even if the server request fails.
     */
    suspend operator fun invoke(): Result<Unit> = authRepository.logout()
}
