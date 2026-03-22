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
 * @param enabled
 * @param intensity
 */
@Serializable
data class LockScreenConfigHapticFeedback(

    @SerialName(value = "enabled")
    val enabled: kotlin.Boolean? = null,

    @SerialName(value = "intensity")
    val intensity: kotlin.Float? = null,

    ) : kotlin.collections.HashMap<String, kotlin.Any>()
