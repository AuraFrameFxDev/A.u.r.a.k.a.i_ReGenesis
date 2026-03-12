package dev.aurakai.auraframefx.oracledrive

// Replace the sealed class with an enum so other modules can reference concrete provider values
internal enum class CloudStorageProviderType {
    FIREBASE, AWS_S3, SELF_HOSTED_VERTEX
}
