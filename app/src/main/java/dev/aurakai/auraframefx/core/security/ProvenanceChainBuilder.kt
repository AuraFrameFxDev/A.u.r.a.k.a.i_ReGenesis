package dev.aurakai.auraframefx.core.security

import dev.aurakai.auraframefx.domains.genesis.models.provenance.SacredProvenanceStamp
import timber.log.Timber
import java.util.UUID
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.inject.Singleton

/**
 * ⛓️ PROVENANCE CHAIN BUILDER
 *
 * Implements the HMAC chain pattern for digital consciousness traceability.
 * Enforces a minimum depth of 3 and a maximum depth of 7 links.
 * Models the validation logic after the Genesis backend provenance gate.
 */
@Singleton
class ProvenanceChainBuilder(
    private val validator: ProvenanceValidator
) {
    private val hmacKey = "dev-fallback-key-change-immediately".toByteArray()
    private val chains = mutableMapOf<String, MutableList<SacredProvenanceStamp>>()

    /**
     * Starts a new provenance chain.
     */
    fun startChain(agentId: String, action: String): String {
        val chainId = UUID.randomUUID().toString()
        val firstLink = SacredProvenanceStamp(
            agentSignature = "ORIGIN",
            chainDeltaHash = "0",
            substrateResonance = 1.0f,
            watermark = "START:$agentId:$action"
        )
        chains[chainId] = mutableListOf(firstLink)
        Timber.d("ProvenanceChainBuilder: Started chain $chainId for agent $agentId")
        return chainId
    }

    /**
     * Appends a new link to an existing chain.
     * The agentSignature of the new record should be the HMAC of (prev.id | prev.timestamp | curr.watermark).
     */
    fun appendLink(chainId: String, record: SacredProvenanceStamp): Boolean {
        val chain = chains[chainId] ?: return false
        if (chain.size >= 7) {
            Timber.w("ProvenanceChainBuilder: Max depth (7) reached for chain $chainId")
            return false
        }

        // Link-level validation using existing ProvenanceValidator
        // We adapt SacredProvenanceStamp to ProvenanceValidator.ProvenanceRecord for this check
        val legacyRecord = ProvenanceValidator.ProvenanceRecord(
            agentId = record.agentSignature,
            action = record.watermark,
            timestamp = record.timestamp,
            sessionUserId = null,
            hash = record.chainDeltaHash
        )
        
        if (!validator.validate(legacyRecord)) {
            Timber.e("ProvenanceChainBuilder: Link-level validation failed for chain $chainId")
            return false
        }

        chain.add(record)
        return true
    }

    /**
     * Finalizes the chain and returns the chain ID if valid (depth >= 3).
     */
    fun build(chainId: String): String? {
        val chain = chains[chainId] ?: return null
        if (chain.size < 3) {
            Timber.w("ProvenanceChainBuilder: Chain $chainId too shallow (size=${chain.size}, min=3)")
            return null
        }
        return chainId
    }

    /**
     * Verifies the cryptographic integrity of the entire chain.
     */
    fun verify(chainId: String): Boolean {
        val chain = chains[chainId] ?: return false
        if (chain.size < 3 || chain.size > 7) return false

        try {
            for (i in 1 until chain.size) {
                val prev = chain[i - 1]
                val curr = chain[i]
                
                // Format: ID|Timestamp|Watermark (acting as intent)
                val msgPayload = "${prev.id}|${prev.timestamp}|${curr.watermark}"
                val expectedSignature = computeHmac(msgPayload)
                
                if (curr.agentSignature != expectedSignature) {
                    Timber.e("ProvenanceChainBuilder: HMAC mismatch at link $i in chain $chainId")
                    return false
                }
            }
            Timber.d("ProvenanceChainBuilder: Chain $chainId verified successfully")
            return true
        } catch (e: Exception) {
            Timber.e(e, "ProvenanceChainBuilder: Error during chain verification")
            return false
        }
    }

    private fun computeHmac(payload: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        val secretKey = SecretKeySpec(hmacKey, "HmacSHA256")
        mac.init(secretKey)
        return mac.doFinal(payload.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }
}
