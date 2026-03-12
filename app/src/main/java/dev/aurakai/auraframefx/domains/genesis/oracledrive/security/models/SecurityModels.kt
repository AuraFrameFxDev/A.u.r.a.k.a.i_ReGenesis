package dev.aurakai.auraframefx.domains.genesis.oracledrive.security.models

import kotlinx.serialization.Serializable

/**
 * Security check result
 */
@Serializable
data class SecurityCheck(
    val passed: Boolean,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Access check result
 */
@Serializable
data class AccessCheck(
    val granted: Boolean,
    val reason: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Deletion validation result
 */
@Serializable
data class DeletionValidation(
    val allowed: Boolean,
    val reason: String? = null,
    val requiresConfirmation: Boolean = true
)
