package dev.aurakai.auraframefx.oracle.drive.utils

import android.util.Log

/**
 * Logger — Unified logging utility for Oracle Drive
 */
object Logger {
    private const val TAG = "AuraFrameFX"
    
    fun d(tag: String = TAG, message: String) {
        Log.d(tag, message)
    }
    
    fun e(tag: String = TAG, message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.e(tag, message, throwable)
        } else {
            Log.e(tag, message)
        }
    }
    
    fun i(tag: String = TAG, message: String) {
        Log.i(tag, message)
    }
    
    fun w(tag: String = TAG, message: String) {
        Log.w(tag, message)
    }
}
