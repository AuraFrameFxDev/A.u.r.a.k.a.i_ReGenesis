package dev.aurakai.auraframefx.network

import dev.aurakai.auraframefx.network.api.UserApi
import dev.aurakai.auraframefx.network.api.AIAgentApi
import dev.aurakai.auraframefx.network.api.ThemeApi
import javax.inject.Inject

/**
 * Wrapper around API service interfaces used by repositories.
 * Provides centralized access to all API services.
 */
class AuraApiServiceWrapper @Inject constructor(
    val userApi: UserApi,
    val aiAgentApi: AIAgentApi,
    val themeApi: ThemeApi
)

