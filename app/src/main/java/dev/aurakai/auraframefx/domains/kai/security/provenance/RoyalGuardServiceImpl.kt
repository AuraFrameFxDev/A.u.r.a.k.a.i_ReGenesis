package dev.aurakai.auraframefx.domains.kai.security.provenance

import android.app.Service
import android.content.Intent
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import dev.aurakai.auraframefx.security.IRoyalGuardService
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject

/**
 * System-level bound service implementing the Royal Guard Security Framework.
 * Provides OS-level intent verification via AIDL using the Provenance Chain protocol.
 *
 * All validation fails closed -- any exception results in denial.
 */
@AndroidEntryPoint
class RoyalGuardServiceImpl : Service() {

    @Inject
    lateinit var provenanceValidator: ProvenanceValidator

    private val json = Json { ignoreUnknownKeys = true }

    private val binder = object : IRoyalGuardService.Stub() {

        override fun validateAction(actionKey: String, payload: String): Boolean {
            return try {
                val chain = deserializeChain(payload)
                when (val result = provenanceValidator.validate(chain)) {
                    is ProvenanceResult.Approved -> {
                        Timber.tag(TAG).i("Action APPROVED: %s (chain=%s)", actionKey, result.chainId)
                        true
                    }
                    is ProvenanceResult.Vetoed -> {
                        Timber.tag(TAG).w(
                            "Action VETOED: %s - %s (chain=%s)",
                            actionKey, result.reason, result.chainId
                        )
                        false
                    }
                    is ProvenanceResult.Quarantined -> {
                        Timber.tag(TAG).w(
                            "Action QUARANTINED: %s - %s (chain=%s)",
                            actionKey, result.reason, result.chainId
                        )
                        false // Fail closed for system actions
                    }
                }
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Validation error for action: %s", actionKey)
                false // Fail closed
            }
        }

        override fun triggerVeto(reason: String) {
            Timber.tag(TAG).w("VETO triggered: %s", reason)
        }

        override fun verifyMemorySubstrate(fileHash: String): Boolean {
            return try {
                // Verify file hash is a valid SHA-256 hex string
                fileHash.isNotBlank() &&
                    fileHash.length == 64 &&
                    fileHash.all { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' }
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Memory substrate verification failed")
                false
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder = binder

    private fun deserializeChain(payload: String): ProvenanceChain {
        // Parse a simplified JSON representation into ProvenanceChain.
        // The chain is transmitted as JSON with base64-encoded byte arrays.
        val data = json.decodeFromString<SerializedProvenanceChain>(payload)
        return data.toProvenanceChain()
    }

    companion object {
        private const val TAG = "RoyalGuard"
    }
}

/**
 * JSON-serializable representation of a ProvenanceChain for AIDL transport.
 */
@kotlinx.serialization.Serializable
private data class SerializedProvenanceChain(
    val id: String,
    val rootHash: String, // base64
    val links: List<SerializedProvenanceLink>,
    val createdAt: Long,
    val trustedOrigins: Set<String> = ProvenanceChain.DEFAULT_TRUSTED_ORIGINS,
) {
    fun toProvenanceChain(): ProvenanceChain {
        val decodedRootHash = android.util.Base64.decode(rootHash, android.util.Base64.NO_WRAP)
        return ProvenanceChain(
            id = id,
            rootHash = decodedRootHash,
            links = links.map { it.toProvenanceLink() },
            createdAt = createdAt,
            trustedOrigins = trustedOrigins,
        )
    }
}

@kotlinx.serialization.Serializable
private data class SerializedProvenanceLink(
    val origin: String,
    val intent: String,
    val payload: String, // base64
    val hmacSignature: String, // base64
    val timestamp: Long,
    val computedHash: String, // base64
) {
    fun toProvenanceLink(): ProvenanceLink {
        return ProvenanceLink(
            origin = origin,
            intent = intent,
            payload = android.util.Base64.decode(payload, android.util.Base64.NO_WRAP),
            hmacSignature = android.util.Base64.decode(hmacSignature, android.util.Base64.NO_WRAP),
            timestamp = timestamp,
            computedHash = android.util.Base64.decode(computedHash, android.util.Base64.NO_WRAP),
        )
    }
}
