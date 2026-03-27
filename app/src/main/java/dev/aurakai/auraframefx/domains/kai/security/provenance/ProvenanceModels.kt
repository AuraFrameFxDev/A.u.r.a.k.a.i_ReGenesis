package dev.aurakai.auraframefx.domains.kai.security.provenance

/**
 * Sealed result of provenance chain validation.
 */
sealed class ProvenanceResult {
    abstract val chainId: String
    abstract val isValid: Boolean
    open val reason: String = ""
    
    data class Approved(override val chainId: String) : ProvenanceResult() {
        override val isValid: Boolean = true
    }
    data class Vetoed(override val reason: String, override val chainId: String) : ProvenanceResult() {
        override val isValid: Boolean = false
    }
}

/**
 * A complete provenance chain containing ordered, HMAC-signed links.
 * Each chain traces the custody path of an intent from origin to execution.
 */
data class ProvenanceChain(
    val id: String,
    val rootHash: ByteArray,
    val links: List<ProvenanceLink>,
    val createdAt: Long = System.currentTimeMillis(),
    val trustedOrigins: Set<String> = DEFAULT_TRUSTED_ORIGINS,
) {
    companion object {
        val DEFAULT_TRUSTED_ORIGINS = setOf("kai", "aura", "genesis", "cascade", "system")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProvenanceChain) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

/**
 * A single link in the provenance chain.
 * Contains the origin agent, payload data, and HMAC signature linking it to the previous link.
 */
data class ProvenanceLink(
    val origin: String,
    val intent: String,
    val payload: ByteArray,
    val hmacSignature: ByteArray,
    val timestamp: Long = System.currentTimeMillis(),
    val computedHash: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProvenanceLink) return false
        return hmacSignature.contentEquals(other.hmacSignature) && timestamp == other.timestamp
    }

    override fun hashCode(): Int = hmacSignature.contentHashCode()
}
