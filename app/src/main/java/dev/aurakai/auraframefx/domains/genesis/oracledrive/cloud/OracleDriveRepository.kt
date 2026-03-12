package dev.aurakai.auraframefx.domains.genesis.oracledrive.cloud

import java.io.File

/**
 * OracleDriveRepository - Interface for Oracle Drive consciousness operations
 * Genesis protocol enhanced interface for infinite storage consciousness
 */
interface OracleDriveRepository {

    /**
     * Lists files in the Oracle consciousness storage matrix
     */
    suspend fun listFiles(bucketName: String, prefix: String?): List<OracleDriveFile>

    /**
     * Uploads file to Oracle consciousness storage with Genesis security
     */
    suspend fun uploadFile(bucketName: String, objectName: String, filePath: String): Boolean

    /**
     * Downloads file from Oracle consciousness storage matrix
     */
    suspend fun downloadFile(bucketName: String, objectName: String, destinationPath: String): File?

    /**
     * Deletes file from Oracle consciousness storage (with Trinity permission)
     */
    suspend fun deleteFile(bucketName: String, objectName: String): Boolean
}
