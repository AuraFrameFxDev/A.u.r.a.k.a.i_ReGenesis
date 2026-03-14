package dev.aurakai.auraframefx.ai.clients

/**
 * Typealias shim: Redirects to canonical VertexAIClient in oracledrive package
 * The oracledrive.genesis version is more complete with additional methods:
 * - initializeCreativeModels()
 * - analyzeImage()
 * - validateConnection()
 * - generateContent()
 */
typealias VertexAIClient = dev.aurakai.auraframefx.oracledrive.genesis.ai.clients.VertexAIClient
typealias DefaultVertexAIClient = dev.aurakai.auraframefx.oracledrive.genesis.ai.clients.DefaultVertexAIClient
