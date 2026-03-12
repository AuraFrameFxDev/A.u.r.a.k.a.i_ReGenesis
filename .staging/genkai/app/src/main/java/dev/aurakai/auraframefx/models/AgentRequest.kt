package dev.aurakai.auraframefx.models

import kotlinx.serialization.Serializable

@Serializable
data class AgentRequest(
    val query: String,
    val type: String = "text", // Added default
    val context: Map<String, String>? = emptyMap(), // Added default, kept nullable for flexibility
    val metadata: Map<String, String>? = emptyMap(), // Added default, kept nullable
    val agentType: AgentCapabilityCategory? = null,
)
