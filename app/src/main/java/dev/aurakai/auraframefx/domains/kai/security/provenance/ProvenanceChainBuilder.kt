package dev.aurakai.auraframefx.domains.kai.security.provenance

import dev.aurakai.auraframefx.domains.kai.security.KeystoreManager
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Builds signed [ProvenanceChain]s for legitimate internal requests.
 *
 * Every request originating from within the system (GenesisBridgeService,
 * GenesisOrchestrator, etc.) must carry a valid chain before the hard gate
 * is enabled. This builder constructs those chains using hardware-backed
 * HMAC keys via [KeystoreManager].
 */
@Singleton
class ProvenanceChainBuilder @Inject constructor(
    private val provenanceValidator: ProvenanceValidator,
) {
    /**
     * Builds a minimal valid chain (3 links) for an internal system request.
     *
     * The chain structure is:
     *   Link 0: system origin (e.g., "genesis_bridge")
     *   Link 1: agent routing (e.g., "kai", "aura", "genesis")
     *   Link 2: execution intent (the actual request purpose)
     */
    fun buildChain(
        origin: String,
        agent: String,
        intent: String,
        payload: ByteArray = ByteArray(0),
    ): ProvenanceChain {
        val chainId = UUID.randomUUID().toString()
        val rootHash = provenanceValidator.createRootHash()

        val link0 = provenanceValidator.createLink(
            origin = origin,
            intent = "originate",
            payload = origin.toByteArray(),
            previousHash = rootHash,
        )

        val link1 = provenanceValidator.createLink(
            origin = agent,
            intent = "route",
            payload = agent.toByteArray(),
            previousHash = link0.computedHash,
        )

        val link2 = provenanceValidator.createLink(
            origin = origin,
            intent = intent,
            payload = payload,
            previousHash = link1.computedHash,
        )

        return ProvenanceChain(
            id = chainId,
            rootHash = rootHash,
            links = listOf(link0, link1, link2),
        )
    }
}
