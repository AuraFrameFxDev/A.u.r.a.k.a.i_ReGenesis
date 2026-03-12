package dev.aurakai.auraframefx.domains.aura.ui.components.unified

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import dev.aurakai.auraframefx.domains.aura.config.GateAssetConfig
import dev.aurakai.auraframefx.domains.aura.ui.components.*
import dev.aurakai.auraframefx.domains.aura.ui.components.backgrounds.*
import dev.aurakai.auraframefx.domains.aura.ui.theme.AgentDomain

/**
 * 🌌 UNIFIED BACKGROUND ENGINE
 * 
 * Handles the "Organism's Skin" - switching between:
 * 1. Animated Backgrounds (Starfield, Lava, etc.)
 * 2. Image Backgrounds (High-fidelity Gate Scenes)
 * 3. Gradient Backgrounds (Dynamic domain-based colors)
 */
/**
 * Renders a full-size background chosen from animated, image-based, or gradient themes according to the given agent domain and style flag.
 *
 * @param domain The agent domain whose themed background will be displayed.
 * @param useStyleB When true selects the domain's alternate (style B) background; when false selects the default style.
 * @param modifier Modifier applied to the root container.
 */
@Composable
fun UnifiedBackground(
    domain: AgentDomain,
    useStyleB: Boolean = false,
    modifier: Modifier = Modifier
) {
    LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 1. Logic to determine if we use Animation, Image, or Gradient
        // This is where the "Wiring" happens
        when (domain) {
            AgentDomain.AURA -> {
                if (useStyleB) {
                    // Style B: Clean Studio (Gradient + Ribbons)
                    DataRibbonsBackground(baseColor = domain.primaryColor)
                } else {
                    // Style A: Paint Splash Animation
                    PaintSplashBackground()
                }
            }

            AgentDomain.KAI -> {
                if (useStyleB) {
                    // Style B: Cyber Sentinel (Fortress Grid)
                    HexagonGridBackground(primaryColor = domain.primaryColor)
                } else {
                    // Style A: Icy Tundra Animation
                    IcyTundraBackground()
                }
            }

            AgentDomain.GENESIS -> {
                if (useStyleB) {
                    // Style B: Neural Matrix (Image-based)
                    BackgroundImage(GateAssetConfig.GenesisSubGates.NEURAL_ARCHIVE.styleB)
                } else {
                    // Style A: Lava Apocalypse Animation
                    LavaApocalypseBackground()
                }
            }

            AgentDomain.AGENT_NEXUS -> {
                if (useStyleB) {
                    // Style B: Control Room (Image-based)
                    BackgroundImage(GateAssetConfig.NexusSubGates.MONITORING.styleB)
                } else {
                    // Style A: Starfield Animation
                    StarfieldBackground()
                }
            }

            else -> {
                // Fallback: Data Ribbons
                DataRibbonsBackground(baseColor = domain.primaryColor)
            }
        }
    }
}

/**
 * Renders a full-size cropped Image from a drawable resource identified by name if the resource exists.
 *
 * Attempts to resolve `resourceName` as a drawable; if found, displays it filling the available space with `ContentScale.Crop`.
 *
 * @param resourceName The name of the drawable resource (without file extension) to resolve and display. If no matching resource is found, nothing is rendered.
 */
@Composable
fun BackgroundImage(resourceName: String) {
    val context = LocalContext.current
    val resId = context.resources.getIdentifier(resourceName, "drawable", context.packageName)
    if (resId != 0) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
