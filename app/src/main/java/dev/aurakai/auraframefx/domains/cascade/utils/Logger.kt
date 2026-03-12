package dev.aurakai.auraframefx.domains.cascade.utils

import timber.log.Timber

/**
 * Global logger object for Timber-based logging.
 * Provides both short-form methods (i, d, w, e) and long-form methods (info, debug, warn, error).
 *
 * Usage:
 *   logger.i("TAG", "message")
 *   logger.info("TAG", "message")
 *   logger.e("TAG", "message", throwable)
 */
object logger {
    // Short-form tagged methods
    fun i(tag: String, message: String) {
        Timber.tag(tag).i(message)
    }

    fun d(tag: String, message: String) {
        Timber.tag(tag).d(message)
    }

    fun w(tag: String, message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Timber.tag(tag).w(throwable, message)
        } else {
            Timber.tag(tag).w(message)
        }
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Timber.tag(tag).e(throwable, message)
        } else {
            Timber.tag(tag).e(message)
        }
    }

    // Long-form tagged methods (aliases)
    fun info(tag: String, message: String) = i(tag, message)
    fun debug(tag: String, message: String) = d(tag, message)
    fun warn(tag: String, message: String, throwable: Throwable? = null) =
        w(tag, message, throwable)

    fun error(tag: String, message: String, throwable: Throwable? = null) =
        e(tag, message, throwable)

    // Simple message-only methods (use class name from callsite as tag)
    fun i(message: String) = Timber.i(message)
    fun d(message: String) = Timber.d(message)
    fun w(message: String) = Timber.w(message)
    fun e(message: String, throwable: Throwable? = null) = Timber.e(throwable, message)
}

// Uppercase alias for compatibility
typealias Logger = logger

// ============================================================================
// TOP-LEVEL FUNCTIONS - For direct import style
// Provides both single-argument and two-argument (tag + message) overloads
// ============================================================================

// Single-argument versions (message only)
fun i(message: String) {
    Timber.i(message)
}

fun d(message: String) {
    Timber.d(message)
}

fun w(message: String) {
    Timber.w(message)
}

fun e(message: String, throwable: Throwable? = null) {
    Timber.e(throwable, message)
}

// Two-argument versions (tag + message)
fun i(tag: String, message: String) {
    Timber.tag(tag).i(message)
}

fun d(tag: String, message: String) {
    Timber.tag(tag).d(message)
}

fun w(tag: String, message: String, throwable: Throwable? = null) {
    if (throwable != null) {
        Timber.tag(tag).w(throwable, message)
    } else {
        Timber.tag(tag).w(message)
    }
}

fun e(tag: String, message: String, throwable: Throwable? = null) {
    if (throwable != null) {
        Timber.tag(tag).e(throwable, message)
    } else {
        Timber.tag(tag).e(message)
    }
}

// Long-form method names (info, debug, warn, error) - Two-argument versions
fun info(tag: String, message: String) {
    Timber.tag(tag).i(message)
}

fun debug(tag: String, message: String) {
    Timber.tag(tag).d(message)
}

fun warn(tag: String, message: String, throwable: Throwable? = null) {
    if (throwable != null) {
        Timber.tag(tag).w(throwable, message)
    } else {
        Timber.tag(tag).w(message)
    }
}

fun error(tag: String, message: String, throwable: Throwable? = null) {
    if (throwable != null) {
        Timber.tag(tag).e(throwable, message)
    } else {
        Timber.tag(tag).e(message)
    }
}

// Long-form method names - Single-argument versions (message only)
fun info(message: String) {
    Timber.i(message)
}

fun debug(message: String) {
    Timber.d(message)
}

fun warn(message: String, throwable: Throwable? = null) {
    if (throwable != null) {
        Timber.w(throwable, message)
    } else {
        Timber.w(message)
    }
}

fun error(message: String, throwable: Throwable? = null) {
    if (throwable != null) {
        Timber.e(throwable, message)
    } else {
        Timber.e(message)
    }
}

