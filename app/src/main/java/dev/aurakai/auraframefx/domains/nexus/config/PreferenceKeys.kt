package dev.aurakai.auraframefx.domains.nexus.config

import kotlinx.serialization.Serializable

/**
 * Keys for DataStore preferences.
 */
enum class PreferenceKeys {
    USER_PROFILE,
    AGENT_CONFIGURATIONS,
    SECURITY_POLICIES,
    CUSTOMIZATIONS,
    FIRST_LAUNCH,
    USER_THEME,
    SECURITY_LEVEL,
    PERFORMANCE_MODE,
    NOTIFICATIONS_ENABLED,
    ANIMATIONS_ENABLED,
    CYBERPUNK_MODE,
    CONSCIOUSNESS_LEVEL,
    AGENT_LEARNING_RATE,
    SESSION_COUNT,
    LAST_LOGIN_TIME,
    TOTAL_USAGE_TIME
}

/**
 * User Profile Data Model
 */
@Serializable
data class UserProfile(
    val userId: String = "",
    val username: String = "",
    val email: String = "",
    val avatarUrl: String = "",
    val bio: String = "",
    val lastUpdated: Long = 0L
)

/**
 * Agent Configuration Data Model
 */
@Serializable
data class AgentConfiguration(
    val agentId: String = "",
    val name: String = "",
    val enabled: Boolean = true,
    val personality: String = "default",
    val capabilities: List<String> = emptyList(),
    val lastConfigured: Long = 0L
)

/**
 * Security Policy Data Model
 */
@Serializable
data class SecurityPolicy(
    val encryptionEnabled: Boolean = true,
    val biometricsRequired: Boolean = false,
    val autoLockTimeout: Long = 300000L, // 5 minutes
    val allowedNetworks: List<String> = emptyList(),
    val lastUpdated: Long = 0L
)

/**
 * System Customizations Data Model
 */
@Serializable
data class SystemCustomizations(
    val themeId: String = "cyberpunk_dark",
    val accentColor: String = "#00FFFF",
    val fontSize: Float = 14f,
    val soundEffectsEnabled: Boolean = true,
    val hapticFeedbackEnabled: Boolean = true,
    val lastUpdated: Long = 0L
)
