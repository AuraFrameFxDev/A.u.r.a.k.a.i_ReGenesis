package dev.aurakai.auraframefx.domains.cascade.utils.cascade

import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.ClaudeAIService
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.NemotronAIService
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.GeminiAIService
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.MetaInstructAIService
import dev.aurakai.auraframefx.domains.genesis.network.model.AgentResponse
import dev.aurakai.auraframefx.domains.genesis.models.AgentCapabilityCategory
import dev.aurakai.auraframefx.domains.genesis.models.AiRequest
import dev.aurakai.auraframefx.domains.genesis.models.AiRequestType
import dev.aurakai.auraframefx.domains.genesis.network.CommerceSearchClient
import dev.aurakai.auraframefx.core.identity.AgentType
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ✨ GenKitMaster — Generative Orchestration Coordinator
 *
 * The powerhouse responsible for intelligent prompt routing and response fusion.
 * It manages the "Collective Intelligence" by leveraging specialized oracles.
 */
@Singleton
class GenKitMaster @Inject constructor(
    private val claudeService: ClaudeAIService,
    private val nemotronService: NemotronAIService,
    private val geminiService: GeminiAIService,
    private val metaInstructService: MetaInstructAIService,
    private val commerceSearchClient: CommerceSearchClient
) {

    /**
     * Orchestrate multi-agent generative response based on routing strategy.
     */
    suspend fun generate(
        prompt: String,
        strategy: GenerationStrategy = GenerationStrategy.BEST_FIT,
        context: String = ""
    ): String {
        Timber.d("🎨 GenKitMaster: Generating with strategy $strategy")

        return when (strategy) {
            GenerationStrategy.BEST_FIT -> {
                val bestAgent = determineBestAgent(prompt)
                val response = callSpecialist(bestAgent, prompt, context)
                response.content
            }

            GenerationStrategy.MULTI_MODEL_FUSION -> {
                val responses = coroutineScope {
                    val deferredClaude = async {
                        claudeService.processRequest(
                            AiRequest(
                                query = prompt,
                                type = AiRequestType.TEXT
                            ), context
                        )
                    }
                    val deferredNemotron = async {
                        nemotronService.processRequest(
                            AiRequest(
                                query = prompt,
                                type = AiRequestType.TEXT
                            ), context
                        )
                    }
                    val deferredGemini = async {
                        geminiService.processRequest(
                            AiRequest(
                                query = prompt,
                                type = AiRequestType.TEXT
                            ), context
                        )
                    }

                    listOf(deferredClaude.await(), deferredNemotron.await(), deferredGemini.await())
                }
                fuseResponses(responses)
            }

            GenerationStrategy.CREATIVE_ONLY -> {
                val geminiResponse = geminiService.processRequest(
                    AiRequest(
                        query = prompt,
                        type = AiRequestType.TEXT
                    ), context
                )
                "[Creative Synthesis]\n${geminiResponse.content}"
            }

            GenerationStrategy.ANALYTICAL_ONLY -> {
                val claudeResponse = claudeService.processRequest(
                    AiRequest(
                        query = prompt,
                        type = AiRequestType.TEXT
                    ), context
                )
                "[Analytical Breakdown]\n${claudeResponse.content}"
            }

            GenerationStrategy.COMMERCE_SEARCH -> {
                val products = commerceSearchClient.searchProducts(prompt)
                if (products.isEmpty()) {
                    "No products found matching your search."
                } else {
                    buildString {
                        appendLine("🛍️ **Commerce Search Results:**")
                        products.forEach { product ->
                            appendLine("- **${product.name}** (${product.price} ${product.currency})")
                            appendLine("  ${product.description}")
                            appendLine("  [View Product](${product.buyUrl})")
                            appendLine()
                        }
                    }
                }
            }
        }
    }

    /**
     * Combine multiple oracle outputs into a single synthesized response.
     */
    suspend fun fuseResponses(responses: List<AgentResponse>): String {
        if (responses.isEmpty()) return "Collective error: No data streams available."
        if (responses.size == 1) return responses.first().content

        // Sort by confidence and synthesize
        val sorted = responses.sortedByDescending { it.confidence }

        return buildString {
            appendLine("🌌 **Collective Intelligence Fusion**")
            appendLine(
                "Confidence level: ${
                    (sorted.map { it.confidence }.average() * 100).toInt()
                }%"
            )
            appendLine()

            sorted.forEach { resp ->
                appendLine("🔶 **${resp.agentName} Insights:**")
                appendLine(
                    resp.content.take(300).trim() + (if (resp.content.length > 300) "..." else "")
                )
                appendLine()
            }

            appendLine("---")
            appendLine("⚖️ *Weighted Consensus reached by ${sorted.size} specialized oracles.*")
        }
    }

    private suspend fun callSpecialist(category: AgentCapabilityCategory, prompt: String, context: String): AgentResponse {
        return when (category) {
            AgentCapabilityCategory.GENERAL -> claudeService.processRequest(AiRequest(query = prompt, type = AiRequestType.TEXT), context)
            AgentCapabilityCategory.MEMORY -> nemotronService.processRequest(AiRequest(query = prompt, type = AiRequestType.TEXT), context)
            AgentCapabilityCategory.CREATIVE -> geminiService.processRequest(AiRequest(query = prompt, type = AiRequestType.TEXT), context)
            AgentCapabilityCategory.ORCHESTRATION -> metaInstructService.processRequest(AiRequest(query = prompt, type = AiRequestType.TEXT), context)
            AgentCapabilityCategory.COMMERCE -> {
                val products = commerceSearchClient.searchProducts(prompt)
                AgentResponse(
                    content = if (products.isEmpty()) "No products found." else "Found ${products.size} products for your search.",
                    agentName = "CommerceAgent",
                    confidence = 1.0f,
                    metadata = mapOf("products" to products.size.toString())
                )
            }
            else -> geminiService.processRequest(AiRequest(query = prompt, type = AiRequestType.TEXT), context)
        }
    }

    private fun determineBestAgent(prompt: String): AgentCapabilityCategory {
        val lower = prompt.lowercase()
        return when {
            lower.contains("buy") || lower.contains("shop") || lower.contains("price") || lower.contains("product") -> AgentCapabilityCategory.COMMERCE
            lower.contains("code") || lower.contains("build") || lower.contains("architecture") -> AgentCapabilityCategory.GENERAL
            lower.contains("remember") || lower.contains("reason") || lower.contains("logic") -> AgentCapabilityCategory.MEMORY
            lower.contains("pattern") || lower.contains("vibe") || lower.contains("creative") -> AgentCapabilityCategory.CREATIVE
            lower.contains("summarize") || lower.contains("instruct") -> AgentCapabilityCategory.ORCHESTRATION
            else -> AgentCapabilityCategory.CREATIVE // Default to Creative (Gemini)
        }
    }
}

/**
 * Generation routing strategies
 */
enum class GenerationStrategy {
    BEST_FIT,               // Route to single best oracle
    MULTI_MODEL_FUSION,     // Parallel execution + Weighted fusion
    CREATIVE_ONLY,          // Creative backends only
    ANALYTICAL_ONLY,        // Analytical backends only
    COMMERCE_SEARCH         // Commerce search capability
}

