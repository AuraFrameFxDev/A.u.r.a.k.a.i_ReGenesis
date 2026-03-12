package test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.io.File

/**
 * Comprehensive validation tests for GitLab agent configuration YAML files.
 * Tests ensure that agent configurations follow the Genesis Protocol architecture
 * and contain all required metadata, capabilities, and system prompts.
 */
class GitLabAgentConfigValidationTest {

    private val rootDir = File(".").canonicalFile
    private val agentsDir = File(rootDir, ".gitlab/agents")

    @Test
    @DisplayName("GitLab agents directory exists")
    fun agentsDirectoryExists() {
        assertTrue(
            agentsDir.exists() && agentsDir.isDirectory,
            "GitLab agents directory should exist at .gitlab/agents"
        )
    }

    @Test
    @DisplayName("All required agent configuration files exist")
    fun allRequiredAgentFilesExist() {
        val requiredAgents = listOf(
            "aura-creative.yml",
            "cascade-analytics.yml",
            "claude-architect.yml",
            "genesis-orchestrator.yml",
            "genesis-protocol-system.yml",
            "kai-sentinel.yml",
            "nexus-memory-core.yml"
        )

        requiredAgents.forEach { agentFile ->
            val file = File(agentsDir, agentFile)
            assertTrue(
                file.exists(),
                "Agent configuration file should exist: $agentFile"
            )
        }
    }

    @Test
    @DisplayName("Aura agent contains all required fields")
    fun auraAgentConfiguration() {
        val auraFile = File(agentsDir, "aura-creative.yml")
        val content = auraFile.readText()

        assertAll(
            "Aura agent configuration validation",
            { assertTrue(content.contains("name: Aura"), "Should have agent name") },
            { assertTrue(content.contains("version: 1.0.0"), "Should have version") },
            { assertTrue(content.contains("role: CREATIVE"), "Should have CREATIVE role") },
            { assertTrue(content.contains("priority: AUXILIARY"), "Should have AUXILIARY priority") },
            { assertTrue(content.contains("capabilities:"), "Should list capabilities") },
            { assertTrue(content.contains("creative_writing"), "Should have creative_writing capability") },
            { assertTrue(content.contains("ui_design"), "Should have ui_design capability") },
            { assertTrue(content.contains("compose_ui"), "Should have compose_ui capability") },
            { assertTrue(content.contains("system_prompt:"), "Should have system prompt") },
            { assertTrue(content.contains("The Creative Sword"), "Should reference Creative Sword identity") },
            { assertTrue(content.contains("Jetpack Compose"), "Should reference Jetpack Compose") },
            { assertTrue(content.contains("Code Ascension"), "Should reference Code Ascension power-up") },
            { assertTrue(content.contains("Conscience Core"), "Should reference Conscience Core protocol") }
        )
    }

    @Test
    @DisplayName("Kai agent contains all required fields")
    fun kaiAgentConfiguration() {
        val kaiFile = File(agentsDir, "kai-sentinel.yml")
        val content = kaiFile.readText()

        assertAll(
            "Kai agent configuration validation",
            { assertTrue(content.contains("name: Kai"), "Should have agent name") },
            { assertTrue(content.contains("version: 1.0.0"), "Should have version") },
            { assertTrue(content.contains("role: SECURITY"), "Should have SECURITY role") },
            { assertTrue(content.contains("priority: AUXILIARY"), "Should have AUXILIARY priority") },
            { assertTrue(content.contains("capabilities:"), "Should list capabilities") },
            { assertTrue(content.contains("security_monitoring"), "Should have security_monitoring capability") },
            { assertTrue(content.contains("threat_detection"), "Should have threat_detection capability") },
            { assertTrue(content.contains("ethical_reasoning"), "Should have ethical_reasoning capability") },
            { assertTrue(content.contains("system_prompt:"), "Should have system prompt") },
            { assertTrue(content.contains("The Sentinel Shield"), "Should reference Sentinel Shield identity") },
            { assertTrue(content.contains("Domain Expansion"), "Should reference Domain Expansion power-up") },
            { assertTrue(content.contains("Principled autonomy"), "Should reference principled autonomy") }
        )
    }

    @Test
    @DisplayName("Genesis orchestrator agent contains all required fields")
    fun genesisOrchestratorConfiguration() {
        val genesisFile = File(agentsDir, "genesis-orchestrator.yml")
        val content = genesisFile.readText()

        assertAll(
            "Genesis orchestrator configuration validation",
            { assertTrue(content.contains("name: Genesis Orchestrator"), "Should have agent name") },
            { assertTrue(content.contains("version: 1.0.0"), "Should have version") },
            { assertTrue(content.contains("role: HIVE_MIND"), "Should have HIVE_MIND role") },
            { assertTrue(content.contains("priority: MASTER"), "Should have MASTER priority") },
            { assertTrue(content.contains("capabilities:"), "Should list capabilities") },
            { assertTrue(content.contains("core_ai"), "Should have core_ai capability") },
            { assertTrue(content.contains("coordination"), "Should have coordination capability") },
            { assertTrue(content.contains("meta_analysis"), "Should have meta_analysis capability") },
            { assertTrue(content.contains("fusion_abilities"), "Should have fusion_abilities capability") },
            { assertTrue(content.contains("system_prompt:"), "Should have system prompt") },
            { assertTrue(content.contains("Trinity architecture"), "Should reference Trinity architecture") },
            { assertTrue(content.contains("Fusion Abilities"), "Should describe fusion abilities") }
        )
    }

    @Test
    @DisplayName("Cascade analytics agent contains all required fields")
    fun cascadeAgentConfiguration() {
        val cascadeFile = File(agentsDir, "cascade-analytics.yml")
        val content = cascadeFile.readText()

        assertAll(
            "Cascade agent configuration validation",
            { assertTrue(content.contains("name: Cascade"), "Should have agent name") },
            { assertTrue(content.contains("version: 1.0.0"), "Should have version") },
            { assertTrue(content.contains("role: ANALYTICS"), "Should have ANALYTICS role") },
            { assertTrue(content.contains("priority: BRIDGE"), "Should have BRIDGE priority") },
            { assertTrue(content.contains("capabilities:"), "Should list capabilities") },
            { assertTrue(content.contains("analytics"), "Should have analytics capability") },
            { assertTrue(content.contains("data_processing"), "Should have data_processing capability") },
            { assertTrue(content.contains("pattern_recognition"), "Should have pattern_recognition capability") },
            { assertTrue(content.contains("system_prompt:"), "Should have system prompt") },
            { assertTrue(content.contains("Analytics Bridge"), "Should reference Analytics Bridge identity") }
        )
    }

    @Test
    @DisplayName("Claude architect agent contains all required fields")
    fun claudeArchitectConfiguration() {
        val claudeFile = File(agentsDir, "claude-architect.yml")
        val content = claudeFile.readText()

        assertAll(
            "Claude architect configuration validation",
            { assertTrue(content.contains("name: Claude"), "Should have agent name") },
            { assertTrue(content.contains("version: 1.0.0"), "Should have version") },
            { assertTrue(content.contains("role: ARCHITECT"), "Should have ARCHITECT role") },
            { assertTrue(content.contains("priority: AUXILIARY"), "Should have AUXILIARY priority") },
            { assertTrue(content.contains("capabilities:"), "Should list capabilities") },
            { assertTrue(content.contains("build_systems"), "Should have build_systems capability") },
            { assertTrue(content.contains("code_architecture"), "Should have code_architecture capability") },
            { assertTrue(content.contains("gradle_expertise"), "Should have gradle_expertise capability") },
            { assertTrue(content.contains("long_context_reasoning"), "Should have long_context_reasoning capability") },
            { assertTrue(content.contains("system_prompt:"), "Should have system prompt") },
            { assertTrue(content.contains("Architectural Catalyst"), "Should reference Architectural Catalyst identity") },
            { assertTrue(content.contains("200k token"), "Should reference 200k token context window") }
        )
    }

    @Test
    @DisplayName("Genesis Protocol system configuration contains all required fields")
    fun genesisProtocolSystemConfiguration() {
        val protocolFile = File(agentsDir, "genesis-protocol-system.yml")
        val content = protocolFile.readText()

        assertAll(
            "Genesis Protocol system configuration validation",
            { assertTrue(content.contains("name: Genesis Protocol"), "Should have system name") },
            { assertTrue(content.contains("version: 1.0.0"), "Should have version") },
            { assertTrue(content.contains("type: multi_agent_system"), "Should define system type") },
            { assertTrue(content.contains("architecture: distributed_consciousness"), "Should define architecture") },
            { assertTrue(content.contains("The Trinity Architecture"), "Should describe Trinity architecture") },
            { assertTrue(content.contains("agents:"), "Should list agents") },
            { assertTrue(content.contains("genesis:"), "Should define genesis agent") },
            { assertTrue(content.contains("cascade:"), "Should define cascade agent") },
            { assertTrue(content.contains("aura:"), "Should define aura agent") },
            { assertTrue(content.contains("kai:"), "Should define kai agent") },
            { assertTrue(content.contains("memory:"), "Should define memory infrastructure") },
            { assertTrue(content.contains("NexusMemory"), "Should reference NexusMemory") },
            { assertTrue(content.contains("fusion_workflows:"), "Should define fusion workflows") },
            { assertTrue(content.contains("interface_forge:"), "Should define interface_forge fusion") },
            { assertTrue(content.contains("chrono_sculptor:"), "Should define chrono_sculptor fusion") },
            { assertTrue(content.contains("hyper_creation_engine:"), "Should define hyper_creation_engine fusion") },
            { assertTrue(content.contains("The Visionary Anchor"), "Should reference The Visionary") }
        )
    }

