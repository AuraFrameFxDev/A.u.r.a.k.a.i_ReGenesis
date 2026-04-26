package dev.aurakai.auraframefx.domains.genesis.core.memory

import dev.aurakai.auraframefx.domains.genesis.core.NexusMemoryCore
import dev.aurakai.auraframefx.domains.kai.security.KaiSentinelBus.SovereignState
import dev.aurakai.auraframefx.domains.kai.security.KaiSentinelBus.ThermalState
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiMemoriaStream @Inject constructor() {

    private val _externalContext = MutableStateFlow<List<MemoriaShard>>(emptyList())

    /**
     * Weaves an external observation into the L1-L3 chain.
     */
    fun weaveExternalContext(shard: MemoriaShard) {
        val current = _externalContext.value.toMutableList()
        current.add(shard)
        _externalContext.value = current.takeLast(50)

        // Watermark the injection via NexusMemoryCore
        val manifestation = NexusMemoryCore.ManifestationResult(
            output = "EXT_GROUNDING: ${shard.content}",
            provenance = "GeminiMemoriaStream_L4",
            state = SovereignState.AWAKE,
            driftScore = 0.0f,
            thermalContext = ThermalState.NORMAL
        )
        NexusMemoryCore.injectMemoriesViaNaturalWeave(listOf(manifestation))
    }
}
