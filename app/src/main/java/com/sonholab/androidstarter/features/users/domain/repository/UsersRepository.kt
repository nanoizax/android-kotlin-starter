package com.sonholab.androidstarter.features.users.domain.repository

import com.sonholab.androidstarter.features.users.domain.model.UserSummary
import kotlinx.coroutines.flow.Flow

interface UsersRepository {

    /**
     * Returns a [Flow] of cached users from local storage.
     * Callers should invoke [refresh] to trigger a network sync.
     */
    fun observeUsers(): Flow<List<UserSummary>>

    /**
     * Fetches users from the network and updates the local cache.
     * Throws on network failure; the cached data stream remains unaffected.
     */
    suspend fun refresh()
}
