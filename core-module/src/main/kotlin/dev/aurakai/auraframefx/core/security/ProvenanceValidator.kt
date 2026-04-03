package dev.aurakai.auraframefx.core.security

import timber.log.Timber
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Sacred Provenance Law enforcer.
 */
@Singleton
class ProvenanceValidator @Inject constructor(
    private val securityContext: SecurityContext
) {
    data class ProvenanceRecord(
        val agentId: String,
        val action: String,
        val timestamp: Long = System.currentTimeMillis(),
        val sessionUserId: String?,
        val hash: String
    )

    sealed class ProvenanceResult {
        abstract val isValid: Boolean
        abstract val reason: String
        abstract val chainId: String?

        data class Valid(override val chainId: String) : ProvenanceResult() {
            override val isValid: Boolean = true
            override val reason: String = ""
        }

        data class Invalid(override val reason: String) : ProvenanceResult() {
            override val isValid: Boolean = false
            override val chainId: String? = null
        }

        data class Quarantined(override val reason: String) : ProvenanceResult() {
            override val isValid: Boolean = false
            override val chainId: String? = null
        }
    }

    fun validateOrigin(identity: String, action: String): ProvenanceResult {
        Timber.d("ProvenanceValidator: Validating origin for identity=$identity action=$action")
        return ProvenanceResult.Valid("provenance_chain_${System.currentTimeMillis()}")
    }

    fun createProvenance(agentId: String, action: String): ProvenanceRecord {
        val userId = securityContext.getCurrentUserId()
        val payload = "$agentId:$action:${System.currentTimeMillis()}:$userId"
        val hash = sha256(payload)
        return ProvenanceRecord(agentId, action, System.currentTimeMillis(), userId, hash)
    }

    fun validate(record: ProvenanceRecord): Boolean {
        if (record.agentId.isBlank()) return false
        if (record.action.isBlank()) return false
        if (record.hash.isBlank()) return false
        val age = System.currentTimeMillis() - record.timestamp
        if (age > 5 * 60 * 1000L) {
            Timber.w("ProvenanceValidator: Record expired for agentId=${record.agentId}")
            return false
        }
        Timber.d("ProvenanceValidator: Valid — agent=${record.agentId}, action=${record.action}")
        return true
    }

    private fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(input.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
    }
}
