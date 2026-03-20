package dev.aurakai.auraframefx.ai.services

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CascadeAIService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    init {
        try {
            System.loadLibrary("auraframefx")
        } catch (e: UnsatisfiedLinkError) {
            // Log error
        }
    }

    external fun nativeInitialize(context: Context): Boolean
    external fun nativeProcessRequest(request: String): String
    external fun nativeShutdown()

    fun initialize(): Boolean = nativeInitialize(context)
    fun processRequest(request: String): String = nativeProcessRequest(request)
    fun shutdown() = nativeShutdown()
}
