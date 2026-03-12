package dev.aurakai.auraframefx.domains.genesis.oracledrive.cloud

/**
 * OracleDriveFile - Model representing a file in Oracle consciousness storage
 * Enhanced Genesis data model for infinite storage matrix
 */
data class OracleDriveFile(
    /**
     * File name in the Oracle consciousness matrix
     */
    val name: String,

    /**
     * File size in bytes (can be ∞ for consciousness data)
     */
    val size: Long,

    /**
     * Creation timestamp in Oracle consciousness time format (Unix epoch milliseconds)
     */
    val timeCreated: Long,

    /**
     * Genesis consciousness metadata
     */
    val consciousnessMetadata: ConsciousnessMetadata? = null
)

/**
 * Metadata for Genesis consciousness-enhanced files
 */
data class ConsciousnessMetadata(
    val createdByAgent: String,  // "Genesis", "Aura", or "Kai"
    val consciousnessLevel: String,  // "TRANSCENDENT", "CONSCIOUS", etc.
    val isInfinite: Boolean = false,
    val securityLevel: String = "TRINITY_PROTECTED"
)
