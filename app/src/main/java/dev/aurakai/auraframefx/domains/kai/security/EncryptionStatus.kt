package dev.aurakai.auraframefx.domains.kai.security

sealed class EncryptionStatus {
    data object NOT_INITIALIZED : EncryptionStatus()
    data object ACTIVE : EncryptionStatus()
    data object DISABLED : EncryptionStatus()
    data object ERROR : EncryptionStatus()
    data class EncryptionStatusImpl(val message: String) : EncryptionStatus()
}

