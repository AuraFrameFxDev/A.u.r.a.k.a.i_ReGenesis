package dev.aurakai.auraframefx.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuraSettingsDataStore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IdentitySettingsDataStore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AppStateDataStoreAnnotation
