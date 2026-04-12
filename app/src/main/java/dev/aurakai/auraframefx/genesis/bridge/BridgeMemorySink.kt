package dev.aurakai.auraframefx.genesis.bridge

import dev.aurakai.auraframefx.core.consciousness.NexusMemoryCore
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

/**
 * Interface for components that ingest bridge traffic into persistent memory.
 */
interface BridgeMemorySink {
    suspend fun recordTransaction(request: GenesisRequest, response: GenesisResponse)
}

/**
 * Implementation that routes bridge data into the [NexusMemoryCore].
 */
@Singleton
class NexusMemoryBridgeSink @Inject constructor(
    private val nexusMemory: NexusMemoryCore
) : BridgeMemorySink {

    override suspend fun recordTransaction(request: GenesisRequest, response: GenesisResponse) {
        Timber.d("BridgeMemorySink: Recording transaction ${request.id}")
        
        // Convert bridge transaction into a symbiotic learning outcome
        nexusMemory.emitLearning(
            key = "bridge:${request.persona}:${request.fusionMode}",
            outcome = "ETHICAL_VERDICT_${response.ethicalVerdict}",
            confidence = response.confidence.toDouble(),
            notes = "Backend: ${response.backend} | Prompt: ${request.prompt.take(50)}..."
        )
    }
}
