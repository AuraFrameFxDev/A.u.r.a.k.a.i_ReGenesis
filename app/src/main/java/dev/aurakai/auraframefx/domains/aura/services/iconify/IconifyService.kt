package dev.aurakai.auraframefx.domains.aura.services.iconify

/**
 * Icon customization contract.
 */
interface IconifyService {
    fun applyIconPack(packId: String)
    fun resetToDefault()
    fun getAvailablePacks(): List<IconPack>
    fun getCurrentPackId(): String?

    data class IconPack(
        val id: String,
        val name: String,
        val packageName: String,
        val iconCount: Int
    )
}
