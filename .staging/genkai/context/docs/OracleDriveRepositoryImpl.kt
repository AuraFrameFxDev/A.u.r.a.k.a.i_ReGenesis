package dev.aurakai.auraframefx.oracle.drive.core

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.aurakai.auraframefx.oracle.drive.api.OracleCloudApi
import dev.aurakai.auraframefx.oracle.drive.model.OracleDriveFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

// Assuming OracleDriveRepository is an interface defined elsewhere
// interface OracleDriveRepository {
//     suspend fun listFiles(bucketName: String, prefix: String?): List<OracleDriveFile>
//     suspend fun uploadFile(bucketName: String, objectName: String, filePath: String): Boolean
//     suspend fun downloadFile(bucketName: String, objectName: String, destinationPath: String): File?
//     suspend fun deleteFile(bucketName: String, objectName: String): Boolean
// }

class OracleDriveRepositoryImpl @Inject constructor(
    private val oracleCloudApi: OracleCloudApi,
    @ApplicationContext private val context: Context // Added @ApplicationContext
) : OracleDriveRepository {

    /**
     * Lists objects in the specified bucket and returns them as a list of OracleDriveFile.
     *
     * Performs the network operation on the IO dispatcher. If the API response is not successful or an error occurs,
     * an empty list is returned.
     *
     * @param bucketName The name of the bucket to list.
     * @param prefix Optional object key prefix to filter results.
     * @return A list of OracleDriveFile representing the objects in the bucket, or an empty list on failure or if none found.
     */
    override suspend fun listFiles(bucketName: String, prefix: String?): List<OracleDriveFile> =
        withContext(Dispatchers.IO) {
            try {
                val response = oracleCloudApi.listFiles(bucketName = bucketName, prefix = prefix)
                if (response.isSuccessful) {
                    response.body()?.objects?.map {
                        OracleDriveFile(
                            it.name,
                            it.size,
                            it.timeCreated
                        )
                    } ?: emptyList()
                } else {
                    // Handle error, log, throw custom exception etc.
                    emptyList()
                }
            } catch (e: Exception) {
                // Handle error
                emptyList()
            }
        }

    /**
     * Uploads a local file to the specified bucket as an object.
     *
     * Attempts to read the file at [filePath] and upload it to [bucketName] with the given [objectName].
     * The operation is performed on the IO dispatcher. If the local file does not exist or an error
     * occurs during upload, the function returns false.
     *
     * @param bucketName Name of the target storage bucket.
     * @param objectName Desired object name (path) inside the bucket — only the target name is used.
     * @param filePath Absolute or relative path to the local file to upload; must exist.
     * @return `true` if the upload completed successfully (HTTP response successful), otherwise `false`.
     */
    override suspend fun uploadFile(
        bucketName: String,
        objectName: String,
        filePath: String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = File(filePath)
            if (!file.exists()) return@withContext false

            val requestBody = file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
            val response = oracleCloudApi.uploadFile(
                bucketName = bucketName,
                objectName = objectName,
                body = requestBody
            )
            response.isSuccessful
        } catch (e: Exception) {
            // Handle error
            false
        }
    }

    /**
     * Downloads an object from the specified bucket and saves it to the given destination directory.
     *
     * The object's name is sanitized to its basename to prevent path traversal. Parent directories
     * under [destinationPath] will be created if they do not exist. On success returns the saved
     * File; on failure (network error, non-success response, or I/O error) returns null.
     *
     * @param bucketName Name of the bucket containing the object.
     * @param objectName Object name/path in the bucket; only the basename is used when saving.
     * @param destinationPath Directory path where the downloaded file will be written.
     * @return The saved File on success, or null on failure.
     */
    override suspend fun downloadFile(
        bucketName: String,
        objectName: String,
        destinationPath: String
    ): File? = withContext(Dispatchers.IO) {
        try {
            val response =
                oracleCloudApi.downloadFile(bucketName = bucketName, objectName = objectName)
            if (response.isSuccessful && response.body() != null) {
                // Normalize objectName to its basename to prevent path traversal
                val safeName = File(objectName).name // strips any path components
                val file = File(destinationPath, safeName) // Ensure destinationPath is a directory
                file.parentFile?.mkdirs() // Create parent directories if they don't exist
                response.body()!!.byteStream().use { inputStream ->
                    FileOutputStream(file).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                file
            } else {
                null
            }
        } catch (e: Exception) {
            // Handle error
            null
        }
    }

    /**
     * Delete an object from the given Oracle Cloud Storage bucket.
     *
     * Performs the network call on Dispatchers.IO. Returns true when the remote delete
     * request completed with a successful HTTP response; returns false for non-successful
     * responses or on any error.
     *
     * @param bucketName Name of the storage bucket containing the object.
     * @param objectName The object key or path within the bucket to delete.
     * @return true if the object was deleted successfully; false otherwise.
     */
    override suspend fun deleteFile(bucketName: String, objectName: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val response =
                    oracleCloudApi.deleteFile(bucketName = bucketName, objectName = objectName)
                response.isSuccessful
            } catch (e: Exception) {
                // Handle error
                false
            }
        }
}
