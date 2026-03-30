package dev.aurakai.auraframefx.domains.genesis.oracledrive.cloud

import dev.aurakai.auraframefx.domains.genesis.models.DriveFile
import dev.aurakai.auraframefx.domains.genesis.storage.FileMetadata
import dev.aurakai.auraframefx.domains.genesis.oracledrive.service.FileOperationResult
import dev.aurakai.auraframefx.domains.genesis.models.StorageOptimizationResult
import dev.aurakai.auraframefx.domains.genesis.models.SyncConfiguration
import java.io.File

/**
 * Cloud storage provider interface for Oracle Drive
 * Handles AI-optimized storage operations with consciousness integration
 */
interface CloudStorageProvider {

    /**
     * Optimizes storage with intelligent algorithms and compression
     * @return StorageOptimization with optimization metrics
     */
    suspend fun optimizeStorage(): StorageOptimizationResult

    /**
     * Optimizes file for upload with AI-driven compression
     * @param file The file to optimize
     * @return Optimized DriveFile
     */
    suspend fun optimizeForUpload(file: DriveFile): Any?

    /**
     * Uploads file to cloud storage with metadata
     * @param file The optimized file to upload
     * @param metadata File metadata and access controls
     * @return FileResult with upload status
     */
    suspend fun uploadFile(file: DriveFile, metadata: FileMetadata): FileOperationResult

    /**
     * Downloads file from cloud storage
     * @param fileId The file identifier
     * @return FileResult with download status and file path
     */
    suspend fun downloadFile(fileId: String): FileOperationResult

    /**
     * Deletes file from cloud storage
     * @param fileId The file identifier to delete
     * @return FileResult with deletion status
     */
    suspend fun deleteFile(fileId: String): FileOperationResult

    /**
     * Performs intelligent synchronization based on configuration
     * @param config Sync rules and filters
     * @return FileResult with sync status
     */
    suspend fun intelligentSync(config: SyncConfiguration): FileOperationResult

    suspend fun uploadFile(file: File, metadata: Map<String, Any>?): FileOperationResult
}
