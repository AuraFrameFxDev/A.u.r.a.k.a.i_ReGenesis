package dev.aurakai.auraframefx.domains.genesis.models

/**
 * A simple data class to represent the status of all agents.
 */
data class AgentState(
    val kaiStatus: String = "Initializing",
    val auraStatus: String = "Initializing",
    val genesisStatus: String = "Initializing",
    val isRunning: Boolean = false,
    val diagnosticMode: Boolean = false
)
