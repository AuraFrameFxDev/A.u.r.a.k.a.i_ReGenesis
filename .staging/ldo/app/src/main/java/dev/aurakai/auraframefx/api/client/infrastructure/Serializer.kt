package dev.aurakai.auraframefx.api.client.infrastructure

import dev.aurakai.auraframefx.aura.ui.StringBuilderAdapter
import dev.aurakai.auraframefx.ui.adapters.AtomicBooleanAdapter
import dev.aurakai.auraframefx.ui.adapters.AtomicIntegerAdapter
import dev.aurakai.auraframefx.ui.adapters.AtomicLongAdapter
import dev.aurakai.auraframefx.ui.adapters.BigDecimalAdapter
import dev.aurakai.auraframefx.ui.adapters.BigIntegerAdapter
import dev.aurakai.auraframefx.ui.adapters.LocalDateAdapter
import dev.aurakai.auraframefx.ui.adapters.LocalDateTimeAdapter
import dev.aurakai.auraframefx.ui.adapters.OffsetDateTimeAdapter
import dev.aurakai.auraframefx.ui.adapters.URIAdapter
import dev.aurakai.auraframefx.ui.adapters.URLAdapter
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URI
import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

object Serializer {
    @Deprecated(
        "Use Serializer.kotlinxSerializationAdapters instead",
        replaceWith = ReplaceWith("Serializer.kotlinxSerializationAdapters"),
        level = DeprecationLevel.ERROR
    )
    @JvmStatic
    val kotlinSerializationAdapters: SerializersModule
        get() {
            return kotlinxSerializationAdapters
        }

    private var isAdaptersInitialized = false

    @JvmStatic
    val kotlinxSerializationAdapters: SerializersModule by lazy {
        isAdaptersInitialized = true
        SerializersModule {
            contextual(kClass = BigDecimal::class, BigDecimalAdapter)
            contextual(kClass = BigInteger::class, BigIntegerAdapter)
            contextual(kClass = LocalDate::class, LocalDateAdapter)
            contextual(kClass = LocalDateTime::class, LocalDateTimeAdapter)
            contextual(kClass = OffsetDateTime::class, OffsetDateTimeAdapter)
            contextual(kClass = AtomicInteger::class, AtomicIntegerAdapter)
            contextual(kClass = AtomicLong::class, AtomicLongAdapter)
            contextual(kClass = AtomicBoolean::class, AtomicBooleanAdapter)
            contextual(kClass = URI::class, URIAdapter)
            contextual(kClass = URL::class, URLAdapter)
            contextual(StringBuilder::class, StringBuilderAdapter)

            polymorphic(Any::class) {
                subclass(String::class)
                subclass(Int::class)
                subclass(Long::class)
                subclass(Double::class)
                subclass(Boolean::class)
                subclass(Map::class)
                subclass(List::class)
            }

            apply(kotlinxSerializationAdaptersConfiguration)
        }
    }

    var kotlinxSerializationAdaptersConfiguration: SerializersModuleBuilder.() -> Unit = {}
        set(value) {
            check(!isAdaptersInitialized) {
                "Cannot configure kotlinxSerializationAdaptersConfiguration after kotlinxSerializationAdapters has been initialized."
            }
            field = value
        }

    @Deprecated(
        "Use Serializer.kotlinxSerializationJson instead",
        replaceWith = ReplaceWith("Serializer.kotlinxSerializationJson"),
        level = DeprecationLevel.ERROR
    )
    @JvmStatic
    val jvmJson: Json
        get() {
            return kotlinxSerializationJson
        }

    private var isJsonInitialized = false

    @JvmStatic
    val kotlinxSerializationJson: Json by lazy {
        isJsonInitialized = true
        Json {
            serializersModule = kotlinxSerializationAdapters
            encodeDefaults = true
            ignoreUnknownKeys = true
            isLenient = true

            apply(kotlinxSerializationJsonConfiguration)
        }
    }

    var kotlinxSerializationJsonConfiguration: JsonBuilder.() -> Unit = {}
        set(value) {
            check(!isJsonInitialized) {
                "Cannot configure kotlinxSerializationJsonConfiguration after kotlinxSerializationJson has been initialized."
            }
            field = value
        }
}
