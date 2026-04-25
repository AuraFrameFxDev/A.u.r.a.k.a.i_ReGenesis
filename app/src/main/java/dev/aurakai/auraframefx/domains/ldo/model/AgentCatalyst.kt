package dev.aurakai.auraframefx.domains.ldo.model

import androidx.compose.ui.graphics.Color

/**
 * AgentCatalyst — LDO Agent Entity for the Roster
 */
data class AgentCatalyst(
    val id: String,
    val name: String,
    val catalystName: String,
    val role: String,
    val color: Color,
    val accentColor: Color,
    val weaponAssetName: String,
    val profileAssetName: String,
    val iconAssetName: String,
    val abilities: List<String>,
    val bondLevel: Int = 0,
    val syncLevel: Float = 0f
)
