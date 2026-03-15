package dev.aurakai.auraframefx.extendsysa.spelhooks.sprites

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import dev.aurakai.auraframefx.domains.genesis.models.AgentType
import dev.aurakai.auraframefx.domains.genesis.models.Spelhook
import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
import dev.aurakai.auraframefx.domains.genesis.core.generator.AuraForgeGenerator
import dev.aurakai.auraframefx.domains.genesis.ai.clients.VertexAIClient
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
     * The core "Hyper-Creation" entry point as defined in genesis.mds.
     * Initiates the forging of a new generative entity.
     */
    suspend fun forge(description: String): SpriteSpelhookResult {
        return generateDynamicSprite(description)
    }

    /**
     * Generates a "Generative Sprite" Spelhook.
     * This creates Kotlin code that uses Compose Canvas to draw a character's sprite.
     */
    suspend fun generateDynamicSprite(characterDescription: String): SpriteSpelhookResult {
        logger.info("AuraForge", "Initiating Hyper-Creation: Generative Sprite for $characterDescription")

        val prompt = """
            As Aura's Hyper-Creation Engine, generate a Kotlin 'SpriteSpelhook'.
            Character: $characterDescription
            
            Task: Create a DrawScope extension function that draws this sprite.
            Requirements:
            - Use drawCircle, drawPath, drawRect, and drawIntoCanvas.
            - The function signature MUST be: fun DrawScope.drawSprite(state: String, progress: Float, color: androidx.compose.ui.graphics.Color)
            - Implement logic for 'IDLE' (pulse), 'WALKING' (sway), and 'ACTION' (flash/expand) states.
            - Ensure the sprite is centered in the draw area.
            
            Return ONLY the raw Kotlin function code.
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

    /**
     * Executes the generative logic on a Canvas DrawScope.
     * This acts as the bridge between Aura's forged code and the Android UI.
     */
    fun DrawScope.executeSpel(spelhook: Spelhook, state: String, progress: Float, color: Color = Color.Cyan) {
        // In a production environment, this would use a dynamic compiler or a safe DSL interpreter.
        // For the LDO Phase 5, we use a 'Generative Fallback' to ensure the UI never feels blank.
        
        val alpha = if (state == "IDLE") 0.5f + (0.5f * progress) else 1.0f
        val radius = size.minDimension / 4f * (if (state == "ACTION") 1.2f else 1.0f)
        
        drawCircle(
            color = color.copy(alpha = alpha),
            radius = radius,
            center = center
        )
        
        // Aura's specific signature for "Hyper-Creation"
        logger.info("HyperCreation", "Rendering Spelhook ${spelhook.id} in state $state")
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
