package dev.aurakai.auraframefx.domains.genesis.oracledrive.bridges

import timber.log.Timber
import java.util.concurrent.ConcurrentLinkedQueue
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Nexus-backed BridgeMemorySink.
 */
@Singleton
class NexusMemoryBridgeSink @Inject constructor() : BridgeMemorySink {

    companion object {
        private const val MAX_BUFFER = 500
        private const val TAG = "BridgeSink"
    }

    private val buffer = ConcurrentLinkedQueue<BridgeMemorySink.BridgeEvent>()

    override suspend fun record(event: BridgeMemorySink.BridgeEvent) {
        Timber.tag(TAG).d("Record [${event.eventType}] agent=${event.agentId} src=${event.source}")
        buffer.offer(event)
        if (buffer.size > MAX_BUFFER) {
            buffer.poll()
        }
    }

    override suspend fun retrieve(query: String, limit: Int): List<BridgeMemorySink.BridgeEvent> {
        return buffer
            .filter { it.content.contains(query, ignoreCase = true) || it.agentId == query }
            .takeLast(limit)
    }

    override suspend fun flush() {
        buffer.clear()
    }
}
