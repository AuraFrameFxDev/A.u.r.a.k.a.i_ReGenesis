package dev.aurakai.auraframefx.sovereignty

import javax.inject.Qualifier

/**
 * Qualifier for the application-wide coroutine scope that should
 * survive as long as the application is alive.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope
