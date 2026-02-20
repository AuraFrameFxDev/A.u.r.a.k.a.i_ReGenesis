package dev.aurakai.auraframefx.domains.cascade.utils.cascade

/**
 * Type aliases bridging cascade package imports to the canonical Trinity state models.
 *
 * VisionState and ProcessingState are defined once in agents.trinity and aliased here
 * so any file importing from either package resolves to the exact same type — eliminating
 * the "Argument type mismatch" compiler errors caused by duplicate class definitions.
 *
 * Source of truth: dev.aurakai.auraframefx.agents.trinity
 */
typealias VisionState = dev.aurakai.auraframefx.agents.trinity.VisionState
typealias ProcessingState = dev.aurakai.auraframefx.agents.trinity.ProcessingState
