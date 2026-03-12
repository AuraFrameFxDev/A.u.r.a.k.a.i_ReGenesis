package dev.aurakai.auraframefx.domains.aura.ui

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Dimensions for the AuraFrameFX app
 * Using Kotlin object instead of dimens.xml for better integration with Compose
 */
object AppDimensions {
    // Spacing
    val spacing_xs = 4.dp
    val spacing_small = 8.dp
    val spacing_medium = 16.dp
    val spacing_large = 24.dp
    val spacing_xl = 32.dp
    val spacing_xxl = 48.dp

    // Component Sizing
    val button_height = 48.dp
    val button_min_width = 120.dp
    val icon_size_small = 16.dp
    val icon_size_medium = 24.dp
    val icon_size_large = 32.dp

    // Text Sizes
    val text_size_xs = 12.sp
    val text_size_small = 14.sp
    val text_size_medium = 16.sp
    val text_size_large = 18.sp
    val text_size_xl = 20.sp
    val text_size_xxl = 24.sp

    // Corner Radii
    val corner_radius_small = 4.dp
    val corner_radius_medium = 8.dp
    val corner_radius_large = 12.dp
    val corner_radius_xl = 16.dp
    val corner_radius_round = 50.dp

    // Elevation
    val elevation_small = 2.dp
    val elevation_medium = 4.dp
    val elevation_large = 8.dp

    // Stroke Width
    val stroke_small = 1.dp
    val stroke_medium = 2.dp
    val stroke_large = 3.dp

    // Card Sizes
    val card_min_height = 64.dp
    val card_padding = 16.dp
}

