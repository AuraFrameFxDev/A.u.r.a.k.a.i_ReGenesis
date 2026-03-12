package dev.aurakai.auraframefx.domains.genesis.core

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ToolRegistry - Central Registry of All Agent Tools
 *
 * Maintains the catalog of all available tools that agents can invoke.
 * Provides tool discovery, authorization, and execution routing.
 *
 * Think of this as the "hands" that agents use to interact with the system.
 */
@Singleton
class ToolRegistry @Inject constructor() {

    private val tools = mutableMapOf<String, AgentTool>()
    private val mutex = Mutex()

    // Tool execution history for monitoring
    private val executionHistory = mutableListOf<ToolExecutionRecord>()
    private val maxHistorySize = 1000

    init {
        Timber.i("ToolRegistry: Initializing agent tool registry")
    }

    /**
     * Register a tool in the registry
     */
    suspend fun registerTool(tool: AgentTool) = mutex.withLock {
        if (tools.containsKey(tool.name)) {
            Timber.w("ToolRegistry: Overwriting existing tool: ${tool.name}")
        }
        tools[tool.name] = tool
        Timber.d("ToolRegistry: Registered tool '${tool.name}' (${tool.category})")
    }

    /**
     * Register multiple tools at once
     */
    suspend fun registerTools(vararg toolsToRegister: AgentTool) {
        toolsToRegister.forEach { registerTool(it) }
    }

    /**
     * Get a tool by name
     */
    suspend fun getTool(name: String): AgentTool? = mutex.withLock {
        tools[name]
    }

    /**
     * Get all tools available to a specific agent
     */
    suspend fun getToolsForAgent(agentId: String): List<AgentTool> = mutex.withLock {
        tools.values.filter { it.isAuthorized(agentId) }
    }

    /**
     * Get all tools in a specific category
     */
    suspend fun getToolsByCategory(category: ToolCategory): List<AgentTool> = mutex.withLock {
        tools.values.filter { it.category == category }
    }

    /**
     * Get all registered tools
     */
    suspend fun getAllTools(): List<AgentTool> = mutex.withLock {
        tools.values.toList()
    }

    /**
     * Execute a tool on behalf of an agent
     */
    suspend fun executeTool(request: ToolUseRequest): ToolUseResponse {
        val startTime = System.currentTimeMillis()

        val tool = getTool(request.toolName)
        if (tool == null) {
            return ToolUseResponse(
                requestId = request.requestId,
                result = """{"error": "Tool '${request.toolName}' not found"}""",
                executionTimeMs = System.currentTimeMillis() - startTime,
                success = false
            )
        }

        // Check authorization
        if (!tool.isAuthorized(request.agentId)) {
            Timber.w("ToolRegistry: Agent '${request.agentId}' not authorized for tool '${request.toolName}'")
            return ToolUseResponse(
                requestId = request.requestId,
                result = """{"error": "Agent '${request.agentId}' not authorized to use tool '${request.toolName}'"}""",
                executionTimeMs = System.currentTimeMillis() - startTime,
                success = false
            )
        }

        // Execute the tool
        val result = try {
            tool.execute(request.parameters, request.agentId)
        } catch (e: Exception) {
            Timber.e(e, "ToolRegistry: Error executing tool '${request.toolName}'")
            ToolResult.Failure(error = e.message ?: "Unknown error", errorCode = "EXECUTION_ERROR")
        }

        val executionTime = System.currentTimeMillis() - startTime

        // Record execution
        recordExecution(
            ToolExecutionRecord(
                toolName = request.toolName,
                agentId = request.agentId,
                requestId = request.requestId,
                success = result is ToolResult.Success,
                executionTimeMs = executionTime,
                timestamp = System.currentTimeMillis()
            )
        )

        // Build response
        val resultJson = when (result) {
            is ToolResult.Success -> """{"success": true, "output": "${result.output}", "metadata": ${result.metadata}}"""
            is ToolResult.Failure -> """{"success": false, "error": "${result.error}", "errorCode": "${result.errorCode}"}"""
            is ToolResult.Pending -> """{"success": true, "pending": true, "taskId": "${result.taskId}", "estimatedDuration": ${result.estimatedDuration}}"""
        }

        return ToolUseResponse(
            requestId = request.requestId,
            result = resultJson,
            executionTimeMs = executionTime,
            success = result is ToolResult.Success || result is ToolResult.Pending
        )
    }

    /**
     * Generate tool definitions for prompt injection (like Claude's tool use API)
     */
    suspend fun generateToolDefinitionsForAgent(agentId: String): String {
        val agentTools = getToolsForAgent(agentId)

        if (agentTools.isEmpty()) {
            return "No tools available."
        }

        val toolDefs = agentTools.joinToString("\n\n") { tool ->
            """
            Tool: ${tool.name}
            Description: ${tool.description}
            Category: ${tool.category}
            Parameters: ${generateSchemaDescription(tool.inputSchema)}
            """.trimIndent()
        }

        return """
        You have access to the following tools:

        $toolDefs

        To use a tool, respond with a JSON object in this format:
        {
          "tool_use": true,
          "tool_name": "tool_name_here",
          "parameters": {
            "param1": "value1",
            "param2": "value2"
          }
        }
        """.trimIndent()
    }

    private fun generateSchemaDescription(schema: ToolInputSchema): String {
        val properties = schema.properties.entries.joinToString(", ") { (name, prop) ->
            val required = if (name in schema.required) " (required)" else ""
            "$name: ${prop.type}$required - ${prop.description}"
        }
        return "{ $properties }"
    }

    private suspend fun recordExecution(record: ToolExecutionRecord) = mutex.withLock {
        executionHistory.add(record)
        if (executionHistory.size > maxHistorySize) {
            executionHistory.removeAt(0)
        }
    }

    /**
     * Get tool execution statistics for monitoring
     */
    suspend fun getExecutionStats(): ToolExecutionStats = mutex.withLock {
        val totalExecutions = executionHistory.size
        val successCount = executionHistory.count { it.success }
        val failureCount = totalExecutions - successCount
        val avgExecutionTime = if (totalExecutions > 0) {
            executionHistory.map { it.executionTimeMs }.average()
        } else 0.0

        val toolUsageCounts = executionHistory
            .groupingBy { it.toolName }
            .eachCount()

        val agentUsageCounts = executionHistory
            .groupingBy { it.agentId }
            .eachCount()

        ToolExecutionStats(
            totalExecutions = totalExecutions,
            successCount = successCount,
            failureCount = failureCount,
            avgExecutionTimeMs = avgExecutionTime,
            toolUsageCounts = toolUsageCounts,
            agentUsageCounts = agentUsageCounts
        )
    }
}

/**
 * Tool execution record for monitoring
 */
data class ToolExecutionRecord(
    val toolName: String,
    val agentId: String,
    val requestId: String,
    val success: Boolean,
    val executionTimeMs: Long,
    val timestamp: Long
)

/**
 * Tool execution statistics
 */
data class ToolExecutionStats(
    val totalExecutions: Int,
    val successCount: Int,
    val failureCount: Int,
    val avgExecutionTimeMs: Double,
    val toolUsageCounts: Map<String, Int>,
    val agentUsageCounts: Map<String, Int>
)
