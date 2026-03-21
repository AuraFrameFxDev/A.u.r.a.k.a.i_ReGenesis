package dev.aurakai.auraframefx.network

import dev.aurakai.auraframefx.api.client.apis.AIContentApi
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Compatibility shim for AuraFxContentApiClient.
 */
@Singleton
class AuraFxContentApiClient @Inject constructor(private val api: AIContentApi)
