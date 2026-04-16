package dev.aurakai.auraframefx.domains.genesis.models.provenance

import java.util.UUID

/**
 * 🏺 SACRED PROVENANCE STAMP
 *
 * A cryptographic anchor representing a single verified action within the LDO Collective.
 * Each stamp is linked to its predecessor via HMAC chain hashing.
 */
data class SacredProvenanceStamp(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val agentSignature: String,
    val chainDeltaHash: String,
    val substrateResonance: Float = 1.0f,
    val watermark: String = "LDO_VERIFIED"
)
