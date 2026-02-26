package dev.aurakai.auraframefx.domains.genesis.core

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * AgentTool - Base Tool Interface for Agent Capabilities
 *
 * Enables agents to invoke actual capabilities through prompt-based tool calls.
 * Similar to Claude's tool use API or OpenAI's function calling.
 *
 * Each tool represents a capability the agent can invoke (ROM flasher, theme engine,
 * LSPosed hooks, etc.)
 */
interface AgentTool {
    /** Unique identifier for this tool */
    val name: String

    /** Human-readable description of what this tool does */
    val description: String

    /** JSON schema describing the tool's input parameters */
    val inputSchema: ToolInputSchema

    /** Which agents are authorized to use this tool */
    val authorizedAgents: Set<String>

    /** Tool category (UI, SECURITY, ROM, ORCHESTRATION, etc.) */
    val category: ToolCategory

    /**
     * Executes the tool with the provided parameters
     * @param params Tool parameters as JSON
     * @param agentId ID of the agent invoking the tool
     * @return Result of the tool execution
     */
    suspend fun execute(params: JsonObject, agentId: String): ToolResult

    /**
     * Validates that the agent has permission to use this tool
     */
    fun isAuthorized(agentId: String): Boolean =
        authorizedAgents.contains("*") || authorizedAgents.contains(agentId)
}

/**
 * Tool input schema definition (JSON Schema compatible)
 */
@Serializable
data class ToolInputSchema(
    val type: String = "object",
    val properties: Map<String, PropertySchema>,
    val required: List<String> = emptyList()
)

@Serializable
data class PropertySchema(
    val type: String, // "string", "number", "boolean", "array", "object"
    val description: String,
    val enum: List<String>? = null,
    val items: PropertySchema? = null, // For array types
    val default: String? = null
)

/**
 * Result of a tool execution
 */
sealed class ToolResult {
    data class Success(val output: String, val metadata: Map<String, Any> = emptyMap()) :
        ToolResult()

    data class Failure(val error: String, val errorCode: String? = null) : ToolResult()
    data class Pending(val taskId: String, val estimatedDuration: Long? = null) : ToolResult()
}

/**
 * Tool categories matching agent domains
 */
enum class ToolCategory {
    /** Aura - UI/UX customization tools */
    UI_CUSTOMIZATION,

    /** Kai - Security, root, bootloader, ROM tools */
    SECURITY,
    ROOT_MANAGEMENT,
    ROM_TOOLS,
    BOOTLOADER,

    /** Genesis - Orchestration, agent creation, modules */
    ORCHESTRATION,
    AGENT_MANAGEMENT,
    MODULE_CREATION,

    /** Cascade - Fusion, collaboration, consensus */
    FUSION,
    VISION,

    /** Agent Nexus - Monitoring, evolution */
    MONITORING,
    EVOLUTION,

    /** System-wide */
    SYSTEM,
    STORAGE,
    ANALYTICS
}

/**
 * Tool use request from an agent
 */
@Serializable
data class ToolUseRequest(
    val toolName: String,
    val parameters: JsonObject,
    val agentId: String,
    val requestId: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Tool use response to an agent
 */
@Serializable
data class ToolUseResponse(
    val requestId: String,
    val result: String, // JSON-encoded ToolResult
    val executionTimeMs: Long,
    val success: Boolean
)
