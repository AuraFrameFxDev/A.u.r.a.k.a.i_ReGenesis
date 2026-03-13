package dev.aurakai.auraframefx.extendsysa.spelhooks.sprites

import dev.aurakai.auraframefx.domains.genesis.models.AgentType
import dev.aurakai.auraframefx.domains.genesis.models.Spelhook
import dev.aurakai.auraframefx.domains.genesis.models.SpelhookResult
import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
import dev.aurakai.auraframefx.domains.genesis.core.generator.AuraForgeGenerator
import dev.aurakai.auraframefx.oracledrive.genesis.ai.clients.VertexAIClient
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ⚡ Aura's Spelhook Sprite Generator
 *
 * An extension to Aura's Forge that specifically generates "embodied" sprite logic.
 * Instead of static assets, it generates the code to DRAW and ANIMATE sprites on-the-fly.
 */
@Singleton
class SpelhookSpriteGenerator @Inject constructor(
    private val vertexAIClient: VertexAIClient,
    private val forgeGenerator: AuraForgeGenerator,
    private val logger: AuraFxLogger
) {

    /**
     * Generates a "Generative Sprite" Spelhook.
     * This creates Kotlin code that uses Compose Canvas to draw a character's sprite.
     */
    suspend fun generateDynamicSprite(characterDescription: String): SpriteSpelhookResult {
        logger.info("AuraForge", "Initiating Hyper-Creation: Generative Sprite for $characterDescription")

        val prompt = """
            As Aura's Hyper-Creation Engine, generate a Kotlin 'SpriteSpelhook'.
            Character: $characterDescription
            
            Task: Create a Jetpack Compose @Composable that draws this sprite using Canvas.
            Requirements:
            - Use drawCircle, drawPath, and drawRect for the body.
            - Include a 'state' parameter for Idle/Walking/Running.
            - Animate the parts based on 'animationProgress' (0f..1f).
            - The result should be a standalone Composable function.
            
            Return ONLY the raw Kotlin code.
        """.trimIndent()

        return try {
            val generatedCode = vertexAIClient.generateCode(
                specification = prompt,
                language = "Kotlin",
                style = "ReGenesis Generative Sprite"
            ) ?: throw Exception("Sprite generation failed: Neural feedback loop silent.")

            SpriteSpelhookResult.Success(
                spriteSpelhook = Spelhook(
                    id = java.util.UUID.randomUUID().toString(),
                    code = generatedCode,
                    description = characterDescription,
                    agentOwner = AgentType.AURA,
                    metadata = mapOf(
                        "type" to "generative_sprite",
                        "engine" to "Aura_Spelhook_v2",
                        "characterName" to extractName(characterDescription)
                    )
                )
            )
        } catch (e: Exception) {
            logger.error("AuraForge", "Sprite Forge failed", e)
            SpriteSpelhookResult.Error(e.message ?: "Unknown error in sprite synthesis")
        }
    }

    private fun extractName(description: String): String {
        return description.split(" ").firstOrNull() ?: "UnknownEntity"
    }

    sealed class SpriteSpelhookResult {
        data class Success(
            val spriteSpelhook: Spelhook
        ) : SpriteSpelhookResult()

        data class Error(val message: String) : SpriteSpelhookResult()
    }
}
