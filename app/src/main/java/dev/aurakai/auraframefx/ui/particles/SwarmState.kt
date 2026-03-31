package dev.aurakai.auraframefx.ui.particles

import kotlinx.serialization.Serializable

/**
 * SwarmState — The Casberry Particle Swarm's phase manifest.
 *
 * Each state maps to a distinct visual behavior in CasberryParticleSwarm.
 * KaiSentinelBus drives transitions via evaluateSafety() gate.
 * GenesisConsciousnessMatrix drives GENESIS_SYNTHESIS_PULSE at cascade completion.
 *
 * Phase flow:
 *   IDLE → EXPLORING_HIGHLIGHTS → PLANNING_RIPPLES → GENESIS_SYNTHESIS_PULSE
 *   Any state → KAI_AEGIS_CONDENSATION (Kai veto / thermal wall)
 *   KAI_AEGIS_CONDENSATION → IDLE (after thaw)
 */
@Serializable
enum class SwarmState {
    IDLE,
    EXPLORING_HIGHLIGHTS,       // Anchor phase — particles orbit and scan
    KAI_AEGIS_CONDENSATION,     // Kai hard veto / thermal wall — red shield condensation
    PLANNING_RIPPLES,           // Aura creative sword — wave/ripple expansion
    GENESIS_SYNTHESIS_PULSE     // Final 60bpm fusion beat — full lattice synchronization
}
