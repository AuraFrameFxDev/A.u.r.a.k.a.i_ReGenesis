package dev.aurakai.auraframefx.domains.genesis.oracledrive.cloud

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

private const val TAG = "OracleDriveRepo"
private const val OCTET_STREAM_MIME = "application/octet-stream"

open class OracleDriveRepositoryImpl @Inject constructor(
    private val oracleCloudApi: OracleCloudApi,
    @param:ApplicationContext private val context: Context // Added @ApplicationContext
) : OracleDriveRepository {

    /**
     * Retrieve the objects in the specified bucket as a list of OracleDriveFile.
     *
     * The result is mapped from the API response objects; if the bucket contains no objects the returned list will be empty.
     *
     * @param bucketName The name of the bucket to list.
     * @param prefix Optional object key prefix to filter results.
     * @return A list of OracleDriveFile representing the objects in the bucket; empty if none are found.
     * @throws OracleDriveException If the API response is unsuccessful or an error occurs while listing files.
     */
    override suspend fun listFiles(bucketName: String, prefix: String?): List<OracleDriveFile> =
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Fetching files from bucket: $bucketName, prefix: $prefix")
                val response = oracleCloudApi.listFiles(bucketName = bucketName, prefix = prefix)

                if (!response.isSuccessful) {
                    throw OracleDriveException(
                        "Failed to list files. Code: ${response.code()}, Message: ${response.message()}"
                    )
                }

                val files: List<OracleDriveFile> = response.body()?.objects?.map { file ->
                    OracleDriveFile(
                        name = file.name,
                        size = file.size,
                        timeCreated = file.timeCreated
                    )
                } ?: emptyList()

                Log.d(TAG, "Successfully listed ${files.size} files")
                files

            } catch (e: Exception) {
                Log.e(TAG, "Error listing files in bucket: $bucketName", e)
                throw when (e) {
                    is OracleDriveException -> e
                    else -> OracleDriveException("Failed to list files: ${e.message}", e)
                }
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
        require(bucketName.isNotBlank()) { "Bucket name cannot be blank" }
        require(objectName.isNotBlank()) { "Object name cannot be blank" }
        require(filePath.isNotBlank()) { "File path cannot be blank" }

        val file = File(filePath)
        if (!file.exists()) {
            Log.e(TAG, "File not found: $filePath")
            return@withContext false
        }

        try {
            Log.d(TAG, "Uploading file: $filePath to bucket: $bucketName/$objectName")

            val requestBody = file.asRequestBody(OCTET_STREAM_MIME.toMediaTypeOrNull())
            val response = oracleCloudApi.uploadFile(
                bucketName = bucketName,
                objectName = objectName,
                body = requestBody
            )

            if (!response.isSuccessful) {
                Log.e(
                    TAG,
                    "Upload failed. Code: ${response.code()}, Message: ${response.message()}"
                )
                return@withContext false
            }

            Log.i(TAG, "Successfully uploaded file: $filePath")
            true

        } catch (e: IOException) {
            Log.e(TAG, "IO error during file upload: ${e.message}", e)
            false
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during file upload", e)
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
        require(bucketName.isNotBlank()) { "Bucket name cannot be blank" }
        require(objectName.isNotBlank()) { "Object name cannot be blank" }
        require(destinationPath.isNotBlank()) { "Destination path cannot be blank" }

        try {
            Log.d(TAG, "Downloading file: $bucketName/$objectName to $destinationPath")

            val response =
                oracleCloudApi.downloadFile(bucketName = bucketName, objectName = objectName)

            if (!response.isSuccessful) {
                Log.e(
                    TAG,
                    "Download failed. Code: ${response.code()}, Message: ${response.message()}"
                )
                return@withContext null
            }

            val responseBody = response.body() ?: run {
                Log.e(TAG, "Download failed: Empty response body")
                return@withContext null
            }

            val safeName = File(objectName).name // Prevent path traversal
            val destinationDir = File(destinationPath).also { it.mkdirs() }
            val outputFile = File(destinationDir, safeName)

            responseBody.byteStream().use { inputStream ->
                FileOutputStream(outputFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            if (!outputFile.exists() || outputFile.length() == 0L) {
                Log.e(TAG, "Download failed: Output file was not created or is empty")
                outputFile.delete() // Clean up empty file
                return@withContext null
            }

            Log.i(TAG, "Successfully downloaded file to: ${outputFile.absolutePath}")
            outputFile

        } catch (e: IOException) {
            Log.e(TAG, "IO error during file download: ${e.message}", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during file download", e)
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
            require(bucketName.isNotBlank()) { "Bucket name cannot be blank" }
            require(objectName.isNotBlank()) { "Object name cannot be blank" }

            try {
                Log.d(TAG, "Deleting file: $bucketName/$objectName")

                val response = oracleCloudApi.deleteFile(
                    bucketName = bucketName,
                    objectName = objectName
                )

                if (!response.isSuccessful) {
                    Log.e(
                        TAG,
                        "Delete failed. Code: ${response.code()}, Message: ${response.message()}"
                    )
                    return@withContext false
                }

                Log.i(TAG, "Successfully deleted file: $bucketName/$objectName")
                true

            } catch (e: Exception) {
                Log.e(TAG, "Error deleting file: $bucketName/$objectName", e)
                false
            }
        }
}

