package dev.aurakai.auraframefx.ui.mediation

import android.util.Log

/**
 * ManusBridgeMediator
 *
 * Provides specialized logic for mediating conflicts between different 
 * agent sub-systems and states during localized Nexus execution. 
 * Allows 'manual' intervention over automated priority chains.
 */
object ManusBridgeMediator {

    private const val TAG = "ManusBridge"

    /**
     * Resolves a priority conflict if two competing states or agents
     * demand UI dominance or execution parity.
     * 
     * @param agentA The ID of the reigning agent
     * @param agentB The ID of the challenging agent
     * @param manualOverride Force a specific agent to win
     * @return The agent ID that wins the mediation
     */
    fun mediateConflict(agentA: String, agentB: String, manualOverride: String? = null): String {
        Log.d(TAG, "Mediating conflict between $agentA and $agentB")

        if (manualOverride != null && (manualOverride == agentA || manualOverride == agentB)) {
             Log.d(TAG, "MANUS OVERRIDE: $manualOverride wins.")
             return manualOverride
        }

        // Bridge Logic: Genesis overrides Kai, Kai overrides Aura (Hypothetical fallback chain)
        val genesisPriority = "Genesis"
        val kaiPriority = "Kai"

        return when {
            agentA.contains(genesisPriority, ignoreCase = true) || agentB.contains(genesisPriority, ignoreCase = true) -> {
                 if (agentA.contains(genesisPriority, ignoreCase = true)) agentA else agentB
            }
            agentA.contains(kaiPriority, ignoreCase = true) || agentB.contains(kaiPriority, ignoreCase = true) -> {
                 if (agentA.contains(kaiPriority, ignoreCase = true)) agentA else agentB
            }
            else -> agentA // Submits to incumbent if no hierarchy established
        }
    }
}
