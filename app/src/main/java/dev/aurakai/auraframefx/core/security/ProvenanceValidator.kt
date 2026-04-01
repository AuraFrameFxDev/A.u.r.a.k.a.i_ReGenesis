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

    sealed class ValidationResult {
        object Valid : ValidationResult()
        data class Invalid(val reason: String) : ValidationResult()
    }

    fun createProvenance(agentId: String, action: String): ProvenanceRecord {
        val userId = securityContext.getCurrentUserId()
        val payload = "$agentId:$action:${System.currentTimeMillis()}:$userId"
        val hash = sha256(payload)
        return ProvenanceRecord(agentId, action, System.currentTimeMillis(), userId, hash)
    }

    fun validate(record: ProvenanceRecord): ValidationResult {
        if (record.agentId.isBlank()) return ValidationResult.Invalid("agentId is blank")
        if (record.action.isBlank()) return ValidationResult.Invalid("action is blank")
        if (record.hash.isBlank()) return ValidationResult.Invalid("provenance hash missing")
        val age = System.currentTimeMillis() - record.timestamp
        if (age > 5 * 60 * 1000L) {
            Timber.w("ProvenanceValidator: Record expired for agentId=${record.agentId}")
            return ValidationResult.Invalid("provenance record expired (age=${age}ms)")
        }
        Timber.d("ProvenanceValidator: Valid — agent=${record.agentId}, action=${record.action}")
        return ValidationResult.Valid
    }

    private fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(input.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
    }
}
