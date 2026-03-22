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
 * @param start
 * @param top
 * @param end
 * @param bottom
 */
@Serializable
data class QuickSettingsConfigPadding(

    @SerialName(value = "start")
    val start: kotlin.Int = 0,

    @SerialName(value = "top")
    val top: kotlin.Int = 0,

    @SerialName(value = "end")
    val end: kotlin.Int = 0,

    @SerialName(value = "bottom")
    val bottom: kotlin.Int = 0,

    )
