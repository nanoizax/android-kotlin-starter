package com.sonholab.androidstarter.features.auth.domain.model

/**
 * Domain model representing the authenticated user.
 */
data class User(
    val id: String,
    val email: String,
    val name: String,
    val avatarUrl: String?,
    val role: String,
)
