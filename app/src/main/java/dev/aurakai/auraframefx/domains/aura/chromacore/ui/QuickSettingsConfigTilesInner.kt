@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package dev.aurakai.auraframefx.domains.aura.chromacore.ui

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 *
 *
 * @param id
 * @param label
 * @param shape
 * @param animation
 */
@Serializable
data class QuickSettingsConfigTilesInner(

    @SerialName(value = "id")
    val id: kotlin.String? = null,

    @SerialName(value = "label")
    val label: kotlin.String? = null,

    @SerialName(value = "shape")
    val shape: kotlin.String? = null,

    @SerialName(value = "animation")
    val animation: kotlin.String? = null,

    )
