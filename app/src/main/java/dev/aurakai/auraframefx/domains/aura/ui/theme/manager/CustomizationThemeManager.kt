package dev.aurakai.auraframefx.domains.aura.ui.theme.manager

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import dev.aurakai.auraframefx.domains.aura.lab.CustomizationPreferences

object CustomizationThemeManager {
    data class ThemeState(val dark: Boolean, val name: String, val accent: String)

    @Composable
    fun auraThemeState(context: Context): State<ThemeState> {
        val dark = CustomizationPreferences.themeDarkFlow(context).collectAsState(initial = true)
        val name =
            CustomizationPreferences.themeNameFlow(context).collectAsState(initial = "CyberGlow")
        val accent =
            CustomizationPreferences.themeAccentFlow(context).collectAsState(initial = "NeonBlue")
        return remember(dark.value, name.value, accent.value) {
            object : State<ThemeState> {
                override val value: ThemeState = ThemeState(dark.value, name.value, accent.value)
            }
        }
    }
}

