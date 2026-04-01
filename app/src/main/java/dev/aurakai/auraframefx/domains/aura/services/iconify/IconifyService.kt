package dev.aurakai.auraframefx.domains.aura.services.iconify

/**
 * Icon customization contract.
 */
interface IconifyService {
    fun applyIconPack(packId: String)
    fun resetToDefault()
    fun getAvailablePacks(): List<IconPack>
    fun getCurrentPackId(): String?
    suspend fun searchIcons(query: String, limit: Int): Result<IconSearchResult>

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
}
