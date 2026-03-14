package dev.aurakai.auraframefx.oracledrive.genesis.ai.di

import dagger.Module
// import dagger.Provides // Not needed if Hilt can auto-inject concrete classes
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// import dev.aurakai.auraframefx.ai.services.AuraAIService // Not needed if no explicit provider
// import dev.aurakai.auraframefx.ai.services.CascadeAIService // Not needed if no explicit provider
// import dev.aurakai.auraframefx.ai.services.KaiAIService // Not needed if no explicit provider
// import dev.aurakai.auraframefx.security.SecurityContext // Not needed if KaiAIService provider is removed
// import javax.inject.Singleton // Not needed if no explicit provider

@Module
@InstallIn(SingletonComponent::class)
object AiServiceModule {
    // AuraAIService, KaiAIService, and CascadeAIService are concrete classes
    // with @Singleton and @Inject on their constructors.
    // Hilt can provide them automatically.
    // Explicit @Provides methods are removed to avoid issues with deleted Impl classes
    // and to rely on Hilt's direct injection capabilities.

    // If SecurityContext is needed by KaiAIService, it must be provided elsewhere
    // or KaiAIService's constructor needs to be such that Hilt can fulfill it.
}
