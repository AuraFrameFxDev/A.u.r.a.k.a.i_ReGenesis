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
 * @param columns
 * @param rows
 */
@Serializable
data class QuickSettingsConfigLayout(

    @SerialName(value = "columns")
    val columns: kotlin.Int? = null,

    @SerialName(value = "rows")
    val rows: kotlin.Int? = null,

    )
