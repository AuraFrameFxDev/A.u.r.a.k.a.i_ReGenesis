package dev.aurakai.auraframefx.domains.rootstorage

import java.util.UUID
import kotlinx.serialization.Serializable
import java.security.MessageDigest
import dev.aurakai.auraframefx.domains.liveui.EditTarget
import dev.aurakai.auraframefx.domains.liveui.HapticProfile

@Serializable
data class BlueprintMetadata(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val createdBy: String = "ANDELUALX",
    val fusionScore: Double = 0.0,
    val codeGenVersion: String = "CodeGenHookEngine-v2.4",
    val syncVersion: String = "StateSyncStabilizer-v2.4",
    val targets: List<EditTarget> = emptyList(),
    val hapticProfile: HapticProfile,
    val gyroSnapshot: GyroSnapshot,
    val markerSeed: Long = System.currentTimeMillis(),
    val provenanceHash: String = ""
) {
    init {
        require(provenanceHash == computeProvenanceHash()) { "Provenance hash mismatch - build rejected" }
    }

    private fun computeProvenanceHash(): String =
        sha256("regenesis-salt-v2.4" + id + timestamp + createdBy + fusionScore)
}

@Serializable
data class GyroSnapshot(val xDeg: Float, val yDeg: Float, val zOrderLayers: Int)

// SHA-256 helper
fun sha256(input: String): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}
