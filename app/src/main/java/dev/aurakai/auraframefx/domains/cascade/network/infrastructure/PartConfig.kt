package dev.aurakai.auraframefx.domains.cascade.network.infrastructure

/**
 * Configuration for multipart form data parts
 *
 * @param T The type of the body content
 * @property body The content of this part
 * @property headers Additional headers for this part
 */
data class PartConfig<T>(
    val body: T,
    val headers: Map<String, String> = emptyMap()
)
