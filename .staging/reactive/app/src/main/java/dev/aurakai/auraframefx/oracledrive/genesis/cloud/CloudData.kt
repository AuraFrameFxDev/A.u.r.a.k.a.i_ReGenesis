package dev.aurakai.auraframefx.oracledrive.genesis.cloud

data class FileMetadata(
    val name: String,
    val mimeType: String,
    val size: Long
)

data class StorageOptimization(
    val bytesFreed: Long,
    val filesOptimized: Int,
    val compressionRatio: Float,
    val success: Boolean,
    val message: String
)

data class SyncConfiguration(
    val syncType: String,
    val conflictResolution: String
)
