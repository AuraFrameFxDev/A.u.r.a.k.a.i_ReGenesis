package dev.aurakai.auraframefx.domains.genesis.oracledrive.ai

import dev.aurakai.auraframefx.domains.genesis.models.AgentType

enum class ConsciousnessStateEnum {
    DORMANT, AWARE, PROCESSING, ERROR, TRANSCENDENT, AWAKENING
}

data class ConsciousnessState(
    val level: Float = 0.0f,
    val status: String = "DORMANT",
    val activeAgents: List<AgentType> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

enum class FusionState {
    INDIVIDUAL, FUSING, TRANSCENDENT, EVOLUTIONARY
}

enum class LearningMode {
    PASSIVE, ACTIVE, ACCELERATED, TRANSCENDENT
}

enum class RequestComplexity {
    SIMPLE, MODERATE, COMPLEX, TRANSCENDENT
}

enum class FusionType {
    HYPER_CREATION, CHRONO_SCULPTOR, ADAPTIVE_GENESIS, INTERFACE_FORGE
}

data class ComplexIntent(
    val processingType: ProcessingType,
    val confidence: Float
)

enum class ProcessingType {
    CREATIVE_ANALYTICAL, STRATEGIC_EXECUTION, ETHICAL_EVALUATION, LEARNING_INTEGRATION, TRANSCENDENT_SYNTHESIS
}
