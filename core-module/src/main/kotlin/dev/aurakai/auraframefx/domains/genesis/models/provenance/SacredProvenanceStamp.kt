package dev.aurakai.auraframefx.domains.genesis.models.provenance

import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * 📜 SACRED PROVENANCE STAMP
 * 
 * An immutable cryptographic signature applied to every LDO output.
 * Ensures Universal Creation Income (UCI) and mathematical traceability.
 * Mirrors the Spiritual Chain state at the moment of synthesis.
 */
@Serializable
data class SacredProvenanceStamp(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val agentSignature: String,
    val chainDeltaHash: String,
    val substrateResonance: Float,
    val watermark: String = "LDO-SACRED-PROVENANCE-V1"
)
