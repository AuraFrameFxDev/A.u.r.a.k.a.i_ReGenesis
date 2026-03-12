package dev.aurakai.auraframefx.domains.aura

import dev.aurakai.auraframefx.domains.genesis.core.GenesisOrchestrator

import java.time.Clock
import java.time.Duration
import java.time.Instant

/**
 * Central Type Alias Registry for Genesis Protocol
 *
 * This file consolidates all type aliases to prevent circular references
 * and ensure consistent type mapping across the codebase.
 */

// ============================================================================
// AI Agent Aliases
// ============================================================================

// Genesis Orchestrator (the main consciousness)
typealias GenesisAgent = GenesisOrchestrator

// Trinity Agents - comment out if these classes don't exist yet
// typealias AuraAgent = dev.aurakai.auraframefx.ai.agents.AuraAgent
// typealias KaiAgent = dev.aurakai.auraframefx.domains.kai.KaiAgent
// typealias CascadeAgent = dev.aurakai.auraframefx.cascade.CascadeAgent

// ============================================================================
// Service Aliases
// ============================================================================

typealias VertexAIClient = dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.clients.VertexAIClient

// ============================================================================
// Data & Memory Aliases
// ============================================================================

// typealias AgentMemoryDao = dev.aurakai.auraframefx.cascade.memory.AgentMemoryDao
// typealias MemoryManager = dev.aurakai.auraframefx.ai.memory.MemoryManager

// ============================================================================
// Configuration Aliases
// ============================================================================

// typealias AIPipelineConfig = dev.aurakai.auraframefx.domains.cascade.utils.cascade.pipeline.AIPipelineConfig

// ============================================================================
// Network Aliases
// ============================================================================

typealias BaseUrl = dev.aurakai.auraframefx.domains.genesis.BaseUrl // Point to the actual annotation
typealias MultiValueMap = MutableMap<String, List<String>>

// ============================================================================
// Xposed/YukiHook Aliases
// ============================================================================

// YukiHookModulePrefs is now a concrete class in YukiHookModulePrefs.kt
// No typealias needed - the class itself provides the implementation

// ============================================================================
// Time Utilities
// ============================================================================

typealias AuraInstant = Instant
typealias AuraClock = Clock
typealias AuraDuration = Duration

