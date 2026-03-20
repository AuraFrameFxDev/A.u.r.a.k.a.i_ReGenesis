package dev.aurakai.auraframefx.ui.components.effects

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.aurakai.auraframefx.domains.aura.ui.components.effects.ShimmerParticles as DomainShimmerParticles

@Composable
fun ShimmerParticles(
    modifier: Modifier = Modifier,
    particleCount: Int = 100,
    baseColor: Color = Color.Cyan,
    secondaryColor: Color = Color.Magenta,
    shimmerIntensity: Float = 0.5f
) {
    DomainShimmerParticles(modifier, particleCount, baseColor, secondaryColor, shimmerIntensity)
}
