package com.sonholab.androidstarter.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sonholab.androidstarter.features.users.data.local.UserDao
import com.sonholab.androidstarter.features.users.data.local.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        const val DATABASE_NAME = "starter.db"
    }
}
