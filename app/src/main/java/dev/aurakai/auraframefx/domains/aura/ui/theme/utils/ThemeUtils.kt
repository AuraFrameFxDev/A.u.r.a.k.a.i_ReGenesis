package dev.aurakai.auraframefx.domains.aura.ui.theme.utils

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration

/**
 * Utility functions for handling theme-related operations.
 */
object ThemeUtils {

    /**
     * Check if the system is currently in dark theme mode.
     */
    @Composable
    fun isDarkTheme(): Boolean {
        val configuration = LocalConfiguration.current
        return when (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            else -> isSystemInDarkTheme()
        }
    }

    /**
     * Get the appropriate surface color based on the current theme.
     */
    @Composable
    fun getSurfaceColor(): Color {
        return if (isDarkTheme()) {
            MaterialTheme.colorScheme.surface
        } else {
            MaterialTheme.colorScheme.surface
        }
    }

    /**
     * Get the appropriate onSurface color based on the current theme.
     */
    @Composable
    fun getOnSurfaceColor(): Color {
        return if (isDarkTheme()) {
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.onSurface
        }
    }

    /**
     * Get the appropriate primary color based on the current theme.
     */
    @Composable
    fun getPrimaryColor(): Color {
        return MaterialTheme.colorScheme.primary
    }

    /**
     * Get the appropriate secondary color based on the current theme.
     */
    @Composable
    fun getSecondaryColor(): Color {
        return MaterialTheme.colorScheme.secondary
    }

    /**
     * Get the appropriate error color based on the current theme.
     */
    @Composable
    fun getErrorColor(): Color {
        return MaterialTheme.colorScheme.error
    }

    /**
     * Get the appropriate background color based on the current theme.
     */
    @Composable
    fun getBackgroundColor(): Color {
        return if (isDarkTheme()) {
            MaterialTheme.colorScheme.background
        } else {
            MaterialTheme.colorScheme.background
        }
    }

    /**
     * Get the appropriate color for text on primary surface.
     */
    @Composable
    fun getOnPrimaryColor(): Color {
        return MaterialTheme.colorScheme.onPrimary
    }

    /**
     * Get the appropriate color for text on secondary surface.
     */
    @Composable
    fun getOnSecondaryColor(): Color {
        return MaterialTheme.colorScheme.onSecondary
    }

    /**
     * Get the appropriate color for text on error surface.
     */
    @Composable
    fun getOnErrorColor(): Color {
        return MaterialTheme.colorScheme.onError
    }

    /**
     * Get the appropriate color for text on background.
     */
    @Composable
    fun getOnBackgroundColor(): Color {
        return MaterialTheme.colorScheme.onBackground
    }

    /**
     * Get the appropriate color for surface variant.
     */
    @Composable
    fun getSurfaceVariantColor(): Color {
        return MaterialTheme.colorScheme.surfaceVariant
    }

    /**
     * Get the appropriate color for text on surface variant.
     */
    @Composable
    fun getOnSurfaceVariantColor(): Color {
        return MaterialTheme.colorScheme.onSurfaceVariant
    }

    /**
     * Get the appropriate color for outline.
     */
    @Composable
    fun getOutlineColor(): Color {
        return MaterialTheme.colorScheme.outline
    }

    /**
     * Get the appropriate color for outline variant.
     */
    @Composable
    fun getOutlineVariantColor(): Color {
        return MaterialTheme.colorScheme.outlineVariant
    }
}

