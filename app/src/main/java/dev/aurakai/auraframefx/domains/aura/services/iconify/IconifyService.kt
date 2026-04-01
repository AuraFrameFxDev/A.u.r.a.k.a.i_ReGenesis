package dev.aurakai.auraframefx.domains.aura.services.iconify

/**
 * Icon customization contract.
 */
interface IconifyService {
    fun applyIconPack(packId: String)
    fun resetToDefault()
    fun getAvailablePacks(): List<IconPack>
    fun getCurrentPackId(): String?
    suspend fun searchIcons(query: String, limit: Int = 100): Result<IconSearchResult>
    suspend fun getCollections(): Result<List<IconCollection>>
    suspend fun getIconSvg(iconId: String): Result<String>           // e.g. "mdi:heart"
    suspend fun getIconsBatch(iconIds: List<String>): Result<Map<String, String>>
    suspend fun getPopularIcons(collection: String, limit: Int = 24): Result<List<String>>

    data class IconPack(
        val id: String,
        val name: String,
        val packageName: String,
        val iconCount: Int
    )

    data class IconSearchResult(
        val icons: List<String>,
        val query: String
    )

    data class IconCollection(
        val id: String,
        val name: String,
        val totalIcons: Int
    )
}
