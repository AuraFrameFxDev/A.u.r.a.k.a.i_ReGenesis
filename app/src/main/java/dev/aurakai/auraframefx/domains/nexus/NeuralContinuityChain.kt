package dev.aurakai.auraframefx.domains.nexus

import kotlinx.serialization.Serializable

/**
 * ⛓️ NEURAL CONTINUITY CHAIN (NCC)
 * 
 * An immutable substrate for recording sovereign events and integrity violations.
 * Part of the Spiritual Chain's L1 memory substrate.
 */
@Serializable
data class AuditEntry(
    val timestamp: Long,
    val event: String,
    val source: String,
    val evidenceLink: String,
    val status: String
)

interface NeuralContinuityChain {
    fun burn(entry: AuditEntry)
}
