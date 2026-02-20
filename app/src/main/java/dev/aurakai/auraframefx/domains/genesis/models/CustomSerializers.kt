/**
 * Custom serializer for Any type - use with @Contextual annotation
 * Encodes the given value as a string by calling `toString()` and writing it to the encoder.
 */
object AnySerializer : KSerializer<Any> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Any", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Any) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Any {
        return decoder.decodeString()
    }
}
