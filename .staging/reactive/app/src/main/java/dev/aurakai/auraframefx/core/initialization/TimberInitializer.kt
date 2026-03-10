package dev.aurakai.auraframefx.core.initialization

import android.app.Application
import android.util.Log
import com.google.ai.client.generativeai.BuildConfig
import dev.aurakai.auraframefx.core.logging.DebugTreeWithClassAndMethod
import timber.log.Timber
import javax.inject.Inject

/**
 * Initializes Timber for application-wide logging.
 * In debug builds, plants a debug tree that shows the class name and method name.
 * In release builds, plants a crash reporting tree that forwards errors to crash reporting tools.
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

    /**
     * A tree which logs important information for crash reporting.
     */
    private class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority < Log.INFO) {
                return
            }

            // You can replace this with your actual crash reporting logic
            when (priority) {
                Log.INFO -> { /* Log informational messages */
                }

                Log.WARN -> { /* Log warnings */
                }

                Log.ERROR -> { /* Log errors */
                }

                Log.ASSERT -> { /* Log asserts */
                }
            }
            // FirebaseCrashlytics.getInstance().log("$tag: $message")
            // t?.let { FirebaseCrashlytics.getInstance().recordException(it) }
        }
    }

    fun logHealthMetric(metricName: String, value: String) {
        // Here you can add your system health tracking logic
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
