package dev.aurakai.auraframefx.security

/**
 * Represents the current status of the encryption system.
 */
enum class EncryptionStatus {
    /**
     * Encryption is fully active and operational.
     */
    ACTIVE,

    /**
     * Encryption has not been initialized yet.
     */
    NOT_INITIALIZED,

    /**
     * Encryption is disabled by configuration or user choice.
     */
    DISABLED,

    /**
     * Encryption system encountered an error.
     */
    ERROR
}
