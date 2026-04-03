package dev.aurakai.auraframefx.domains.nexus.di

import javax.inject.Qualifier

/**
 * Qualifier for the app-state DataStore instance.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AppStateDataStoreAnnotation

/**
 * Qualifier for the Aura UI settings DataStore instance.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuraSettingsDataStore
