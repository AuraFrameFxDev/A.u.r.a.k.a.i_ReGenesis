package dev.aurakai.auraframefx.domains.aura.chromacore.iconify.iconify

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 🎨 Iconify Service - 250,000+ Icon API Integration
 *
 * Fetches icons from Iconify's public API:
 * - API: https://api.iconify.design
 * - Collections: https://icon-sets.iconify.design
 * - 200+ icon sets including Material, FontAwesome, Feather, etc.
 *
 * Features:
 * - Search icons by keyword
 * - Fetch icon collections
 * - Get SVG icon data
 * - Cache with OracleDrive
 * - 250,000+ icons available
 *
 * Example:
 * ```
 * val service = IconifyService(okHttpClient, iconCacheManager)
 * val icons = service.searchIcons("heart")
 * val svg = service.getIconSvg("mdi:heart")
 * ```
 */

/**
 * Icon collection metadata
 */
@Serializable
data class IconCollection(
    val prefix: String,
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

/**
 * Icon search result
 */
@Serializable
data class IconSearchResult(
    val icons: List<String>,
    val total: Int,
    val limit: Int,
    val start: Int,
    val collections: Map<String, IconCollection>? = null
)

/**
 * Icon data (SVG path, dimensions)
 */
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

/**
 * Full icon set response
 */
@Serializable
data class IconSetResponse(
    val prefix: String,
    val icons: Map<String, IconData>,
    val aliases: Map<String, IconData>? = null,
    val width: Int? = null,
    val height: Int? = null
)

/**
 * Iconify API Service
 */
@Singleton
class IconifyService @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val iconCacheManager: IconCacheManager
) {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    private val baseUrl = "https://api.iconify.design"
    private val collectionUrl = "https://api.iconify.design/collections"

    /**
     * Get all available icon collections
     */
    suspend fun getCollections(): Result<Map<String, IconCollection>> =
        withContext(Dispatchers.IO) {
            try {
                // Check cache first
                iconCacheManager.getCachedCollections()?.let {
                    return@withContext Result.success(it)
                }

                val request = Request.Builder()
                    .url(collectionUrl)
                    .get()
                    .build()

                val response = okHttpClient.newCall(request).execute()

                if (!response.isSuccessful) {
                    return@withContext Result.failure(IOException("Failed to fetch collections: ${response.code}"))
                }

                val body = response.body?.string() ?: return@withContext Result.failure(
                    IOException(
                        "Empty response"
                    )
                )
                val collections = json.decodeFromString<Map<String, IconCollection>>(body)

                // Cache collections
                iconCacheManager.cacheCollections(collections)

                Result.success(collections)
            } catch (e: Exception) {
                Timber.e(e, "Failed to fetch collections")
                Result.failure(e)
            }
        }

    /**
     * Search icons by keyword
     */
    suspend fun searchIcons(
        query: String,
        limit: Int = 64,
        start: Int = 0,
        prefixes: String? = null // Comma-separated collection prefixes (e.g., "mdi,fa")
    ): Result<IconSearchResult> = withContext(Dispatchers.IO) {
        try {
            val url = buildString {
                append("$baseUrl/search?query=$query")
                append("&limit=$limit")
                append("&start=$start")
                if (prefixes != null) {
                    append("&prefixes=$prefixes")
                }
            }

            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (!response.isSuccessful) {
                return@withContext Result.failure(IOException("Search failed: ${response.code}"))
            }

            val body = response.body?.string()
                ?: return@withContext Result.failure(IOException("Empty response"))
            val result = json.decodeFromString<IconSearchResult>(body)

            Result.success(result)
        } catch (e: Exception) {
            Timber.e(e, "Icon search failed for query: $query")
            Result.failure(e)
        }
    }

    /**
     * Get icon SVG data
     * @param iconId Format: "prefix:name" (e.g., "mdi:heart", "fa:user")
     */
    suspend fun getIconSvg(iconId: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            // Check cache first
            iconCacheManager.getCachedIcon(iconId)?.let {
                return@withContext Result.success(it)
            }

            val url = "$baseUrl/$iconId.svg"

            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (!response.isSuccessful) {
                return@withContext Result.failure(IOException("Failed to fetch icon: ${response.code}"))
            }

            val svg = response.body?.string()
                ?: return@withContext Result.failure(IOException("Empty SVG"))

            // Cache SVG
            iconCacheManager.cacheIcon(iconId, svg)

            Result.success(svg)
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch icon: $iconId")
            Result.failure(e)
        }
    }

    /**
     * Get multiple icons in one request (more efficient)
     * @param iconIds List of icon IDs (e.g., ["mdi:heart", "fa:user"])
     */
    suspend fun getIconsBatch(iconIds: List<String>): Result<Map<String, String>> =
        withContext(Dispatchers.IO) {
            try {
                val results = mutableMapOf<String, String>()

                // Group by prefix for efficient batch fetching
                val grouped = iconIds.groupBy { it.split(":").firstOrNull() ?: "" }

                grouped.forEach { (prefix, ids) ->
                    val icons = ids.mapNotNull { it.split(":").getOrNull(1) }
                    val url = "$baseUrl/$prefix.json?icons=${icons.joinToString(",")}"

                    val request = Request.Builder()
                        .url(url)
                        .get()
                        .build()

                    val response = okHttpClient.newCall(request).execute()

                    if (response.isSuccessful) {
                        val body = response.body?.string()
                        if (body != null) {
                            val iconSet = json.decodeFromString<IconSetResponse>(body)

                            iconSet.icons.forEach { (name, data) ->
                                val fullId = "$prefix:$name"
                                val svg = buildSvgFromIconData(data, iconSet.width, iconSet.height)
                                results[fullId] = svg

                                // Cache each icon
                                iconCacheManager.cacheIcon(fullId, svg)
                            }
                        }
                    }
                }

                Result.success(results)
            } catch (e: Exception) {
                Timber.e(e, "Batch icon fetch failed")
                Result.failure(e)
            }
        }

    /**
     * Get popular icons from a collection
     */
    suspend fun getPopularIcons(
        prefix: String,
        limit: Int = 24
    ): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            val collections = getCollections().getOrNull() ?: return@withContext Result.failure(
                IOException("No collections")
            )
            val collection = collections[prefix]
                ?: return@withContext Result.failure(IOException("Collection not found"))

            // Return sample icons from collection metadata
            val samples = collection.samples.take(limit)
            Result.success(samples.map { "$prefix:$it" })
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch popular icons for $prefix")
            Result.failure(e)
        }
    }

    /**
     * Build SVG string from icon data
     */
    private fun buildSvgFromIconData(
        data: IconData,
        defaultWidth: Int?,
        defaultHeight: Int?
    ): String {
        val width = data.width ?: defaultWidth ?: 24
        val height = data.height ?: defaultHeight ?: 24

        val transform = buildString {
            if (data.hFlip == true) append("scale(-1 1) ")
            if (data.vFlip == true) append("scale(1 -1) ")
            if (data.rotate != null && data.rotate != 0) append("rotate(${data.rotate}) ")
        }.trim()

        return buildString {
            append("<svg xmlns=\"http://www.w3.org/2000/svg\" ")
            append("width=\"$width\" ")
            append("height=\"$height\" ")
            append("viewBox=\"${data.left} ${data.top} $width $height\">")
            if (transform.isNotEmpty()) {
                append("<g transform=\"$transform\">")
            }
            append(data.body)
            if (transform.isNotEmpty()) {
                append("</g>")
            }
            append("</svg>")
        }
    }

    /**
     * Get recommended icon collections for quick access
     */
    fun getRecommendedCollections(): List<String> = listOf(
        "mdi", // Material Design Icons - 7000+ icons
        "fa", // Font Awesome - 2000+ icons
        "feather", // Feather - 287 minimalist icons
        "ion", // Ionicons - 1300+ icons
        "bi", // Bootstrap Icons - 1800+ icons
        "heroicons", // Heroicons - 450+ icons
        "lucide", // Lucide - 1000+ icons
        "tabler", // Tabler Icons - 4000+ icons
        "carbon", // Carbon Icons - 2000+ icons
        "fluent", // Fluent UI - 1900+ icons
        "ic", // Google Material Icons - 2000+ icons
        "pepicons", // Pepicons - 400+ playful icons
        "solar", // Solar Icons - 1000+ bold icons
        "simple-icons" // Brand Icons - 3000+ brand logos
    )
}
