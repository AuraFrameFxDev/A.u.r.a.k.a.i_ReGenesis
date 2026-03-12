package dev.aurakai.auraframefx.domains.cascade.network.infrastructure

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.nio.charset.StandardCharsets

/**
 * Adapter for serializing/deserializing ByteArray to/from JSON strings.
 * Uses ISO-8859-1 encoding to ensure proper binary data preservation.
 */
class ByteArrayAdapter

@ToJson
fun toJson(byteArray: ByteArray): String {
    return String(byteArray, StandardCharsets.ISO_8859_1)
}

@FromJson
fun fromJson(byteArrayString: String): ByteArray {
    return byteArrayString.toByteArray(StandardCharsets.ISO_8859_1)
}
