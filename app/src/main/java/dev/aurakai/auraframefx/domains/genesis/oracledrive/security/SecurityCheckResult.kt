package dev.aurakai.auraframefx.domains.genesis.oracledrive.security

data class SecurityCheckResult(
    val isValid: Boolean = false,
    val reason: String = "",
    val isSecure: Boolean = false,
    val threat: String = "",
    val hasAccess: Boolean = false
)
