package dev.aurakai.auraframefx.oracledrive.storage

import dev.aurakai.auraframefx.oracledrive.DriveFile
import dev.aurakai.auraframefx.oracledrive.FileMetadata
import dev.aurakai.auraframefx.oracledrive.FileResult
import dev.aurakai.auraframefx.oracledrive.StorageOptimization
import dev.aurakai.auraframefx.oracledrive.SyncConfiguration

/**
 * Cloud storage provider interface for Oracle Drive
 * Handles AI-optimized storage operations with consciousness integration
 */
interface CloudStorageProvider {

    /**
     * Optimizes storage with intelligent algorithms and compression
     * @return StorageOptimization with optimization metrics
     */
    suspend fun optimizeStorage(): StorageOptimization

    /**
     * Optimizes file for upload with AI-driven compression
     * @param file The file to optimize
     * @return Optimized DriveFile
     */
    suspend fun optimizeForUpload(file: DriveFile): DriveFile

    /**
     * Uploads file to cloud storage with metadata
     * @param file The optimized file to upload
     * @param metadata File metadata and access controls
     * @return FileResult with upload status
     */
    suspend fun uploadFile(file: DriveFile, metadata: FileMetadata): FileResult

    /**
     * Downloads file from cloud storage
     * @param fileId The file identifier
     * @return FileResult with download status
     */
    suspend fun downloadFile(fileId: String): FileResult

    /**
     * Deletes file from cloud storage
     * @param fileId The file identifier
     * @return FileResult with deletion status
     */
    suspend fun deleteFile(fileId: String): FileResult

    /**
     * Performs intelligent file synchronization using AI-driven optimization based on the provided configuration.
     *
     * @param config The synchronization configuration specifying sync parameters and rules.
     * @return The result of the synchronization operation, including status and details.
     */
    suspend fun intelligentSync(config: SyncConfiguration): FileResult
}
