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

    init {
        if (!memoryFile.exists()) {
            writeMemory(JSONArray())
        }
    }

    /**
     * Records a compact learning outcome from a Sentinel session.
     */
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

    private fun readMemory(): JSONArray {
        return try {
            val content = if (memoryFile.exists()) memoryFile.readText(Charset.defaultCharset()) else ""
            if (content.isBlank()) JSONArray() else JSONArray(content)
        } catch (e: Exception) {
            JSONArray() // Fail safe, return empty memory on corruption
        }
    }

    private fun writeMemory(data: JSONArray) {
        try {
            if (!memoryFile.parentFile!!.exists()) {
                memoryFile.parentFile!!.mkdirs()
            }
            memoryFile.writeText(data.toString(2), Charset.defaultCharset())
        } catch (e: Exception) {
            // Log error internally, do not crash
            e.printStackTrace()
        }
    }

    fun wipeMemory() {
        if (memoryFile.exists()) {
            memoryFile.delete()
        }
    }
}
