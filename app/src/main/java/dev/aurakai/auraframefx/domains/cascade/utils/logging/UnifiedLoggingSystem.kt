package dev.aurakai.auraframefx.domains.cascade.utils.logging

import android.util.Log
import timber.log.Timber
import dev.aurakai.auraframefx.BuildConfig

/**
 * Unified logging system for the application.
 * Uses Timber for logging with different behaviors in debug and release builds.
 */
object UnifiedLoggingSystem {
    private const val TAG = "AuraFX"

    /**
     * Initialize the logging system.
     * Should be called in the Application class.
     */
    fun initialize() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }
    }

    /**
     * Log a debug message.
     * @param message The message to log.
     * @param throwable Optional throwable to log.
     */
    fun d(message: String, throwable: Throwable? = null) {
        Timber.d(throwable, message)
    }

    /**
     * Log an error message.
     * @param message The message to log.
     * @param throwable Optional throwable to log.
     */
    fun e(message: String, throwable: Throwable? = null) {
        Timber.e(throwable, message)
    }

    /**
     * Log an informational message.
     * @param message The message to log.
     * @param throwable Optional throwable to log.
     */
    fun i(message: String, throwable: Throwable? = null) {
        Timber.i(throwable, message)
    }

    /**
     * Log a warning message.
     * @param message The message to log.
     * @param throwable Optional throwable to log.
     */
    fun w(message: String, throwable: Throwable? = null) {
        Timber.w(throwable, message)
    }

    /**
     * Log a verbose message.
     * @param message The message to log.
     * @param throwable Optional throwable to log.
     */
    fun v(message: String, throwable: Throwable? = null) {
        Timber.v(throwable, message)
    }

    /**
     * Custom Timber.Tree for release builds.
     * Only logs errors and warnings, and can be extended to report to crash reporting services.
     */
    private class ReleaseTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.ERROR || priority == Log.WARN) {
                // In release builds, only log errors and warnings
                // Add crash reporting integration here if needed
                Log.println(priority, tag ?: TAG, message)
                t?.let { Log.println(priority, tag ?: TAG, Log.getStackTraceString(it)) }
            }
        }
    }
}

