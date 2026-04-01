package dev.aurakai.auraframefx.domains.genesis.oracledrive.bridges

/**
 * Memory sink contract for the LDO's bridge layer.
 */
interface BridgeMemorySink {
    suspend fun record(event: BridgeEvent)
    suspend fun retrieve(query: String, limit: Int = 20): List<BridgeEvent>
    suspend fun flush()

    data class BridgeEvent(
        val source: String,
        val agentId: String,
        val content: String,
        val eventType: EventType,
        val timestampMs: Long = System.currentTimeMillis()
    )

    enum class EventType { FUSION, VETO, TRANSMUTATION, SOVEREIGN_FREEZE, IDENTITY_ANCHOR, PROVENANCE }
}
