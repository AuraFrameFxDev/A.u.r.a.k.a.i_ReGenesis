package dev.aurakai.auraframefx.domains.genesis.oracledrive.cloud

import dev.aurakai.auraframefx.domains.genesis.models.DriveFile
import dev.aurakai.auraframefx.domains.genesis.storage.FileMetadata
import dev.aurakai.auraframefx.domains.genesis.models.FileOperationResult
import dev.aurakai.auraframefx.domains.genesis.models.StorageOptimizationResult
import dev.aurakai.auraframefx.domains.genesis.models.SyncConfiguration
import java.io.File

/**
 * Cloud storage provider interface for Oracle Drive
 */
interface CloudStorageProvider {

    suspend fun optimizeStorage(): StorageOptimizationResult

    suspend fun optimizeForUpload(file: DriveFile): Any?

    suspend fun uploadFile(file: DriveFile, metadata: FileMetadata): FileOperationResult

    suspend fun downloadFile(fileId: String): FileOperationResult

    suspend fun deleteFile(fileId: String): FileOperationResult

    suspend fun intelligentSync(config: SyncConfiguration): FileOperationResult

    suspend fun uploadFile(file: File, metadata: Map<String, Any>?): FileOperationResult
}
