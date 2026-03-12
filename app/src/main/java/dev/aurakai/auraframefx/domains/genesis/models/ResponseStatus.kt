package dev.aurakai.auraframefx.domains.genesis.models

import kotlinx.serialization.Serializable

@Serializable
enum class ResponseStatus {
    SUCCESS,
    FAILURE,
    PARTIAL,
    TIMEOUT,
    PENDING
}
