package dev.aurakai.auraframefx.network.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val username: String,
    val email: String? = null,
    val role: String? = null,
    val preferences: UserPreferences? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class UserPreferences(
    val theme: String? = null,
    val notifications: Boolean = true,
    val language: String = "en",
)
