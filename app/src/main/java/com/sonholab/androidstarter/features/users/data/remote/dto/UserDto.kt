package com.sonholab.androidstarter.features.users.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDto(
    @Json(name = "id")
    val id: Int,
    @Json(name = "name")
    val name: String,
    @Json(name = "email")
    val email: String,
    @Json(name = "username")
    val username: String,
    @Json(name = "phone")
    val phone: String,
    @Json(name = "website")
    val website: String,
    @Json(name = "company")
    val company: CompanyDto,
) {
    @JsonClass(generateAdapter = true)
    data class CompanyDto(
        @Json(name = "name")
        val name: String,
        @Json(name = "catchPhrase")
        val catchPhrase: String?,
        @Json(name = "bs")
        val bs: String?,
    )
}
