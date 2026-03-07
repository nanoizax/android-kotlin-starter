package com.sonholab.androidstarter.features.users.data.repository

import com.sonholab.androidstarter.features.users.data.local.UserDao
import com.sonholab.androidstarter.features.users.data.local.UserEntity
import com.sonholab.androidstarter.features.users.data.remote.UsersApi
import com.sonholab.androidstarter.features.users.data.remote.dto.UserDto
import com.sonholab.androidstarter.features.users.domain.model.UserSummary
import com.sonholab.androidstarter.features.users.domain.repository.UsersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UsersRepositoryImpl @Inject constructor(
    private val usersApi: UsersApi,
    private val userDao: UserDao,
) : UsersRepository {

    /**
     * Emits cached users from Room. Room automatically emits updates when [refresh] writes
     * new data, providing a local-first reactive pattern without manual merging.
     */
    override fun observeUsers(): Flow<List<UserSummary>> =
        userDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    /**
     * Fetches all users from the remote API and replaces the local cache atomically.
     * The [observeUsers] flow will automatically emit updated data after this call.
     */
    override suspend fun refresh() {
        val remoteUsers = usersApi.getUsers()
        val entities = remoteUsers.map { it.toEntity() }
        userDao.deleteAll()
        userDao.insertAll(entities)
    }

    // -------------------------------------------------------------------------
    // Mappers
    // -------------------------------------------------------------------------

    private fun UserDto.toEntity(): UserEntity = UserEntity(
        id = id,
        name = name,
        email = email,
        username = username,
        phone = phone,
        website = website,
        companyName = company.name,
    )

    private fun UserEntity.toDomain(): UserSummary = UserSummary(
        id = id,
        name = name,
        email = email,
        username = username,
        companyName = companyName,
    )
}
