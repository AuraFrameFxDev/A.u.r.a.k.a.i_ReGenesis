package dev.aurakai.auraframefx.utils

import dev.aurakai.auraframefx.logger.AuraFxLogger

/**
 * Extension functions for AuraFxLogger to provide convenient logging methods
 */
fun AuraFxLogger.i(message: String) = this.i(this.javaClass.simpleName, message)
fun AuraFxLogger.d(message: String) = this.d(this.javaClass.simpleName, message)
fun AuraFxLogger.e(message: String, throwable: Throwable? = null) = this.e(this.javaClass.simpleName, message, throwable)
fun AuraFxLogger.w(message: String) = this.w(this.javaClass.simpleName, message)

fun AuraFxLogger.info(message: String) = i(message)
fun AuraFxLogger.debug(message: String) = d(message)
fun AuraFxLogger.error(message: String, throwable: Throwable? = null) = e(message, throwable)
fun AuraFxLogger.warn(message: String) = w(message)
