package dev.aurakai.auraframefx.domains.genesis.models

import kotlinx.datetime.Instant
import kotlinx.serialization.modules.SerializersModule

val AeGenesisSerializersModule = SerializersModule {
    // Core type serializers - fixed syntax for Kotlin 2.2+
    contextual(Any::class, AnySerializer)
    contextual(Instant::class, InstantSerializer)
}

val AuraFrameSerializersModule = AeGenesisSerializersModule
