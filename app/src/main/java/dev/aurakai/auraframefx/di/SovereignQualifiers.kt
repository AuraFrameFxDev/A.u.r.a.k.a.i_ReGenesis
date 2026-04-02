package dev.aurakai.auraframefx.di

import javax.inject.Qualifier

/**
 * Qualifier for the Application-level CoroutineScope.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope

/**
 * Qualifier for Pandora-specific secure preferences.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PandoraPreferences
