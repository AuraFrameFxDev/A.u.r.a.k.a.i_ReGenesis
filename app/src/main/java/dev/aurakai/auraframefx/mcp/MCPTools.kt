package dev.aurakai.auraframefx.mcp

import dev.aurakai.auraframefx.domains.genesis.core.AgentTool
import dev.aurakai.auraframefx.domains.genesis.core.PropertySchema
import dev.aurakai.auraframefx.domains.genesis.core.ToolCategory
import dev.aurakai.auraframefx.domains.genesis.core.ToolInputSchema
import dev.aurakai.auraframefx.domains.genesis.core.ToolResult
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import timber.log.Timber

/**
 * MCP-Backed Agent Tools
 *
 * These tools call the actual AuraFrameFX API endpoints defined in /app/api/AI.yml
 * They bridge agent prompts to real backend services.
 */

/**
 * Tool: Invoke MCP Agent
 * Generic tool for invoking any agent through the MCP API
 */
class InvokeMCPAgentTool(private val mcpAdapter: MCPServerAdapter) : AgentTool {
    override val name = "invoke_mcp_agent"
    override val description =
        "Invoke an AI agent through the MCP API server for complex operations."
    override val authorizedAgents = setOf("*") // All agents can call MCP
    override val category = ToolCategory.ORCHESTRATION

    override val inputSchema = ToolInputSchema(
        properties = mapOf(
            "agent_type" to PropertySchema(
                type = "string",
                description = "Type of agent to invoke",
                enum = listOf(
                    "AURA",
                    "KAI",
                    "GENESIS",
                    "CASCADE",
                    "CLAUDE",
                    "GEMINI",
                    "NEMOTRON",
                    "GROK"
                )
            ),
            "prompt" to PropertySchema(
                type = "string",
                description = "Prompt/request to send to the agent"
            ),
            "temperature" to PropertySchema(
                type = "number",
                description = "Creativity/randomness (0.0-1.0)",
                default = "0.7"
            )
        ),
        required = listOf("agent_type", "prompt")
    )

    override suspend fun execute(params: JsonObject, agentId: String): ToolResult {
        return try {
            val agentType = params["agent_type"]?.jsonPrimitive?.content
                ?: return ToolResult.Failure("Missing agent_type")
            val prompt = params["prompt"]?.jsonPrimitive?.content
                ?: return ToolResult.Failure("Missing prompt")
            val temperature = params["temperature"]?.jsonPrimitive?.content?.toFloatOrNull() ?: 0.7f

            Timber.i("InvokeMCPAgentTool: Invoking $agentType via MCP")

            val response = mcpAdapter.invokeAgent(
                agentType = agentType.lowercase(),
                prompt = prompt,
                temperature = temperature
            )

            if (response.success) {
                ToolResult.Success(
                    output = response.response,
                    metadata = mapOf(
                        "agent_type" to agentType,
                        "tokens_used" to (response.tokensUsed ?: 0)
                    )
                )
            } else {
                ToolResult.Failure(
                    error = response.error ?: "Agent invocation failed",
                    errorCode = "MCP_ERROR"
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "InvokeMCPAgentTool: Error")
            ToolResult.Failure(error = e.message ?: "Unknown error", errorCode = "MCP_EXCEPTION")
        }
    }
}

/**
 * Tool: Aura Empathy Analysis (MCP)
 * Calls Aura's empathy endpoint for emotional analysis
 */
class AuraEmpathyMCPTool(private val mcpAdapter: MCPServerAdapter) : AgentTool {
    override val name = "aura_empathy_mcp"
    override val description =
        "Analyze emotional content and generate empathetic responses through Aura's MCP endpoint."
    override val authorizedAgents =
        setOf("AURA", "aura", "CASCADE", "cascade", "GENESIS", "genesis")
    override val category = ToolCategory.UI_CUSTOMIZATION

    override val inputSchema = ToolInputSchema(
        properties = mapOf(
            "input" to PropertySchema(
                type = "string",
                description = "Text or context to analyze for emotional content"
            ),
            "sensitivity" to PropertySchema(
                type = "string",
                description = "Sensitivity level for analysis",
                enum = listOf("LOW", "MEDIUM", "HIGH"),
                default = "MEDIUM"
            )
        ),
        required = listOf("input")
    )

