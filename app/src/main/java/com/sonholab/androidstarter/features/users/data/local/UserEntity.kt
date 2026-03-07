package com.sonholab.androidstarter.features.users.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)],
)
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "phone")
    val phone: String,

    @ColumnInfo(name = "website")
    val website: String,

    @ColumnInfo(name = "company_name")
    val companyName: String,

    @ColumnInfo(name = "cached_at")
    val cachedAt: Long = System.currentTimeMillis(),
)
