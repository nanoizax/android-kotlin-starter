package com.sonholab.androidstarter.features.users.domain.usecase

import com.sonholab.androidstarter.features.users.domain.model.UserSummary
import com.sonholab.androidstarter.features.users.domain.repository.UsersRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUsersUseCase @Inject constructor(
    private val usersRepository: UsersRepository,
) {
    /**
     * Returns a [Flow] of [UserSummary] from local cache.
     * Use [UsersRepository.refresh] to trigger a background network sync.
     */
    operator fun invoke(): Flow<List<UserSummary>> = usersRepository.observeUsers()
}
