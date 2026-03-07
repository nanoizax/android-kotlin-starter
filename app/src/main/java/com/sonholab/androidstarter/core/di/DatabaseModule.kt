package com.sonholab.androidstarter.core.di

import android.content.Context
import androidx.room.Room
import com.sonholab.androidstarter.core.data.local.AppDatabase
import com.sonholab.androidstarter.features.users.data.local.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase = Room.databaseBuilder(
        context = context,
        klass = AppDatabase::class.java,
        name = AppDatabase.DATABASE_NAME,
    )
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()
}
