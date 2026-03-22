package dev.aurakai.auraframefx.domains.genesis.network

/**
 * Typealias shim: Redirects to canonical VertexAIClient in oracledrive package
 * The oracledrive.genesis version is more complete with additional methods:
 * - initializeCreativeModels()
 * - analyzeImage()
 * - validateConnection()
 * - generateContent()
 */
typealias VertexAIClient = dev.aurakai.auraframefx.domains.genesis.ai.clients.VertexAIClient
typealias DefaultVertexAIClient = dev.aurakai.auraframefx.domains.genesis.ai.clients.DefaultVertexAIClient

