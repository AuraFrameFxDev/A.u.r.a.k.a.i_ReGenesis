package dev.aurakai.auraframefx.domains.aura.chromacore.iconify.iconify

import kotlinx.serialization.Serializable

/**
 * 🎨 ICONIFY MODELS
 */

@Serializable
data class IconifyApiCollection(
    val prefix: String? = null,
    val name: String,
    val total: Int,
    val author: IconAuthor? = null,
    val license: IconLicense? = null,
    val samples: List<String> = emptyList(),
    val height: Int? = null,
    val category: String? = null,
    val palette: Boolean = false
)

@Serializable
data class IconAuthor(
    val name: String,
    val url: String? = null
)

@Serializable
data class IconLicense(
    val title: String,
    val spdx: String? = null,
    val url: String? = null
)

@Serializable
data class IconSearchResult(
    val icons: List<String>,
    val total: Int,
    val limit: Int,
    val start: Int,
)

@Serializable
data class IconData(
    val body: String,
    val width: Int? = null,
    val height: Int? = null,
    val left: Int? = 0,
    val top: Int? = 0,
    val rotate: Int? = 0,
    val hFlip: Boolean? = false,
    val vFlip: Boolean? = false
)

@Serializable
data class IconSet(
    val prefix: String,
    val icons: Map<String, IconData>,
    val aliases: Map<String, String>? = null,
    val width: Int? = null,
    val height: Int? = null
)
