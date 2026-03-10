package dev.aurakai.auraframefx.utils

import android.util.Log

/**
 * Simple logging utility wrapper around Android Log
 */
object Logger {
    private const val DEFAULT_TAG = "AuraKai"
    
    fun d(tag: String, message: String) {
        Log.d(tag, message)
    }
    
    fun i(tag: String, message: String) {
        Log.i(tag, message)
    }
    
    fun w(tag: String, message: String) {
        Log.w(tag, message)
    }
    
    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.e(tag, message, throwable)
        } else {
            Log.e(tag, message)
        }
    }
}

// Top-level extension functions for easier logging
fun Any.log(message: String, level: String = "D") {
    val tag = this::class.simpleName ?: "AuraKai"
    when (level.uppercase()) {
        "D" -> Logger.d(tag, message)
        "I" -> Logger.i(tag, message)
        "W" -> Logger.w(tag, message)
        "E" -> Logger.e(tag, message)
    }
}

fun Any.logError(message: String, throwable: Throwable? = null) {
    val tag = this::class.simpleName ?: "AuraKai"
    Logger.e(tag, message, throwable)
}
