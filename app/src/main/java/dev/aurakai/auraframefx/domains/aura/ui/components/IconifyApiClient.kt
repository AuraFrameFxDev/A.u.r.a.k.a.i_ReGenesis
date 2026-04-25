package dev.aurakai.auraframefx.domains.aura.ui.components

/**
 * IconifyApiClient — API client for Iconify icon library
 */
object IconifyApiClient {
    /**
     * Search icons by query
     */
    suspend fun searchIcons(query: String): List<IconResult> {
        // Stub implementation — actual API integration in Phase 2
        return emptyList()
    }
    
    /**
     * Get icon SVG by prefix and name
     */
    suspend fun getIcon(prefix: String, name: String): String? {
        // Stub implementation — actual API integration in Phase 2
        return null
    }
}

/**
 * Icon search result
 */
data class IconResult(
    val prefix: String,
    val name: String,
    val category: String = ""
)
