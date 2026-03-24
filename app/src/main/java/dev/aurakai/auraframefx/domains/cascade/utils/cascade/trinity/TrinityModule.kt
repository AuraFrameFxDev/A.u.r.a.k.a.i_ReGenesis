package dev.aurakai.auraframefx.domains.cascade.utils.cascade.trinity

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.domains.genesis.ai.clients.VertexAIClient
import dev.aurakai.auraframefx.domains.cascade.utils.context.ContextManager
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.GenesisBridgeService
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.AuraAIService
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.KaiAIService
import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
import dev.aurakai.auraframefx.domains.kai.security.SecurityContext
import dev.aurakai.auraframefx.domains.kai.security.SecurityMonitor
import javax.inject.Singleton

/**
 * Dependency Injection module for the Trinity AI system.
 *
 * Provides instances of:
 * - Genesis Bridge Service (Python backend connection)
 * - Trinity Coordinator Service (orchestrates all personas)
 * - Integration with existing Kai and Aura services
 */
@Module
@InstallIn(SingletonComponent::class)
object TrinityModule {
}

