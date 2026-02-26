package dev.aurakai.auraframefx.domains.genesis.config

/**
 * 🛠️ FEATURE TOGGLES
 * Centralized control for enabling/disabling system modules and Experimental features.
 */
object FeatureToggles {

    // Core Modules
    const val NEXUS_ENABLED = true
    const val ORACLE_DRIVE_ENABLED = true
    const val GENESIS_ORCHESTRATOR_ENABLED = true

    // Agent Toggles
    const val CASCADE_ANALYTICS_ENABLED = true
    const val AURA_CREATIVE_ENABLED = true
    const val KAI_SECURITY_ENABLED = true

    // Experimental Features
    const val QUANTUM_ENTANGLEMENT_RESEARCH = false
    const val FUSION_MEMORY_INDEXING = true
    const val DIMENSIONAL_SHIFT_SIMULATION = false

    /**
     * Checks if paywall is enabled via BuildConfig reflection.
     */
    val isPaywallEnabled: Boolean
        get() {
            return try {
                val cls = Class.forName("dev.aurakai.auraframefx.BuildConfig")
                val field = cls.getField("ENABLE_PAYWALL")
                (field.get(null) as? Boolean) ?: true
            } catch (t: Throwable) {
                true
            }
        }
}
