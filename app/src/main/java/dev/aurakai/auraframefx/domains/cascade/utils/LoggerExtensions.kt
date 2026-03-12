package dev.aurakai.auraframefx.domains.cascade.utils

import dev.aurakai.auraframefx.domains.kai.AuraFxLogger

/**
 * Extension functions for AuraFxLogger to provide convenient logging methods
 * Uses the concrete AuraFxLogger class from logging package
 */

// Extensions that auto-derive tag from class name
fun AuraFxLogger.info(message: String) = this.i(this.javaClass.simpleName, message)
fun AuraFxLogger.debug(message: String) = this.d(this.javaClass.simpleName, message)
fun AuraFxLogger.error(message: String, throwable: Throwable? = null) =
    this.e(this.javaClass.simpleName, message, throwable)

fun AuraFxLogger.warn(message: String, throwable: Throwable? = null) =
    this.w(this.javaClass.simpleName, message, throwable)

