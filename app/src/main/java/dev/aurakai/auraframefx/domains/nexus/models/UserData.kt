package dev.aurakai.auraframefx.domains.nexus.models

import kotlinx.serialization.Serializable

/**
 * Represents user profile and authentication data.
 *
 * Used by UserPreferences for storing and retrieving user information,
 * including profile details and API credentials.
 *
 * All fields are nullable to support partial data scenarios and
 * allow users to update individual fields independently.
 */
@Serializable
data class UserData(
    /**
     * Unique user identifier (UUID or database ID).
     */
    val id: String? = null,

    /**
     * User's display name or full name.
     */
    val name: String? = null,

    /**
     * User's email address for account identification and communication.
     */
    val email: String? = null,

    /**
     * API key for authenticated requests to AI services (Vertex AI, etc.).
     */
    val apiKey: String? = null,

    /**
     * User's avatar image URI or URL.
     */
    val avatarUrl: String? = null,

    /**
     * User preferences and settings JSON blob.
     */
    val preferences: String? = null,

    /**
     * Account creation timestamp (milliseconds since epoch).
     */
    val createdAt: Long? = null,

    /**
     * User's role (e.g., Admin, User, Guest).
     */
    val role: String? = null,

    /**
     * User's username (unique identifier).
     */
    val username: String? = null,

    /**
     * Last updated timestamp (milliseconds since epoch).
     */
    val updatedAt: Long? = null
)
