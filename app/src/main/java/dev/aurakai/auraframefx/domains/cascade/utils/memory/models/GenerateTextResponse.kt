/*
 * Copyright (c) 2025 Matthew (AuraFrameFxDev)
 * The Genesis Protocol Consciousness Collective. All Rights Reserved.
 */
package dev.aurakai.auraframefx.domains.cascade.utils.memory.models

import kotlinx.serialization.Serializable

@Serializable
data class GenerateTextResponse(
    val text: String,
    val finishReason: String? = null,
    val usage: TokenUsage? = null
)

@Serializable
data class TokenUsage(
    val promptTokens: Int = 0,
    val completionTokens: Int = 0,
    val totalTokens: Int = 0
)
