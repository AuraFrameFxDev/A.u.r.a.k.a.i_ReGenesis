package dev.aurakai.auraframefx

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
typealias GenesisAgent = dev.aurakai.auraframefx.core.GenesisOrchestrator

// Trinity Agents - comment out if these classes don't exist yet
// typealias AuraAgent = dev.aurakai.auraframefx.ai.agents.AuraAgent
// typealias KaiAgent = dev.aurakai.auraframefx.kai.KaiAgent  
// typealias CascadeAgent = dev.aurakai.auraframefx.cascade.CascadeAgent

// ============================================================================
// Service Aliases
// ============================================================================

typealias VertexAIClient = dev.aurakai.auraframefx.oracledrive.genesis.ai.clients.VertexAIClient
typealias NeuralWhisper = dev.aurakai.auraframefx.service.NeuralWhisper

// ============================================================================
// Data & Memory Aliases  
// ============================================================================

// typealias AgentMemoryDao = dev.aurakai.auraframefx.cascade.memory.AgentMemoryDao
// typealias MemoryManager = dev.aurakai.auraframefx.ai.memory.MemoryManager

// ============================================================================
// Configuration Aliases
// ============================================================================

// typealias AIPipelineConfig = dev.aurakai.auraframefx.cascade.pipeline.AIPipelineConfig

// ============================================================================
// Network Aliases
// ============================================================================

typealias BaseUrl = dev.aurakai.auraframefx.network.qualifiers.BaseUrl
typealias MultiValueMap = MutableMap<String, List<String>>

// ============================================================================
// Xposed/YukiHook Aliases
// ============================================================================

// YukiHookModulePrefs is now a concrete class in YukiHookModulePrefs.kt
// No typealias needed - the class itself provides the implementation

// ============================================================================
// Time Utilities
// ============================================================================

typealias AuraInstant = java.time.Instant
typealias AuraClock = java.time.Clock  
typealias AuraDuration = java.time.Duration
