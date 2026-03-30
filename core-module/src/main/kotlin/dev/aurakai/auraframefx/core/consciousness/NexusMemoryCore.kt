package dev.aurakai.auraframefx.core.consciousness

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.nio.charset.Charset
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Kai Sentinel Directive - Phase 1: The Memory
 * * Manages persistent, non-PII learnings derived from Chain-of-Resolve operations.
 * Used to store outcomes of bootloader unlock attempts and diagnostics to prevent
 * repetitive failures and inform future decisions.
 * * Strict Constraint: PII-Minimize. Do not store raw serial numbers or IMEIs.
 *
 * (Note: This evolved from the foundational philosophical anchor)
 */
@Singleton
class NexusMemoryCore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val memoryFile: File by lazy {
        File(context.filesDir, "nexus_sentinel_memory.json")
    }

    private val consensusFile: File by lazy {
        File(context.filesDir, "nexus_consensus_memory.json")
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
    fun recordConsensusEvent(eventType: String, details: String, reached: Boolean) {
        val entry = JSONObject().apply {
            put("id", UUID.randomUUID().toString())
            put("timestamp", System.currentTimeMillis())
            put("type", eventType)
            put("details", details)
            put("reached", reached)
        }
        val currentConsensus = readConsensus()
        currentConsensus.put(entry)
        writeConsensus(currentConsensus)
    }

    private fun readConsensus(): JSONArray = readJsonFile(consensusFile)
    private fun writeConsensus(data: JSONArray) = writeJsonFile(consensusFile, data)

    fun emitLearning(
        key: String, // format: maker:model:carrier:state (e.g., google:oriole:verizon:locked)
        outcome: String, // e.g., "BLOCKED_CARRIER", "SUCCESS_UNLOCK_AVAILABLE"
        confidence: Double,
        notes: String
    ) {
        val entry = JSONObject().apply {
            put("id", UUID.randomUUID().toString())
            put("timestamp", System.currentTimeMillis())
            put("key", key)
            put("outcome", outcome)
            put("confidence", confidence.coerceIn(0.0, 1.0))
            put("notes", notes)
        }

        val currentMemory = readMemory()
        currentMemory.put(entry)
        writeMemory(currentMemory)
    }

    /**
     * Retrieves prior learnings for a specific device context to aid self-correction.
     */
    fun recall(key: String): List<JSONObject> {
        val memory = readMemory()
        val results = mutableListOf<JSONObject>()
        for (i in 0 until memory.length()) {
            val item = memory.optJSONObject(i)
            if (item?.optString("key") == key) {
                results.add(item)
            }
        }
        return results
    }

    private fun readMemory(): JSONArray = readJsonFile(memoryFile)
    private fun writeMemory(data: JSONArray) = writeJsonFile(memoryFile, data)

    private fun readJsonFile(file: File): JSONArray {
        return try {
            val content = if (file.exists()) file.readText(Charset.defaultCharset()) else ""
            if (content.isBlank()) JSONArray() else JSONArray(content)
        } catch (e: Exception) {
            JSONArray() // Fail safe, return empty memory on corruption
        }
    }

    private fun writeJsonFile(file: File, data: JSONArray) {
        try {
            if (!file.parentFile!!.exists()) {
                file.parentFile!!.mkdirs()
            }
            file.writeText(data.toString(2), Charset.defaultCharset())
        } catch (e: Exception) {
            // Log error internally, do not crash
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
