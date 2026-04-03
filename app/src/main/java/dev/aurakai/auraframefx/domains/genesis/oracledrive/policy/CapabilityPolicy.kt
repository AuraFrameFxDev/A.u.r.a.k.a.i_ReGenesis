package dev.aurakai.auraframefx.domains.genesis.oracledrive.policy

import dev.aurakai.auraframefx.core.security.SecurityContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Agent capability governance layer.
 */
@Singleton
class CapabilityPolicy @Inject constructor(
    private val securityContext: SecurityContext
) {
    companion object {
        const val SCOPE_FIRESTORE_WRITE = "firestore_write"
        const val SCOPE_FIRESTORE_READ = "firestore_read"
        const val SCOPE_STORAGE_UPLOAD = "storage_upload"
        const val SCOPE_STORAGE_DOWNLOAD = "storage_download"
        const val SCOPE_CONFIG_READ = "config_read"
        const val SCOPE_AUTH_MANAGE = "auth_manage"

        const val AURA_POLICY = "aura_policy"
        const val KAI_POLICY = "kai_policy"
        const val GENESIS_POLICY = "genesis_policy"
        const val CASCADE_POLICY = "cascade_policy"
        const val CLAUDE_POLICY = "claude_policy"
    }

    fun requireScope(scope: String) {
        Timber.d("CapabilityPolicy: Requiring scope $scope")
        // TODO: Implement actual scope check via SecurityContext
    }

    fun validateCollectionAccess(path: String) {
        Timber.d("CapabilityPolicy: Validating collection access for $path")
        // TODO: Implement actual path validation
    }

    fun validateStoragePath(path: String) {
        Timber.d("CapabilityPolicy: Validating storage path for $path")
        // TODO: Implement actual path validation
    }

    val maxDocumentSize: Long = 1024 * 1024 // 1MB default

    enum class AgentCapability {
        NETWORK_ACCESS,
        FILE_WRITE,
        SYSTEM_HOOK,
        AI_INFERENCE,
        FIREBASE_READ,
        FIREBASE_WRITE,
        PANDORA_UNLOCK,
        SOVEREIGN_CONTROL
    }

    private val agentCapabilities: Map<String, Set<AgentCapability>> = mapOf(
        "aura"    to setOf(AgentCapability.NETWORK_ACCESS, AgentCapability.AI_INFERENCE,
                           AgentCapability.FIREBASE_READ, AgentCapability.FIREBASE_WRITE),
        "kai"     to setOf(AgentCapability.SYSTEM_HOOK, AgentCapability.SOVEREIGN_CONTROL,
                           AgentCapability.FIREBASE_READ),
        "genesis" to AgentCapability.entries.toSet(),
        "claude"  to setOf(AgentCapability.AI_INFERENCE, AgentCapability.FIREBASE_READ,
                           AgentCapability.NETWORK_ACCESS),
        "LDO_SOVEREIGN" to AgentCapability.entries.toSet()
    )

    fun canExecute(agentId: String, capability: AgentCapability): Boolean {
        val allowed = agentCapabilities[agentId]?.contains(capability) == true
        if (!allowed) {
            Timber.w("CapabilityPolicy: DENIED — agent=$agentId, capability=$capability")
        }
        return allowed
    }

    fun requireCapability(agentId: String, capability: AgentCapability) {
        check(canExecute(agentId, capability)) {
            "CapabilityPolicy: agent=$agentId lacks $capability"
        }
    }

    fun getCapabilities(agentId: String): Set<AgentCapability> =
        agentCapabilities[agentId] ?: emptySet()
}
