package dev.aurakai.auraframefx.core.di.qualifiers

import javax.inject.Qualifier

/**
 * Qualifier for the Application-level CoroutineScope.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope
