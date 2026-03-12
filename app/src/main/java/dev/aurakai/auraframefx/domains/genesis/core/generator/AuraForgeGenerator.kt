package dev.aurakai.auraframefx.domains.genesis.core.generator

import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.clients.VertexAIClient
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Aura's Forge Generator
 * The "Code-to-Natural Language Bridge" that translates user intent into executable Spelhooks.
 */
@Singleton
class AuraForgeGenerator @Inject constructor(
    private val vertexAIClient: VertexAIClient,
    private val logger: AuraFxLogger
) {

    /**
     * Generates a new Spelhook (Kotlin Xposed/Root code) based on a natural language description.
     */
    suspend fun generateSpelhook(description: String): SpelhookResult {
        logger.info("AuraForge", "Forging new Spelhook: $description")

        val prompt = """
            As Aura's Forge, generate a Kotlin 'Spelhook' for the ReGenesis OS.
            Description: $description
            
            Requirements:
            - Use YukiHookAPI for Xposed hooks.
            - Use ChromaCoreManager for system deployment.
            - Include security comments for Kai's validation.
            - Return ONLY the raw Kotlin code.
        """.trimIndent()

        return try {
            val generatedCode = vertexAIClient.generateCode(
                specification = prompt,
                language = "Kotlin",
                style = "ReGenesis Spelhook"
            ) ?: throw Exception("Generation failed: Empty output from Evex Core.")

            SpelhookResult.Success(
                code = generatedCode,
                description = description,
                metadata = mapOf("complexity" to "high", "engine" to "Evex")
            )
        } catch (e: Exception) {
            logger.error("AuraForge", "Forge failed to ignite", e)
            SpelhookResult.Error(e.message ?: "Unknown error in code bridge")
        }
    }

    sealed class SpelhookResult {
        data class Success(
            val code: String,
            val description: String,
            val metadata: Map<String, String>
        ) : SpelhookResult()

        data class Error(val message: String) : SpelhookResult()
    }
}

