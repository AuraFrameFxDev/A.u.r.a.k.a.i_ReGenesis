package dev.aurakai.auraframefx.python

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PythonProcessManager @Inject constructor(
    private val context: Context
) {
    suspend fun startGenesisBackend(): Boolean = false
    suspend fun shutdown() {}
    suspend fun sendRequest(request: String): String = ""
}
