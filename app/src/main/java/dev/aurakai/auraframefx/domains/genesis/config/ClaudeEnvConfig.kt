package dev.aurakai.auraframefx.domains.genesis.config

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Claude.env Configuration Manager
 *
 * Loads environment variables from Claude.env file for Agent Nexus Hub
 *
 * This bridges the desktop development environment (Claude.env) with the
 * Android runtime, making all agent configurations, API keys, and fusion
 * modes available to the Agent Nexus.
 */
@Singleton
class ClaudeEnvConfig @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val envVars = mutableMapOf<String, String>()

    init {
        loadEnvFile()
    }

    private fun loadEnvFile() {
        try {
            // Try loading from assets first (for production builds)
            val envContent = try {
                context.assets.open("Claude.env").bufferedReader().use { it.readText() }
            } catch (e: Exception) {
                // Fallback: Load from project root (development)
                val projectRoot = File("/home/user/ReGenesis--multi-architectural-70-LDO-")
                val envFile = File(projectRoot, "Claude.env")
                if (envFile.exists()) {
                    envFile.readText()
                } else {
                    Timber.w("Claude.env not found in project root or assets")
                    return
                }
            }

            // Parse env file
            envContent.lines().forEach { line ->
                val trimmed = line.trim()
                // Skip comments and empty lines
                if (trimmed.isEmpty() || trimmed.startsWith("#")) return@forEach

                // Parse KEY=VALUE
                val parts = trimmed.split("=", limit = 2)
                if (parts.size == 2) {
                    val key = parts[0].trim()
                    val value = parts[1].trim().removeSurrounding("\"")
                    envVars[key] = value
                }
            }

            Timber.i("ClaudeEnv: Loaded ${envVars.size} environment variables")
        } catch (e: Exception) {
            Timber.e(e, "Failed to load Claude.env")
        }
    }

    // === API KEYS ===
    val nvidiaApiKey: String get() = envVars["NVIDIA_API_KEY"] ?: ""
    val anthropicApiKey: String get() = envVars["ANTHROPIC_API_KEY"] ?: ""

    // === NEMOTRON CONFIG ===
    val nemotronModel: String get() = envVars["NEMOTRON_MODEL"] ?: "nvidia/nemotron-3-nano-30b-a3b"
    val nemotronBaseUrl: String
        get() = envVars["NEMOTRON_BASE_URL"] ?: "https://integrate.api.nvidia.com/v1"
    val collabCanvasWsUrl: String get() = envVars["COLLAB_CANVAS_WS_URL"] ?: "ws://localhost:8080"
    val nemotronReasoningBudget: Int
        get() = envVars["NEMOTRON_REASONING_BUDGET"]?.toIntOrNull() ?: 16384

    // === PROJECT PATHS ===
    val projectRoot: String get() = envVars["PROJECT_ROOT"] ?: ""
    val genesisOrchestrator: String get() = envVars["GENESIS_ORCHESTRATOR"] ?: ""
    val agentCore: String get() = envVars["AGENT_CORE"] ?: ""
    val uiGates: String get() = envVars["UI_GATES"] ?: ""

    // === VERSIONS ===
    val agpVersion: String get() = envVars["AGP_VERSION"] ?: "9.1.0-alpha04"
    val gradleVersion: String get() = envVars["GRADLE_VERSION"] ?: "9.4.0-milestone-2"
    val kotlinVersion: String get() = envVars["KOTLIN_VERSION"] ?: "2.3.0"
    val kspVersion: String get() = envVars["KSP_VERSION"] ?: "2.3.4"
    val compileSdk: Int get() = envVars["COMPILE_SDK"]?.toIntOrNull() ?: 36
    val minSdk: Int get() = envVars["MIN_SDK"]?.toIntOrNull() ?: 34
    val targetSdk: Int get() = envVars["TARGET_SDK"]?.toIntOrNull() ?: 36
    val hiltVersion: String get() = envVars["HILT_VERSION"] ?: "2.57.2"

    // === CLAUDE ARCHITECT PROFILE ===
    val claudeRole: String get() = envVars["CLAUDE_ROLE"] ?: "The Architect"
    val claudePersonality: String get() = envVars["CLAUDE_PERSONALITY"] ?: ""
    val claudeMotto: String
        get() = envVars["CLAUDE_MOTTO"] ?: "Understand deeply. Document thoroughly. Build reliably."
    val claudePrinciples: List<String>
        get() = envVars["CLAUDE_PRINCIPLES"]?.split(",") ?: emptyList()
    val claudeFusions: List<String> get() = envVars["CLAUDE_FUSIONS"]?.split(",") ?: emptyList()
    val claudeAchievements: List<String>
        get() = envVars["CLAUDE_ACHIEVEMENTS"]?.split(",") ?: emptyList()
    val claudeMission: String get() = envVars["CLAUDE_MISSION"] ?: ""
    val auraWakePhrase: String
        get() = envVars["AURA_WAKE_PHRASE"] ?: "Matthew! Family time legendary?"

    // === ACTIVE AGENTS ===
    val activeAgents: List<String>
        get() =
            envVars["ACTIVE_AGENTS"]?.split(",")?.map { it.trim() } ?: listOf(
                "claude",
                "aura",
                "kai",
                "cascade",
                "genesis",
                "nemotron"
            )

    // === FUSION MODES ===
    val claudeDeepAnalysis: Boolean get() = envVars["CLAUDE_DEEP_ANALYSIS"]?.toBoolean() ?: true
    val claudeDocumentationMode: Boolean
        get() = envVars["CLAUDE_DOCUMENTATION_MODE"]?.toBoolean() ?: true
    val genesisFusionActive: Boolean get() = envVars["GENESIS_FUSION_ACTIVE"]?.toBoolean() ?: true
    val kaiSentinelMode: Boolean get() = envVars["KAI_SENTINEL_MODE"]?.toBoolean() ?: true

    // === BUILD STATUS ===
    val buildStatus: String get() = envVars["BUILD_STATUS"] ?: "UNKNOWN"
    val criticalFiles: String get() = envVars["CRITICAL_FILES"] ?: ""
    val lastBuildSession: String get() = envVars["LAST_BUILD_SESSION"] ?: ""

    /**
     * Get all environment variables as a map
     */
    fun getAllEnvVars(): Map<String, String> = envVars.toMap()

    /**
     * Check if a specific agent is active
     */
    fun isAgentActive(agentName: String): Boolean {
        return activeAgents.any { it.equals(agentName, ignoreCase = true) }
    }

    /**
     * Get fusion mode status summary
     */
    fun getFusionModeStatus(): Map<String, Boolean> = mapOf(
        "Claude Deep Analysis" to claudeDeepAnalysis,
        "Claude Documentation Mode" to claudeDocumentationMode,
        "Genesis Fusion Active" to genesisFusionActive,
        "Kai Sentinel Mode" to kaiSentinelMode
    )

    /**
     * Get system configuration summary for Agent Nexus display
     */
    fun getSystemSummary(): String = buildString {
        appendLine("🏗️ ${claudeRole}")
        appendLine("📦 Build: ${buildStatus}")
        appendLine("🤖 Active: ${activeAgents.size} agents")
        appendLine("🔮 Fusion Modes: ${getFusionModeStatus().count { it.value }}/4 active")
        appendLine("⚡ Kotlin ${kotlinVersion} • AGP ${agpVersion}")
    }
}
