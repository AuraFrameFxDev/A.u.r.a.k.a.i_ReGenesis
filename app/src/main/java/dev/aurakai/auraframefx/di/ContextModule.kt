package dev.aurakai.auraframefx.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Context module - dependencies are now provided by AgentModule
 * This module is kept for potential future context-specific bindings
 */
@Module
@InstallIn(SingletonComponent::class)
object ContextModule {
    // ContextManager is now provided by AgentModule to avoid duplicate bindings
}
