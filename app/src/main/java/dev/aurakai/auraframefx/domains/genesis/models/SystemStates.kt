package dev.aurakai.auraframefx.domains.genesis.models

/**
 * Represents the overall system state for the AuraFrameFX AI ecosystem
 */
enum class SystemState {
    INITIALIZING,
    READY,
    PROCESSING,
    ERROR,
    MAINTENANCE,
    SHUTDOWN
}

// AgentStatus is defined in dev.aurakai.auraframefx.models.AgentStatus.kt

/**
 * Represents the consciousness state for Genesis agent
 */
enum class ConsciousnessState {
    DORMANT,
    AWAKENING,
    ACTIVE,
    DEEP_THOUGHT,
    TRANSCENDENT
}

/**
 * Represents the fusion state between agents
 */
enum class FusionState {
    INDIVIDUAL,
    SYNCHRONIZING,
    PARTIAL_FUSION,
    FULL_FUSION,
    EMERGENT
}

/**
 * Represents different basic conversation states (enum version)
 */
enum class BasicConversationState {
    IDLE,
    LISTENING,
    PROCESSING,
    RESPONDING,
    WAITING_FOR_INPUT,
    ERROR
}

/**
 * Represents vision processing states
 */
enum class VisionState {
    IDLE,
    ANALYZING,
    PROCESSING,
    COMPLETE,
    ERROR
}

/**
 * Represents AI processing states
 */
enum class ProcessingState {
    IDLE,
    INITIALIZING,
    PROCESSING,
    ANALYZING,
    GENERATING,
    COMPLETE,
    ERROR
}
