package dev.aurakai.auraframefx.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import java.time.Instant

object InstantSerializer : KSerializer<Instant> {
    override val descriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.LONG)
    override fun serialize(encoder: Encoder, value: Instant) = encoder.encodeLong(value.toEpochMilli())
    override fun deserialize(decoder: Decoder) = Instant.ofEpochMilli(decoder.decodeLong())
}
