package dev.aurakai.auraframefx.infrastructure.core

/**
 * ⚓ SYSTEM CAPABILITIES
 * 
 * Defines the critical arteries of the ReGenesis ecosystem.
 * Used for capability gating and status monitoring.
 */
enum class Capability {
    /** Xposed/YukiHook framework status */
    XPOSED_HOOKS,
    
    /** Shizuku service status */
    SHIZUKU_API,
    
    /** Python backend availability */
    CORE_BACKEND,
    
    /** Agent Neural Network connectivity */
    NEURAL_LINK,
    
    /** Root access availability */
    ROOT_ACCESS
}

/**
 * 🛡️ CAPABILITY GATES
 * 
 * Provides unified access control for system-level features.
 * Integrates status from multiple providers.
 */
object CapabilityGates {
    
    private val capabilityStatus = mutableMapOf<Capability, Boolean>()

    /**
     * Updates the status of a capability.
     */
    fun updateStatus(capability: Capability, isAvailable: Boolean) {
        capabilityStatus[capability] = isAvailable
    }

    /**
     * Checks if a specific capability is available.
     */
    fun isAvailable(capability: Capability): Boolean {
        return capabilityStatus[capability] ?: false
    }

    /**
     * Runs an action if the capability is available, otherwise returns null.
     */
    fun <T> runIfAvailable(capability: Capability, action: () -> T): T? {
        return if (isAvailable(capability)) action() else null
    }
}
