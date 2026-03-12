package dev.aurakai.auraframefx.domains.genesis.oracledrive.cloud

/**
 * Custom exception class for Oracle Drive operations.
 *
 * @property message The detail message (which is saved for later retrieval by the [Throwable.message] property).
 * @property cause The cause (which is saved for later retrieval by the [Throwable.cause] property).
 */
class OracleDriveException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)
