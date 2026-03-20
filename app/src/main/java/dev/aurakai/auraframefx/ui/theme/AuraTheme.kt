package dev.aurakai.auraframefx.ui.theme

import androidx.compose.ui.graphics.Color

interface AuraTheme {
    val name: String
    val primaryColor: Color
    val accentColor: Color
    val animationStyle: AnimationStyle

    enum class AnimationStyle {
        ENERGETIC, CALMING, FLOWING, PULSING, SUBTLE
    }
}

object CyberpunkTheme : AuraTheme {
    override val name: String = "Cyberpunk"
    override val primaryColor: Color = Color(0xFF00FF9F)
    override val accentColor: Color = Color(0xFFBC13FE)
    override val animationStyle: AuraTheme.AnimationStyle = AuraTheme.AnimationStyle.ENERGETIC
}

object SolarFlareTheme : AuraTheme {
    override val name: String = "Solar Flare"
    override val primaryColor: Color = Color(0xFFFF5722)
    override val accentColor: Color = Color(0xFFFFC107)
    override val animationStyle: AuraTheme.AnimationStyle = AuraTheme.AnimationStyle.PULSING
}

object ForestTheme : AuraTheme {
    override val name: String = "Forest"
    override val primaryColor: Color = Color(0xFF4CAF50)
    override val accentColor: Color = Color(0xFF8BC34A)
    override val animationStyle: AuraTheme.AnimationStyle = AuraTheme.AnimationStyle.CALMING
}
