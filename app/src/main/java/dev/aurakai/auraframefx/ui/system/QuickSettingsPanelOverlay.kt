package dev.aurakai.auraframefx.ui.system

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.rememberAsyncImagePainter
import dev.aurakai.auraframefx.domains.aura.lab.CustomizationPreferences

// Placeholder for BlendMode mapping from String
fun String.toBlendMode(): BlendMode {
    return when (this) {
        "Clear" -> BlendMode.Clear
        "Src" -> BlendMode.Src
        "Dst" -> BlendMode.Dst
        "SrcOver" -> BlendMode.SrcOver
        "DstOver" -> BlendMode.DstOver
        "SrcIn" -> BlendMode.SrcIn
        "DstIn" -> BlendMode.DstIn
        "SrcOut" -> BlendMode.SrcOut
        "DstOut" -> BlendMode.DstOut
        "SrcAtop" -> BlendMode.SrcAtop
        "DstAtop" -> BlendMode.DstAtop
        "Xor" -> BlendMode.Xor
        "Multiply" -> BlendMode.Multiply
        "Screen" -> BlendMode.Screen
        "Overlay" -> BlendMode.Overlay
        "Darken" -> BlendMode.Darken
        "Lighten" -> BlendMode.Lighten
        "Add" -> BlendMode.Plus
        "Exclusion" -> BlendMode.Exclusion
        "ColorDodge" -> BlendMode.ColorDodge
        "ColorBurn" -> BlendMode.ColorBurn
        "Hardlight" -> BlendMode.Hardlight
        "Softlight" -> BlendMode.Softlight
        "Difference" -> BlendMode.Difference
        "Modulate" -> BlendMode.Modulate
        "Saturation" -> BlendMode.Saturation
        "Color" -> BlendMode.Color
        "Luminosity" -> BlendMode.Luminosity
        else -> BlendMode.SrcOver // Default or fallback
    }
}

@Composable
fun QuickSettingsPanelOverlay(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    var customQsBackgroundEnabled by remember { mutableStateOf(false) }
    var customQsBackgroundUri by remember { mutableStateOf<Uri?>(null) }
    var customQsBackgroundOpacity by remember { mutableStateOf(1.0f) }
    var customQsBackgroundBlendMode by remember { mutableStateOf("SrcOver") }

    // Load preferences on launch and react to changes (in a real app, this would use StateFlow or similar)
    LaunchedEffect(Unit) {
        customQsBackgroundEnabled = CustomizationPreferences.getCustomQsBackgroundEnabled(context)
        customQsBackgroundUri = CustomizationPreferences.getCustomQsBackgroundUri(context)
        customQsBackgroundOpacity = CustomizationPreferences.getCustomQsBackgroundOpacity(context)
        customQsBackgroundBlendMode =
            CustomizationPreferences.getCustomQsBackgroundBlendMode(context)
    }

    if (customQsBackgroundEnabled && customQsBackgroundUri != null) {
        val painter = rememberAsyncImagePainter(customQsBackgroundUri)
        Box(
            modifier = modifier
                .fillMaxSize()
                .graphicsLayer(alpha = customQsBackgroundOpacity) // Apply opacity
        ) {
            Image(
                painter = painter,
                contentDescription = "Custom Quick Settings Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop, // Or other ContentScale options
                colorFilter = ColorFilter.tint(
                    Color.Unspecified,
                    customQsBackgroundBlendMode.toBlendMode()
                ) // Apply blend mode
            )
        }
    } else if (customQsBackgroundEnabled) {
        // Fallback for enabled but no URI, or apply a default color with opacity
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Gray.copy(alpha = customQsBackgroundOpacity))
        )
    }

    // Placeholder for actual system integration logic:
    // In a real Android system, this Composable would not directly "overlay" the Quick Settings panel.
    // Instead, a system service (e.g., using a WindowManager overlay permission) or an Xposed/LSPosed hook
    // would be used to inject this UI into the system's Quick Settings view hierarchy.
    // This Composable represents the visual logic that would be rendered by such an injection mechanism.
}
