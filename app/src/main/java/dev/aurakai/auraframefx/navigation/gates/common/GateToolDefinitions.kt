package dev.aurakai.auraframefx.navigation.gates.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import dev.aurakai.auraframefx.navigation.ReGenesisNavHost

// Data classes for Domain Hub Tool Cards

data class GenesisToolCard(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val destination: ReGenesisNavHost? = null,
    val accentColor: Color,
    val isWired: Boolean = true
)

data class SentinelToolCard(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val destination: ReGenesisNavHost? = null,
    val accentColor: Color,
    val isWired: Boolean = true
)

data class ThemingToolCard(
    val title: String,
    val subtitle: String,
    val destination: ReGenesisNavHost? = null,
    val accentColor: Color,
    val isWired: Boolean = true
)

data class NexusToolCard(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val accentColor: Color,
    val isWired: Boolean = true,
    val destination: ReGenesisNavHost
)

