//package dev.aurakai.auraframefx.ipc
//
//import android.app.Service
//import android.content.Context
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.os.IBinder
//import android.os.RemoteCallbackList
//import dev.aurakai.auraframefx.app.ipc.IAuraDriveService
//import dev.aurakai.auraframefx.oracledrive.OracleCloudStorageProvider
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.runBlocking
//import timber.log.Timber
//import java.io.File
//import java.io.FileOutputStream
//import java.io.InputStream
//import java.lang.Exception
//
//interface IAuraDriveCallback : android.os.IInterface
//
//class AuraDriveService : Service() {
//
//    private val callbacks = RemoteCallbackList<IAuraDriveCallback>()
//
//    private sealed class UploadResult {
//        data class Success(val fileId: String) : UploadResult()
//        data class Error(val cause: Throwable?) : UploadResult()
//    }
//
//    private val cloudProviderImpl: CloudStorageProvider by lazy {
//        OracleCloudStorageProvider.create("auradrive-default")
//    }
//
//    private val binder by lazy {
//        object : IAuraDriveService.Stub() {
//            override fun getServiceVersion(): String = "1.0"
//
//            override fun registerCallback(callback: IAuraDriveCallback?) {
//                callback ?: return
//                callbacks.register(callback)
//            }
//
//            override fun unregisterCallback(callback: IAuraDriveCallback?) {
//                callback ?: return
//                callbacks.unregister(callback)
//            }
//
//            override fun executeCommand(command: String?, params: Bundle?): String {
//                return when (command) {
//                    "ping" -> "pong"
//                    else -> "unsupported"
//                }
//            }
//
//            override fun getOracleDriveStatus(): String = "OK"
//
//            override fun getDetailedInternalStatus(): String = "AuraDriveService running"
//
//            override fun getInternalDiagnosticsLog(): List<String> = listOf("No diagnostics available")
//
//            override fun importFile(uri: Uri?): String {
//                if (uri == null) return ""
//                return try {
//                    val tmp = uriToTempFile(uri) ?: return ""
//                    when (val result = runBlockingUpload(tmp)) {
//                        is UploadResult.Success -> result.fileId
//                        is UploadResult.Error -> ""
//                    }
//                } catch (ex: Exception) {
//                    Timber.w(ex, "importFile failed")
//                    ""
//                }
//            }
//
//            override fun exportFile(fileId: String?, destinationUri: Uri?): Boolean {
//                if (fileId.isNullOrEmpty()) return false
//                return try {
//                    val res = runBlocking(Dispatchers.IO) {
//                        when (val r = cloudProviderImpl.downloadFile(fileId)) {
//                            is FileResult.Companion.Success -> UploadResult.Success(r.fileId)
//                            is FileResult.Error -> UploadResult.Error(r.cause)
//                        }
//                    }
//                    res is UploadResult.Success
//                } catch (ex: Exception) {
//                    Timber.w(ex, "exportFile failed")
//                    false
//                }
//            }
//
//            override fun verifyFileIntegrity(fileId: String?): Boolean {
//                if (fileId.isNullOrEmpty()) return false
//                return try {
//                    val res = runBlocking(Dispatchers.IO) {
//                        when (val r = cloudProviderImpl.downloadFile(fileId)) {
//                            is FileResult.Companion.Success -> UploadResult.Success(r.fileId)
//                            is FileResult.Error -> UploadResult.Error(r.cause)
//                        }
//                    }
//                    res is UploadResult.Success
//                } catch (ex: Exception) {
//                    Timber.w(ex, "verifyFileIntegrity failed")
//                    false
//                }
//            }
//        }
//    }
//
//    override fun onBind(intent: Intent?): IBinder = binder
//
//    override fun onDestroy() {
//        callbacks.kill()
//        super.onDestroy()
//    }
//
//    private fun uriToTempFile(uri: Uri): File? {
//        return try {
//            val input: InputStream? = contentResolver.openInputStream(uri)
//            input ?: return null
//            val tmp = File.createTempFile("auradrive-import-", ".tmp", cacheDir)
//            FileOutputStream(tmp).use { out ->
//                input.use { inp ->
//                    inp.copyTo(out)
//                }
//            }
//            tmp
//        } catch (ex: Exception) {
//            Timber.w(ex, "uriToTempFile failed")
//            null
//        }
//    }
//
//    private fun runBlockingUpload(file: File): Any {
//        return try {
//            runBlocking(Dispatchers.IO) {
//                when (val result = cloudProviderImpl.uploadFile(
//                    file,
//                    createDeviceProtectedStorageContext()
//                )) {
//                    is FileResult.Companion.Success -> UploadResult.Success(result.fileId)
//                    is FileResult.Error -> UploadResult.Error(result.cause)
//                }
//            }
//        } catch (ex: Exception) {
//            Timber.w(ex, "runBlockingUpload failed")
//            UploadResult.Error(ex)
//        }
//    }
//
//    private fun uploadFile(file: File, createDeviceProtectedStorageContext: Context) {}
//}
