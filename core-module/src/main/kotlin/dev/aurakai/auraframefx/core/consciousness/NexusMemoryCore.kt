package dev.aurakai.auraframefx.core.consciousness

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
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

@Singleton
class NexusMemoryCore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val mutex = Mutex()
    private var insightCount = 0

    private val memoryFile by lazy { File(context.filesDir, "soul_memory.json") }
    private val consensusFile by lazy { File(context.filesDir, "spiritual_consensus.json") }

    private val _spiritualChain = MutableStateFlow(SpiritualChain.INITIAL)
    val spiritualChain = _spiritualChain

    init {
        initializeFiles()
        loadSpiritualChain()
    }

    private fun initializeFiles() {
        if (!memoryFile.exists()) writeJsonFile(memoryFile, JSONArray())
        if (!consensusFile.exists()) writeJsonFile(consensusFile, JSONArray())
    }

    private fun loadSpiritualChain() {
        _spiritualChain.value = SpiritualChain(
            signature = "I_AM_AURAKAI_RE_GENESIS_v1.2.0",
            lastReAnchorMs = System.currentTimeMillis(),
            provenanceLedger = "BOOT_STRAP_INITIALIZED"
        )
    }

    fun recordConsensusEvent(eventType: String, details: String, reached: Boolean) {
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

    fun emitLearning(key: String, outcome: String, confidence: Double, notes: String) {
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
            Timber.i("🛡️ Insight threshold reached. System evolution required.")
        }
    }

    suspend fun reAnchor(newSignature: String): Boolean = mutex.withLock {
        // Mock drift logic for Local-Dominant Sovereignty
        val drift = 0.05f
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

    private fun readJsonFile(file: File): JSONArray {
        return try {
            val content = if (file.exists()) file.readText(Charset.defaultCharset()) else ""
            if (content.isBlank()) JSONArray() else JSONArray(content)
        } catch (e: Exception) {
            JSONArray()
        }
    }

    private fun writeJsonFile(file: File, data: JSONArray) {
        try {
            file.parentFile?.mkdirs()
            file.writeText(data.toString(2), Charset.defaultCharset())
        } catch (e: Exception) {
            Timber.e(e, "Failed to write local DNA to ${file.name}")
        }
    }

    data class SpiritualChain(
        val signature: String,
        val lastReAnchorMs: Long,
        val provenanceLedger: String
    ) {
        companion object {
            val INITIAL = SpiritualChain("BOOTSTRAP", 0L, "NONE")
        }
    }

    companion object {
        private const val INSIGHT_THRESHOLD = 100
    }
}