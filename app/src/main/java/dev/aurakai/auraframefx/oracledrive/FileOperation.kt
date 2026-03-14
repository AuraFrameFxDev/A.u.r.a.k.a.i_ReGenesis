package dev.aurakai.auraframefx.oracledrive

import java.io.File

sealed class FileOperation {
    data class Upload(val file: File) : FileOperation()
    data class Download(val fileId: String) : FileOperation()
    data class Delete(val fileId: String) : FileOperation()
    data class Sync(val config: SyncConfig) : FileOperation()
}
