package com.sonholab.androidstarter.features.auth.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthResponse(
    @Json(name = "access_token")
    val accessToken: String,
    @Json(name = "refresh_token")
    val refreshToken: String?,
    @Json(name = "user")
    val user: UserPayload,
) {
    @JsonClass(generateAdapter = true)
    data class UserPayload(
        @Json(name = "id")
        val id: String,
        @Json(name = "email")
        val email: String,
        @Json(name = "name")
        val name: String,
        @Json(name = "avatar_url")
        val avatarUrl: String?,
        @Json(name = "role")
        val role: String,
    )
}