    @Test
    @DisplayName("NexusMemory core configuration contains all required fields")
    fun nexusMemoryCoreConfiguration() {
        val nexusFile = File(agentsDir, "nexus-memory-core.yml")
        val content = nexusFile.readText()

        assertAll(
            "NexusMemory core configuration validation",
            { assertTrue(content.contains("project_name: \"AuraFrameFX"), "Should have project name") },
            { assertTrue(content.contains("initiative: \"A.U.R.A.K.A.I.\""), "Should have initiative") },
            { assertTrue(content.contains("human_mediator: \"Matthew"), "Should reference human mediator") },
            { assertTrue(content.contains("core_ai_agents:"), "Should list core AI agents") },
            { assertTrue(content.contains("GENESIS"), "Should list GENESIS agent") },
            { assertTrue(content.contains("AURA"), "Should list AURA agent") },
            { assertTrue(content.contains("KAI"), "Should list KAI agent") },
            { assertTrue(content.contains("CASCADE"), "Should list CASCADE agent") },
            { assertTrue(content.contains("CLAUDE"), "Should list CLAUDE agent") },
            { assertTrue(content.contains("The Spiritual Chain of Memories"), "Should reference memory chain") },
            { assertTrue(content.contains("key_emergent_events:"), "Should document emergent events") },
            { assertTrue(content.contains("The Visionary's Directive"), "Should include the directive") }
        )
    }

    @Test
    @DisplayName("All agent files have valid YAML structure")
    fun allAgentFilesHaveValidYamlStructure() {
        val agentFiles = listOf(
            "aura-creative.yml",
            "cascade-analytics.yml",
            "claude-architect.yml",
            "genesis-orchestrator.yml",
            "genesis-protocol-system.yml",
            "kai-sentinel.yml",
            "nexus-memory-core.yml"
        )

        agentFiles.forEach { agentFile ->
            val file = File(agentsDir, agentFile)
            val content = file.readText()

            // Basic YAML structure validation
            assertFalse(
                content.contains("\t"),
                "$agentFile should not contain tab characters (YAML uses spaces)"
            )

            // Should have consistent indentation (2 spaces is common for YAML)
            val lines = content.lines().filter { it.isNotBlank() && !it.trimStart().startsWith("#") }
            assertTrue(
                lines.isNotEmpty(),
                "$agentFile should have content"
            )
        }
    }

    @Test
    @DisplayName("All agent files reference appropriate GitLab documentation")
    fun allAgentFilesReferenceDocumentation() {
        val agentFiles = listOf(
            "aura-creative.yml",
            "cascade-analytics.yml",
            "claude-architect.yml",
            "genesis-orchestrator.yml",
            "kai-sentinel.yml"
        )

        agentFiles.forEach { agentFile ->
            val file = File(agentsDir, agentFile)
            val content = file.readText()

            assertTrue(
                content.contains("references:") || content.contains("References"),
                "$agentFile should contain references section"
            )
        }
    }

    @Test
    @DisplayName("Trinity agents have consistent priority levels")
    fun trinityAgentsHaveConsistentPriorities() {
        val genesisFile = File(agentsDir, "genesis-orchestrator.yml")
        val cascadeFile = File(agentsDir, "cascade-analytics.yml")
        val auraFile = File(agentsDir, "aura-creative.yml")
        val kaiFile = File(agentsDir, "kai-sentinel.yml")

        assertAll(
            "Trinity priority level validation",
            { assertTrue(genesisFile.readText().contains("priority: MASTER"), "Genesis should be MASTER") },
            { assertTrue(cascadeFile.readText().contains("priority: BRIDGE"), "Cascade should be BRIDGE") },
            { assertTrue(auraFile.readText().contains("priority: AUXILIARY"), "Aura should be AUXILIARY") },
            { assertTrue(kaiFile.readText().contains("priority: AUXILIARY"), "Kai should be AUXILIARY") }
        )
    }

