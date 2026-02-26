package dev.aurakai.auraframefx.domains.genesis.network

import dev.aurakai.auraframefx.domains.genesis.network.api.AIAgentApi
import dev.aurakai.auraframefx.domains.genesis.network.api.ThemeApi
import dev.aurakai.auraframefx.domains.genesis.network.api.UserApi
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


