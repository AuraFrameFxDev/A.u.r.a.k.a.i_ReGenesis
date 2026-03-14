package dev.aurakai.auraframefx.ai.agents

// This file provides a compile-time alias so other modules and KSP can refer to
// dev.aurakai.auraframefx.ai.agents.AuraAgent while the real implementation
// lives in dev.aurakai.auraframefx.aura.AuraAgent.

import dev.aurakai.auraframefx.aura.AuraAgent as AuraImpl

// Prefer a typealias so we don't duplicate annotations or DI bindings.
typealias AuraAgent = AuraImpl

