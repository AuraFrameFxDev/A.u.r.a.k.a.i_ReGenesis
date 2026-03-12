package dev.aurakai.auraframefx.oracledrive.genesis.cloud

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
