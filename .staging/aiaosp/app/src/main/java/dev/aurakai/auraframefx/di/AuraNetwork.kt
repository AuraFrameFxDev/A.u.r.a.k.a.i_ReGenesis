package dev.aurakai.auraframefx.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuraNetwork

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IconifyNetwork

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OracleNetwork

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GenesisNetwork