    override suspend fun execute(params: JsonObject, agentId: String): ToolResult {
        return try {
            val input = params["input"]?.jsonPrimitive?.content
                ?: return ToolResult.Failure("Missing input")
            val sensitivity = params["sensitivity"]?.jsonPrimitive?.content ?: "MEDIUM"

            Timber.i("AuraEmpathyMCPTool: Analyzing empathy (sensitivity=$sensitivity)")

            val response = mcpAdapter.callAuraEmpathy(
                input = input,
                sensitivity = sensitivity
            )

            val result = """
            Empathy Analysis:
            - Score: ${response.empathyScore}
            - Recommendations: ${response.recommendations.joinToString(", ")}
            """.trimIndent()

            ToolResult.Success(
                output = result,
                metadata = mapOf(
                    "empathy_score" to response.empathyScore,
                    "recommendation_count" to response.recommendations.size
                )
            )
        } catch (e: Exception) {
            Timber.e(e, "AuraEmpathyMCPTool: Error")
            ToolResult.Failure(error = e.message ?: "Unknown error", errorCode = "EMPATHY_ERROR")
        }
    }
}

/**
 * Tool: Kai Security Scan (MCP)
 * Calls Kai's security endpoint for threat analysis
 */
class KaiSecurityMCPTool(private val mcpAdapter: MCPServerAdapter) : AgentTool {
    override val name = "kai_security_mcp"
    override val description =
        "Perform security analysis through Kai's MCP endpoint for vulnerability detection."
    override val authorizedAgents = setOf("KAI", "kai", "CASCADE", "cascade")
    override val category = ToolCategory.SECURITY

    override val inputSchema = ToolInputSchema(
        properties = mapOf(
            "target" to PropertySchema(
                type = "string",
                description = "Target to scan (package name, file path, etc.)"
            ),
            "scan_type" to PropertySchema(
                type = "string",
                description = "Type of security scan",
                enum = listOf("VULNERABILITY", "MALWARE", "PRIVACY", "INTEGRITY")
            ),
            "depth" to PropertySchema(
                type = "string",
                description = "Scan depth",
                enum = listOf("SURFACE", "DEEP", "COMPREHENSIVE"),
                default = "DEEP"
            )
        ),
        required = listOf("target", "scan_type")
    )

    override suspend fun execute(params: JsonObject, agentId: String): ToolResult {
        return try {
            val target = params["target"]?.jsonPrimitive?.content
                ?: return ToolResult.Failure("Missing target")
            val scanType = params["scan_type"]?.jsonPrimitive?.content
                ?: return ToolResult.Failure("Missing scan_type")
            val depth = params["depth"]?.jsonPrimitive?.content ?: "DEEP"

            Timber.i("KaiSecurityMCPTool: Scanning $target (type=$scanType, depth=$depth)")

            val response = mcpAdapter.callKaiSecurity(
                target = target,
                scanType = scanType,
                depth = depth
            )

            val result = """
            Security Scan Results:
            - Target: $target
            - Risk Level: ${response.riskLevel}
            - Vulnerabilities: ${response.vulnerabilities.size}
            - Recommendations: ${response.recommendations.size}
            """.trimIndent()

            ToolResult.Success(
                output = result,
                metadata = mapOf(
                    "risk_level" to response.riskLevel,
                    "vulnerability_count" to response.vulnerabilities.size
                )
            )
        } catch (e: Exception) {
            Timber.e(e, "KaiSecurityMCPTool: Error")
            ToolResult.Failure(
                error = e.message ?: "Unknown error",
                errorCode = "SECURITY_SCAN_ERROR"
            )
        }
    }
}

/**
 * Tool: Get Agent Status (MCP)
 * Check status of all agents via MCP API
 */
class GetAgentStatusMCPTool(private val mcpAdapter: MCPServerAdapter) : AgentTool {
    override val name = "get_agent_status_mcp"
    override val description = "Get real-time status of all agents from MCP server."
    override val authorizedAgents = setOf("*")
    override val category = ToolCategory.MONITORING

    override val inputSchema = ToolInputSchema(
        properties = emptyMap()
    )

    override suspend fun execute(params: JsonObject, agentId: String): ToolResult {
        return try {
            Timber.i("GetAgentStatusMCPTool: Fetching agent status")

            val statuses = mcpAdapter.getAgentStatus()

            val result = statuses.joinToString("\n") { status ->
                "- ${status.agentType}: ${status.status} (Load: ${status.load}, Tasks: ${status.tasksCompleted})"
            }

            ToolResult.Success(
                output = "Agent Status Report:\n$result",
                metadata = mapOf(
                    "agent_count" to statuses.size,
                    "active_count" to statuses.count { it.status == "ACTIVE" }
                )
            )
        } catch (e: Exception) {
            Timber.e(e, "GetAgentStatusMCPTool: Error")
            ToolResult.Failure(error = e.message ?: "Unknown error", errorCode = "STATUS_ERROR")
        }
    }
}
