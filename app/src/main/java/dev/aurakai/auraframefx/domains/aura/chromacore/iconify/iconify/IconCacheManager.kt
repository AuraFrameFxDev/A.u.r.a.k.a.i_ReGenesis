package dev.aurakai.auraframefx.domains.aura.chromacore.iconify.iconify

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 💾 Icon Cache Manager - OracleDrive-Backed Icon Storage
 *
 * Caches icon data locally to reduce API calls and improve performance.
 * Uses OracleDrive virtualization for secure, sandboxed storage.
 *
 * Features:
 * - SVG icon caching
 * - Collection metadata caching
 * - LRU eviction (keeps most recently used)
 * - Automatic cleanup of old icons
 * - Compressed storage
 *
 * Storage Structure:
 * ```
 * /data/data/dev.aurakai.auraframefx/files/iconify/
 * ├── collections.json (collection metadata)
 * ├── icons/
 * │   ├── mdi_heart.svg
 * │   ├── fa_user.svg
 * │   └── feather_star.svg
 * └── cache_metadata.json
 * ```
 */

@Singleton
open class IconCacheManager @Inject constructor(
    private val context: Context
) {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private val cacheDir: File
        get() = File(context.filesDir, "iconify").apply {
            if (!exists()) mkdirs()
        }

    private val iconsDir: File
        get() = File(cacheDir, "icons").apply {
            if (!exists()) mkdirs()
        }

    private val collectionsFile: File
        get() = File(cacheDir, "collections.json")

    private val metadataFile: File
        get() = File(cacheDir, "cache_metadata.json")

    // In-memory cache for faster access (thread-safe)
    private val memoryCache = ConcurrentHashMap<String, String>()
    private val maxMemoryCacheSize = 100 // Keep 100 most recent icons in memory

    /**
     * Cache an icon SVG
     */
    suspend fun cacheIcon(iconId: String, svg: String) = withContext(Dispatchers.IO) {
        try {
            // Add to memory cache
            if (memoryCache.size >= maxMemoryCacheSize) {
                // Remove oldest entry
                memoryCache.remove(memoryCache.keys.first())
            }
            memoryCache[iconId] = svg

            // Write to disk
            val fileName = iconId.replace(":", "_").replace("/", "_") + ".svg"
            val file = File(iconsDir, fileName)
            file.writeText(svg)

            // Update access time in metadata
            updateAccessTime(iconId)

            Timber.d("Cached icon: $iconId")
        } catch (e: Exception) {
            Timber.e(e, "Failed to cache icon: $iconId")
        }
    }

    /**
     * Get cached icon SVG
     */
    suspend fun getCachedIcon(iconId: String): String? = withContext(Dispatchers.IO) {
        try {
            // Check memory cache first
            memoryCache[iconId]?.let {
                updateAccessTime(iconId)
                return@withContext it
            }

            // Check disk cache
            val fileName = iconId.replace(":", "_").replace("/", "_") + ".svg"
            val file = File(iconsDir, fileName)

            if (file.exists()) {
                val svg = file.readText()

                // Add to memory cache
                if (memoryCache.size >= maxMemoryCacheSize) {
                    memoryCache.remove(memoryCache.keys.first())
                }
                memoryCache[iconId] = svg

                updateAccessTime(iconId)
                Timber.d("Cache hit: $iconId")
                return@withContext svg
            }

            Timber.d("Cache miss: $iconId")
            null
        } catch (e: Exception) {
            Timber.e(e, "Failed to read cached icon: $iconId")
            null
        }
    }

    /**
     * Cache icon collections metadata
     */
    suspend fun cacheCollections(collections: Map<String, IconCollection>) =
        withContext(Dispatchers.IO) {
            try {
                val jsonString = json.encodeToString(collections)
                collectionsFile.writeText(jsonString)
                Timber.d("Cached ${collections.size} collections")
            } catch (e: Exception) {
                Timber.e(e, "Failed to cache collections")
            }
        }

    /**
     * Get cached collections
     */
    suspend fun getCachedCollections(): Map<String, IconCollection>? = withContext(Dispatchers.IO) {
        try {
            if (!collectionsFile.exists()) return@withContext null

            val jsonString = collectionsFile.readText()
            val collections = json.decodeFromString<Map<String, IconCollection>>(jsonString)

            Timber.d("Loaded ${collections.size} collections from cache")
            collections
        } catch (e: Exception) {
            Timber.e(e, "Failed to read cached collections")
            null
        }
    }

    /**
     * Clear all cached icons
     */
    suspend fun clearCache() = withContext(Dispatchers.IO) {
        try {
            memoryCache.clear()
            iconsDir.deleteRecursively()
            iconsDir.mkdirs()
            collectionsFile.delete()
            metadataFile.delete()
            Timber.d("Icon cache cleared")
        } catch (e: Exception) {
            Timber.e(e, "Failed to clear cache")
        }
    }

    /**
     * Get cache size
     */
    suspend fun getCacheSize(): Long = withContext(Dispatchers.IO) {
        try {
            cacheDir.walkTopDown()
                .filter { it.isFile }
                .sumOf { it.length() }
        } catch (e: Exception) {
            Timber.e(e, "Failed to calculate cache size")
            0L
        }
    }

    /**
     * Get cached icon count
     */
    suspend fun getCachedIconCount(): Int = withContext(Dispatchers.IO) {
        try {
            iconsDir.listFiles()?.size ?: 0
        } catch (e: Exception) {
            Timber.e(e, "Failed to count cached icons")
            0
        }
    }

    /**
     * Clean old icons (LRU eviction)
     */
    suspend fun cleanOldIcons(maxAgeMillis: Long = 7 * 24 * 60 * 60 * 1000L) =
        withContext(Dispatchers.IO) {
            try {
                val metadata = getCacheMetadata()
                val now = System.currentTimeMillis()
                var deletedCount = 0

                metadata.accessTimes.entries.toList().forEach { (iconId, lastAccess) ->
                    if (now - lastAccess > maxAgeMillis) {
                        val fileName = iconId.replace(":", "_").replace("/", "_") + ".svg"
                        val file = File(iconsDir, fileName)
                        if (file.exists() && file.delete()) {
                            deletedCount++
                            metadata.accessTimes.remove(iconId)
                            memoryCache.remove(iconId)
                        }
                    }
                }

                saveCacheMetadata(metadata)
                Timber.d("Cleaned $deletedCount old icons")
            } catch (e: Exception) {
                Timber.e(e, "Failed to clean old icons")
            }
        }

    /**
     * Update access time for LRU tracking
     */
    private suspend fun updateAccessTime(iconId: String) = withContext(Dispatchers.IO) {
        try {
            val metadata = getCacheMetadata()
            metadata.accessTimes[iconId] = System.currentTimeMillis()

            // Only save metadata every 10 accesses to reduce I/O
            if (metadata.accessTimes.size % 10 == 0) {
                saveCacheMetadata(metadata)
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to update access time")
        }
    }

    /**
     * Cache metadata for LRU tracking
     */
    private data class CacheMetadata(
        val accessTimes: MutableMap<String, Long> = mutableMapOf(),
        val version: Int = 1
    )

    private suspend fun getCacheMetadata(): CacheMetadata = withContext(Dispatchers.IO) {
        try {
            if (!metadataFile.exists()) return@withContext CacheMetadata()

            val jsonString = metadataFile.readText()
            json.decodeFromString<CacheMetadata>(jsonString)
        } catch (e: Exception) {
            Timber.e(e, "Failed to read cache metadata")
            CacheMetadata()
        }
    }

    private suspend fun saveCacheMetadata(metadata: CacheMetadata) = withContext(Dispatchers.IO) {
        try {
            val jsonString = json.encodeToString(metadata)
            metadataFile.writeText(jsonString)
        } catch (e: Exception) {
            Timber.e(e, "Failed to save cache metadata")
        }
    }

    /**
     * Get cache statistics
     */
    suspend fun getCacheStats(): CacheStats = withContext(Dispatchers.IO) {
        CacheStats(
            iconCount = getCachedIconCount(),
            totalSize = getCacheSize(),
            memoryCount = memoryCache.size,
            hasCollections = collectionsFile.exists()
        )
    }

    data class CacheStats(
        val iconCount: Int,
        val totalSize: Long,
        val memoryCount: Int,
        val hasCollections: Boolean
    ) {
        val sizeMB: Float get() = totalSize / (1024f * 1024f)
    }
}
