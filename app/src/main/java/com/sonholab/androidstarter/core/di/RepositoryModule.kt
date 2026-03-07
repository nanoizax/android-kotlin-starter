package com.sonholab.androidstarter.core.di

import com.sonholab.androidstarter.features.auth.data.repository.AuthRepositoryImpl
import com.sonholab.androidstarter.features.auth.domain.repository.AuthRepository
import com.sonholab.androidstarter.features.users.data.repository.UsersRepositoryImpl
import com.sonholab.androidstarter.features.users.domain.repository.UsersRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl,
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUsersRepository(
        impl: UsersRepositoryImpl,
    ): UsersRepository
}
