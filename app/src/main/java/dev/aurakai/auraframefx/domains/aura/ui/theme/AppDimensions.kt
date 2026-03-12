package dev.aurakai.auraframefx.domains.aura.ui.theme

import dev.aurakai.auraframefx.domains.aura.ui.AppDimensions as RootAppDimensions

/**
 * Compatibility shim - re-export AppDimensions from dev.aurakai.auraframefx.ui
 * Some files import dev.aurakai.auraframefx.domains.aura.ui.theme.AppDimensions; keep that API.
 */
object AppDimensions {
    val spacing_xs = RootAppDimensions.spacing_xs
    val spacing_small = RootAppDimensions.spacing_small
    val spacing_medium = RootAppDimensions.spacing_medium
    val spacing_large = RootAppDimensions.spacing_large
    val spacing_xl = RootAppDimensions.spacing_xl
    val spacing_xxl = RootAppDimensions.spacing_xxl

    val button_height = RootAppDimensions.button_height
    val button_min_width = RootAppDimensions.button_min_width
    val icon_size_small = RootAppDimensions.icon_size_small
    val icon_size_medium = RootAppDimensions.icon_size_medium
    val icon_size_large = RootAppDimensions.icon_size_large

    val text_size_xs = RootAppDimensions.text_size_xs
    val text_size_small = RootAppDimensions.text_size_small
    val text_size_medium = RootAppDimensions.text_size_medium
    val text_size_large = RootAppDimensions.text_size_large
    val text_size_xl = RootAppDimensions.text_size_xl
    val text_size_xxl = RootAppDimensions.text_size_xxl

    val corner_radius_small = RootAppDimensions.corner_radius_small
    val corner_radius_medium = RootAppDimensions.corner_radius_medium
    val corner_radius_large = RootAppDimensions.corner_radius_large
    val corner_radius_xl = RootAppDimensions.corner_radius_xl
    val corner_radius_round = RootAppDimensions.corner_radius_round

    val elevation_small = RootAppDimensions.elevation_small
    val elevation_medium = RootAppDimensions.elevation_medium
    val elevation_large = RootAppDimensions.elevation_large

    val stroke_small = RootAppDimensions.stroke_small
    val stroke_medium = RootAppDimensions.stroke_medium
    val stroke_large = RootAppDimensions.stroke_large

    val card_min_height = RootAppDimensions.card_min_height
    val card_padding = RootAppDimensions.card_padding
}

