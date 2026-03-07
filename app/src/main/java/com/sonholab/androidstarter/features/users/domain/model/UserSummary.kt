package com.sonholab.androidstarter.features.users.domain.model

/**
 * Lightweight domain model used in list screens.
 * Contains only the fields needed for display.
 */
data class UserSummary(
    val id: Int,
    val name: String,
    val email: String,
    val username: String,
    val companyName: String,
)
