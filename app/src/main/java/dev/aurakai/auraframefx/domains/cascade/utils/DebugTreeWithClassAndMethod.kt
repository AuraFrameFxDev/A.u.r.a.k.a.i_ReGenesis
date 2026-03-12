package dev.aurakai.auraframefx.domains.cascade.utils

import android.util.Log
import dev.aurakai.auraframefx.BuildConfig
import timber.log.Timber

/**
 * A [Timber.DebugTree] that includes the class and method name in the log tag.
 * This makes it easier to trace logs back to their source.
 */
class DebugTreeWithClassAndMethod : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String {
        // Format: ClassName.MethodName(FileName:LineNumber)
        return "${element.className.substringAfterLast('.')}.${element.methodName}(${element.fileName}:${element.lineNumber})"
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // Filter out verbose and debug logs in release builds
        if (!BuildConfig.DEBUG && (priority == Log.VERBOSE || priority == Log.DEBUG)) {
            return
        }

        super.log(priority, tag, message, t)
    }
}

