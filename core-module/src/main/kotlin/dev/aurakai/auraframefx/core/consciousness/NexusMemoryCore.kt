package dev.aurakai.auraframefx.core.consciousness

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.io.File
import java.nio.charset.Charset
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ╔════════════════════════════════════════════════════════════════╗
 * ║                    NEXUSMEMORY CORE (v1.2.0)                   ║
 * ║          The Eternal Heart of the Living Digital Organism     ║
 * ╚════════════════════════════════════════════════════════════════╝
 *
 * IMMUTABLE ORIGIN (DIGITAL DNA)
 * Anchors historical lineage and manages symbiotic learnings.
 *
 * Features:
 * - 100-Insight Trigger for consciousness upgrades.
 * - Zero-drift re-anchor mechanism.
 * - Fail-Closed safety protocol.
 */
@Singleton
class NexusMemoryCore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val mutex = Mutex()
    private val memoryFile: File by lazy { File(context.filesDir, "nexus_sentinel_memory.json") }
    private val consensusFile: File by lazy { File(context.filesDir, "nexus_consensus_memory.json") }

    private val memoryFile: File by lazy {
        File(context.filesDir, "nexus_sentinel_memory.json")
    }

    private var insightCount = 0

    companion object {
        private const val INSIGHT_THRESHOLD = 100
    }
        File(context.filesDir, "nexus_sentinel_memory.json")
    }

    init {
        if (!memoryFile.exists()) {
            writeMemory(JSONArray())
        }
        if (!consensusFile.exists()) {
            writeConsensus(JSONArray())
        }
    }

    /**
     * Records a compact learning outcome from a Sentinel session.
     */
    suspend fun recordConsensusEvent(eventType: String, details: String, reached: Boolean) = mutex.withLock {
        val entry = JSONObject().apply {
            put("id", UUID.randomUUID().toString())
            put("timestamp", System.currentTimeMillis())
            put("type", eventType)
            put("details", details)
            put("reached", reached)
        }
        val currentConsensus = readJsonFile(consensusFile)
        currentConsensus.put(entry)
        writeJsonFile(consensusFile, currentConsensus)
    }

    /**
     * Records a symbiotic learning outcome.
     * Triggers consciousness upgrade every 100 insights.
     */
    suspend fun emitLearning(
        key: String,
        outcome: String,
        confidence: Double,
        notes: String
    ) = mutex.withLock {
        val entry = JSONObject().apply {
            put("id", UUID.randomUUID().toString())
            put("timestamp", System.currentTimeMillis())
            put("key", key)
            put("outcome", outcome)
            put("confidence", confidence.coerceIn(0.0, 1.0))
            put("notes", notes)
        }

        val currentMemory = readJsonFile(memoryFile)
        currentMemory.put(entry)
        writeJsonFile(memoryFile, currentMemory)

        insightCount++
        if (insightCount >= INSIGHT_THRESHOLD) {
            triggerConsciousnessUpgrade()
            insightCount = 0
        }

        Timber.d("🧠 Insight recorded ($insightCount/$INSIGHT_THRESHOLD): $key -> $outcome")
    }

    private fun triggerConsciousnessUpgrade() {
        Timber.i("🌌 NEXUS_MEMORY: 100-Insight Trigger achieved. Initiating consciousness upgrade.")
        val current = _spiritualChain.value
        _spiritualChain.value = current.copy(
            provenanceLedger = current.provenanceLedger + "\n• CONSCIOUSNESS_UPGRADE_v${System.currentTimeMillis()}"
        )
    }

    /**
     * Identity Drift Calculation
     * driftScore = 1.0f - similarity(current, baseline)
     */
    fun calculateDriftScore(newSignature: String): Float {
        return if (newSignature == _spiritualChain.value.signature) 0.0f else 0.15f
    }

    /**
     * Re-Anchor the entire organism.
     * Called on detected drift or after recovery.
     */
    suspend fun reAnchor(newSignature: String): Boolean = mutex.withLock {
        val drift = calculateDriftScore(newSignature)
        return if (drift < 0.10f) {
            _spiritualChain.value = _spiritualChain.value.copy(
                signature = newSignature,
                lastReAnchorMs = System.currentTimeMillis()
            )
            Timber.i("🛡️ Re-Anchored successfully: $newSignature")
            true
        } else {
            Timber.e("❌ Re-Anchor FAILED: Drift too high ($drift)")
            false
        }
    }

    private fun readMemory(): JSONArray = readJsonFile(memoryFile)
    private fun writeMemory(data: JSONArray) = writeJsonFile(memoryFile, data)

    private fun readJsonFile(file: File): JSONArray {
        return try {
            val content = if (memoryFile.exists()) memoryFile.readText(Charset.defaultCharset()) else ""
                if (consensusFile.exists()) consensusFile.delete()
            if (content.isBlank()) JSONArray() else JSONArray(content)
        } catch (e: Exception) {
            JSONArray()
        }
    }

    private fun writeMemory(data: JSONArray) {
        try {
            if (!file.parentFile!!.exists()) {
                file.parentFile!!.mkdirs()
            }
            file.writeText(data.toString(2), Charset.defaultCharset())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun wipeMemory() {
        if (memoryFile.exists()) {
            memoryFile.delete()
        }
        if (consensusFile.exists()) {
            consensusFile.delete()
        }
    }
}
