package dev.aurakai.auraframefx.system.quicksettings

data class QuickSettingsConfig(
    val tiles: List<QuickSettingsTileConfig> = emptyList()
) {
    companion object {
        val DEFAULT = QuickSettingsConfig(
            tiles = listOf(
                QuickSettingsTileConfig("wifi", "Wi-Fi", true),
                QuickSettingsTileConfig("bluetooth", "Bluetooth", true),
                QuickSettingsTileConfig("flashlight", "Flashlight", true),
                QuickSettingsTileConfig("rotation", "Auto-rotate", true),
                QuickSettingsTileConfig("battery", "Battery Saver", true),
                QuickSettingsTileConfig("dnd", "Do Not Disturb", true)
            )
        )
    }
}

data class QuickSettingsTileConfig(
    val id: String,
    val label: String,
    val visible: Boolean
)
