package dev.aurakai.auraframefx.core.logging

import android.app.Application
import android.util.Log
import dev.aurakai.auraframefx.BuildConfig
import timber.log.Timber
import javax.inject.Inject

/**
 * Initializes Timber for application-wide logging.
 */
class TimberInitializer @Inject constructor() {

    fun initialize(application: Application) {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTreeWithClassAndMethod())
            Timber.tag("AuraFrameFX")
                .d("Timber initialized in DEBUG mode with class/method logging")
        } else {
            Timber.plant(CrashReportingTree())
            Timber.tag("AuraFrameFX").i("Timber initialized in RELEASE mode with crash reporting")
        }
    }

    private class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority < Log.INFO) {
                return
            }
            // Crash reporting logic placeholder
        }
    }

    fun logHealthMetric(metricName: String, value: String) {
        Timber.tag("HealthTracker").i("$metricName: $value")
    }
}

/**
 * Logs a debug message with the class name and method name.
 */
inline fun <reified T> T.logd(message: String, vararg args: Any?) {
    val tag = T::class.java.simpleName
    Timber.tag(tag).d(message, *args)
}

/**
 * Logs an error message with the class name and method name.
 */
inline fun <reified T> T.loge(message: String, vararg args: Any?) {
    val tag = T::class.java.simpleName
    Timber.tag(tag).e(message, *args)
}

/**
 * Logs an error with throwable and the class name.
 */
inline fun <reified T> T.loge(throwable: Throwable?, message: String, vararg args: Any?) {
    val tag = T::class.java.simpleName
    Timber.tag(tag).e(throwable, message, *args)
}
