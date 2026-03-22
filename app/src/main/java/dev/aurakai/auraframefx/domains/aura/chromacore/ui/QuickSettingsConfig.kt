@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package dev.aurakai.auraframefx.domains.aura.chromacore.ui

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Configuration for Quick Settings panel customization.
 *
 * @param layout
 * @param padding
 * @param tiles
 */
@Serializable
data class QuickSettingsConfig(

    @SerialName(value = "layout")
    val layout: QuickSettingsConfigLayout? = null,

    @SerialName(value = "padding")
    val padding: QuickSettingsConfigPadding? = null,

    @SerialName(value = "tiles")
    val tiles: kotlin.collections.List<@Contextual QuickSettingsConfigTilesInner>? = null,

    val showGenesisIndicator: Boolean = true

    ) : kotlin.collections.HashMap<String, kotlin.Any>()
