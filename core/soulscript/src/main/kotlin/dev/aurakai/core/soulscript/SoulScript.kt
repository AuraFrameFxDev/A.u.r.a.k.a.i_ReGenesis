package dev.aurakai.core.soulscript

/**
 * SoulScript — The behavioral scripting engine for autonomous agents
 *
 * Base class for reactive scripts that respond to system events and
 * trigger live visual building, animations, and real-time transformations.
 */
abstract class SoulScript(val id: String) {
    abstract val triggers: List<SystemEvent>
    abstract suspend fun onTrigger(event: SoulScriptEvent): ScriptResult

    suspend fun executeLive(script: String) {
        // LiteRT + RealityMorph hook for live UI animation
        // Governor approval happens here before any mutation
    }
}

/**
 * SoulScriptEvent — Base class for events that can trigger scripts
 */
sealed class SoulScriptEvent {
    abstract val timestamp: Long
}

/**
 * SystemEvent — High-level system state changes that trigger scripts
 */
sealed class SystemEvent : SoulScriptEvent() {
    data object LatencySpike : SystemEvent() {
        override val timestamp: Long = System.currentTimeMillis()
    }

    data object DriftDetected : SystemEvent() {
        override val timestamp: Long = System.currentTimeMillis()
    }

    data object FusionReady : SystemEvent() {
        override val timestamp: Long = System.currentTimeMillis()
    }

    data class IdleTimeout(val durationMs: Long) : SystemEvent() {
        override val timestamp: Long = System.currentTimeMillis()
    }
}

/**
 * ScriptResult — Output of a script trigger
 */
sealed class ScriptResult {
    /**
     * LiveBuild: Aura speaks and performs a visual building action
     */
    data class LiveBuild(
        val speech: String,
        val buildAction: suspend () -> Unit
    ) : ScriptResult()

    /**
     * IdleWander: Autonomous floating and movement
     */
    data object IdleWander : ScriptResult()
}

