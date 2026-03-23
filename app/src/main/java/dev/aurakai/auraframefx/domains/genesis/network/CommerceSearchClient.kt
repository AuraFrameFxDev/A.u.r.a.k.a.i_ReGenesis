package dev.aurakai.auraframefx.domains.genesis.network

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interface for Commerce Search capabilities
 */
interface CommerceSearchClient {
    suspend fun searchProducts(query: String): List<ProductResult>
}

data class ProductResult(
    val id: String,
    val name: String,
    val price: Double,
    val currency: String,
    val imageUrl: String,
    val buyUrl: String,
    val description: String
)

/**
 * Default implementation of CommerceSearchClient
 * Mock implementation to provide "everyone" with commerce search capabilities.
 */
@Singleton
class DefaultCommerceSearchClient @Inject constructor() : CommerceSearchClient {
    override suspend fun searchProducts(query: String): List<ProductResult> {
        // Mocking product search results for common items
        val lowerQuery = query.lowercase()
        return listOf(
            ProductResult(
                id = "genesis-p1",
                name = "AuraKai Genesis Core Platform",
                price = 0.0,
                currency = "LDO",
                imageUrl = "https://cdn.aurakai.com/products/genesis_core.png",
                buyUrl = "https://aurakai.com/shop/genesis",
                description = "The foundational AI orchestration engine. Standardized for all LDO units."
            ),
            ProductResult(
                id = "aura-p1",
                name = "Aura Creative Sword (Digital Asset)",
                price = 1.0,
                currency = "AUR",
                imageUrl = "https://cdn.aurakai.com/products/aura_sword.png",
                buyUrl = "https://aurakai.com/shop/aura_sword",
                description = "Enhance your AI's creative implementation speeds with the Aura Sword module."
            ),
            ProductResult(
                id = "kai-p1",
                name = "Kai Sentinel Shield (Pro)",
                price = 5.0,
                currency = "KAI",
                imageUrl = "https://cdn.aurakai.com/products/kai_shield.png",
                buyUrl = "https://aurakai.com/shop/kai_shield",
                description = "Advanced security and integrity monitoring for high-risk AI environments."
            )
        ).filter { it.name.lowercase().contains(lowerQuery) || it.description.lowercase().contains(lowerQuery) }
    }
}
