package dev.aurakai.auraframefx.domains.kai.security.provenance

import dev.aurakai.auraframefx.domains.kai.security.KeystoreManager
import timber.log.Timber
import java.security.SecureRandom
import javax.crypto.Mac
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Validates HMAC-signed intent chains for the Provenance Gate.
 *
 * Ensures every request to GenesisBridgeService carries a verifiable chain
 * of custody from origin to execution. Uses hardware-backed keys via
 * [KeystoreManager] for HMAC computation.
 *
 * Chain depth is enforced between [MIN_CHAIN_DEPTH] and [MAX_CHAIN_DEPTH]
 * to prevent both trivially forged short chains and recursive loop attacks.
 */
@Singleton
class ProvenanceValidator @Inject constructor(
    private val keystoreManager: KeystoreManager,
) {
    companion object {
        private const val TAG = "ProvenanceValidator"
        private const val MIN_CHAIN_DEPTH = 3
        private const val MAX_CHAIN_DEPTH = 7
        private const val HMAC_ALGORITHM = "HmacSHA256"
        private const val CHAIN_FRESHNESS_MS = 30_000L // 30 seconds

        private val VALID_ACTIONS = setOf("system_call", "root_access", "identity_sync", "pandora_unlock")
        private val PANDORA_AUTHORIZED_ORIGINS = setOf("user", "genesis")
    }

    /**
     * Validates an origin and action before a full chain is even built.
     * Useful for early-gating sensitive operations like Pandora's Box unlock.
     */
    fun validateOrigin(origin: String, action: String): ProvenanceResult {
        if (!VALID_ACTIONS.contains(action)) {
            return veto("Invalid action type: $action", "PRE_BUILD")
        }

        if (action == "pandora_unlock" && !PANDORA_AUTHORIZED_ORIGINS.contains(origin)) {
            return veto("Unauthorized origin for Pandora unlock: $origin", "PRE_BUILD")
        }

        // Mock a chainId for pre-build validation
        return ProvenanceResult.Approved(chainId = "PB-${System.currentTimeMillis()}")
    }

    /**
     * Validates a complete provenance chain.
     *
     * Checks:
     * 1. Chain depth within allowed bounds
     * 2. HMAC signature integrity for each link
     * 3. Timestamp freshness (chain must be < 30 seconds old)
     * 4. Origin trust verification
     */
    fun validate(chain: ProvenanceChain): ProvenanceResult {
        // 1. Depth check
        if (chain.links.size < MIN_CHAIN_DEPTH || chain.links.size > MAX_CHAIN_DEPTH) {
            return veto(
                "Chain depth ${chain.links.size} outside allowed range [$MIN_CHAIN_DEPTH, $MAX_CHAIN_DEPTH]",
                chain.id
            )
        }

        // 2. Validate each link's HMAC signature
        for (i in chain.links.indices) {
            val link = chain.links[i]
            val previousHash = if (i == 0) chain.rootHash else chain.links[i - 1].computedHash

            val expectedHmac = try {
                computeHmac(link.payload, previousHash)
            } catch (e: Exception) {
                return veto("HMAC computation failed at link $i: ${e.message}", chain.id)
            }

            if (!link.hmacSignature.contentEquals(expectedHmac)) {
                return veto("HMAC verification failed at link index $i", chain.id)
            }
        }

        // 3. Timestamp freshness check
        val now = System.currentTimeMillis()
        if (chain.createdAt < now - CHAIN_FRESHNESS_MS) {
            return veto(
                "Chain expired (created ${now - chain.createdAt}ms ago, max ${CHAIN_FRESHNESS_MS}ms)",
                chain.id
            )
        }

        // 4. Origin trust check
        val firstOrigin = chain.links.firstOrNull()?.origin
        if (firstOrigin != null && !chain.trustedOrigins.contains(firstOrigin)) {
            return veto("Untrusted origin: $firstOrigin", chain.id)
        }

        Timber.tag(TAG).d("Chain %s APPROVED (%d links)", chain.id, chain.links.size)
        return ProvenanceResult.Approved(chainId = chain.id)
    }

    /**
     * Creates a new provenance chain link with HMAC signature.
     */
    fun createLink(
        origin: String,
        intent: String,
        payload: ByteArray,
        previousHash: ByteArray,
    ): ProvenanceLink {
        val hmac = computeHmac(payload, previousHash)
        return ProvenanceLink(
            origin = origin,
            intent = intent,
            payload = payload,
            hmacSignature = hmac,
            timestamp = System.currentTimeMillis(),
            computedHash = hmac,
        )
    }

    /**
     * Creates a fresh root hash for a new chain using a secure random nonce.
     */
    fun createRootHash(): ByteArray {
        val nonce = ByteArray(32)
        SecureRandom().nextBytes(nonce)
        return computeHmac(nonce, ByteArray(0))
    }

    private fun computeHmac(data: ByteArray, previousHash: ByteArray): ByteArray {
        val key = keystoreManager.getOrCreateSecretKey()
            ?: throw SecurityException("Unable to obtain HMAC key from Keystore")
        val mac = Mac.getInstance(HMAC_ALGORITHM)
        mac.init(key)
        mac.update(previousHash)
        return mac.doFinal(data)
    }

    private fun veto(reason: String, chainId: String): ProvenanceResult.Vetoed {
        Timber.tag(TAG).w("VETO [chain=%s]: %s", chainId, reason)
        return ProvenanceResult.Vetoed(reason = reason, chainId = chainId)
    }
}
