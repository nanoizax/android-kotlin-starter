package com.sonholab.androidstarter.features.users.data.remote

import com.sonholab.androidstarter.features.users.data.remote.dto.UserDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UsersApi {

    @GET("users")
    suspend fun getUsers(
        @Query("_page") page: Int = 1,
        @Query("_limit") limit: Int = 20,
    ): List<UserDto>

    @GET("users/{id}")
    suspend fun getUserById(
        @Path("id") id: Int,
    ): UserDto
}
