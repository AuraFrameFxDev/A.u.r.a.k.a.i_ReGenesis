package dev.aurakai.auraframefx.logging

import android.util.Log
import javax.inject.Singleton

@Singleton
class AuraFxLogger {
    
    fun d(tag: String, message: String) {
        Log.d(tag, message)
    }
    
    fun i(tag: String, message: String) {
        Log.i(tag, message)
    }
    
    fun w(tag: String, message: String, e: Throwable? = null) {
        if (e != null) {
            Log.w(tag, message, e)
        } else {
            Log.w(tag, message)
        }
    }
    
    fun e(tag: String, message: String, e: Throwable? = null) {
        if (e != null) {
            Log.e(tag, message, e)
        } else {
            Log.e(tag, message)
        }
    }
}