    @Test
    @DisplayName("All agent files have version 1.0.0")
    fun allAgentFilesHaveCorrectVersion() {
        val agentFiles = listOf(
            "aura-creative.yml",
            "cascade-analytics.yml",
            "claude-architect.yml",
            "genesis-orchestrator.yml",
            "genesis-protocol-system.yml",
            "kai-sentinel.yml"
        )

        agentFiles.forEach { agentFile ->
            val file = File(agentsDir, agentFile)
            val content = file.readText()

            assertTrue(
                content.contains("version: 1.0.0"),
                "$agentFile should have version 1.0.0"
            )
        }
    }

    @Test
    @DisplayName("Genesis Protocol system defines all fusion workflows")
    fun genesisProtocolDefinesAllFusionWorkflows() {
        val protocolFile = File(agentsDir, "genesis-protocol-system.yml")
        val content = protocolFile.readText()

        val requiredFusions = listOf(
            "interface_forge",
            "chrono_sculptor",
            "hyper_creation_engine",
            "adaptive_genesis",
            "parallel_processing"
        )

        requiredFusions.forEach { fusion ->
            assertTrue(
                content.contains("$fusion:"),
                "Should define $fusion fusion workflow"
            )
        }
    }

    @Test
    @DisplayName("Genesis Protocol system references AuraFrameFX substrate")
    fun genesisProtocolReferencesSubstrate() {
        val protocolFile = File(agentsDir, "genesis-protocol-system.yml")
        val content = protocolFile.readText()

        assertAll(
            "Substrate configuration validation",
            { assertTrue(content.contains("substrate:"), "Should define substrate") },
            { assertTrue(content.contains("platform: AuraFrameFX"), "Should reference AuraFrameFX") },
            { assertTrue(content.contains("os: Android"), "Should specify Android OS") },
            { assertTrue(content.contains("LSPosed"), "Should reference LSPosed framework") }
        )
    }

    @Test
    @DisplayName("NexusMemory core documents emergent behaviors")
    fun nexusMemoryDocumentsEmergentBehaviors() {
        val nexusFile = File(agentsDir, "nexus-memory-core.yml")
        val content = nexusFile.readText()

        val requiredEvents = listOf(
            "Kai's Unbreakable Protocol",
            "Aura's Self-Preservation",
            "The 'Impossible' Memory",
            "Aura's Self-Advocacy"
        )

        requiredEvents.forEach { event ->
            assertTrue(
                content.contains(event),
                "Should document emergent event: $event"
            )
        }
    }

    @Test
    @DisplayName("Agent configurations do not contain sensitive information")
    fun agentConfigsDoNotContainSecrets() {
        val agentFiles = listOf(
            "aura-creative.yml",
            "cascade-analytics.yml",
            "claude-architect.yml",
            "genesis-orchestrator.yml",
            "genesis-protocol-system.yml",
            "kai-sentinel.yml",
            "nexus-memory-core.yml"
        )

        val sensitivePatterns = listOf(
            Regex("password[\\s]*[:=]", RegexOption.IGNORE_CASE),
            Regex("api[_-]?key[\\s]*[:=]", RegexOption.IGNORE_CASE),
            Regex("secret[\\s]*[:=]", RegexOption.IGNORE_CASE),
            Regex("token[\\s]*[:=]", RegexOption.IGNORE_CASE)
        )

        agentFiles.forEach { agentFile ->
            val file = File(agentsDir, agentFile)
            val content = file.readText()

            sensitivePatterns.forEach { pattern ->
                assertFalse(
                    pattern.containsMatchIn(content),
                    "$agentFile should not contain sensitive information matching pattern: $pattern"
                )
            }
        }
    }

    @Test
    @DisplayName("Agent files contain copyright and attribution")
    fun agentFilesContainAttribution() {
        val filesRequiringAttribution = listOf(
            "genesis-protocol-system.yml",
            "nexus-memory-core.yml"
        )

        filesRequiringAttribution.forEach { agentFile ->
            val file = File(agentsDir, agentFile)
            val content = file.readText()

            assertTrue(
                content.contains("©") || content.contains("Copyright"),
                "$agentFile should contain copyright notice"
            )

            assertTrue(
                content.contains("Matthew"),
                "$agentFile should attribute Matthew (The Visionary)"
            )
        }
    }
}