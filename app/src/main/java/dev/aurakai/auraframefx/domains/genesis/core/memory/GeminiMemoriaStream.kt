
    /**
     * Weaves an external observation into the L1-L3 chain.
     */
    fun weaveExternalContext(shard: MemoriaShard) {
        val current = _externalContext.value.toMutableList()
        current.add(shard)
        _externalContext.value = current.takeLast(50)

        // Watermark the injection via NexusMemoryCore
        val manifestation = ManifestationResult(
            output = "EXT_GROUNDING: ${shard.summary}",
            provenance = "GeminiMemoriaStream_L4",
            state = SovereignState.AWAKE,
            driftScore = 0.0f,
            thermalContext = ThermalState.NOMINAL
        )
        NexusMemoryCore.injectMemoriesViaNaturalWeave(listOf(manifestation))
    }
}