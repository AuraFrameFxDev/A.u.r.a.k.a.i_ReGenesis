package dev.aurakai.auraframefx.domains.aura.chromacore.iconify.iconify

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import timber.log.Timber
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 💾 Icon Cache Manager - OracleDrive-Backed Icon Storage
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

    private val memoryCache = ConcurrentHashMap<String, String>()
    private val maxMemoryCacheSize = 100 

    suspend fun cacheIcon(iconId: String, svg: String) = withContext(Dispatchers.IO) {
        try {
            if (memoryCache.size >= maxMemoryCacheSize) {
                memoryCache.remove(memoryCache.keys.first())
            }
            memoryCache[iconId] = svg

            val fileName = iconId.replace(":", "_").replace("/", "_") + ".svg"
            val file = File(iconsDir, fileName)
            file.writeText(svg)

            updateAccessTime(iconId)
        } catch (e: Exception) {
            Timber.e(e, "Failed to cache icon: $iconId")
        }
    }

    suspend fun getCachedIcon(iconId: String): String? = withContext(Dispatchers.IO) {
        try {
            memoryCache[iconId]?.let {
                updateAccessTime(iconId)
                return@withContext it
            }

            val fileName = iconId.replace(":", "_").replace("/", "_") + ".svg"
            val file = File(iconsDir, fileName)

            if (file.exists()) {
                val svg = file.readText()
                if (memoryCache.size >= maxMemoryCacheSize) {
                    memoryCache.remove(memoryCache.keys.first())
                }
                memoryCache[iconId] = svg
                updateAccessTime(iconId)
                return@withContext svg
            }
            null
        } catch (e: Exception) {
            Timber.e(e, "Failed to read cached icon: $iconId")
            null
        }
    }

    suspend fun cacheCollections(collections: Map<String, IconifyApiCollection>) = withContext(Dispatchers.IO) {
        try {
            val jsonString = json.encodeToString(collections)
            collectionsFile.writeText(jsonString)
        } catch (e: Exception) {
            Timber.e(e, "Failed to cache collections")
        }
    }

    suspend fun getCachedCollections(): Map<String, IconifyApiCollection>? = withContext(Dispatchers.IO) {
        try {
            if (!collectionsFile.exists()) return@withContext null
            val jsonString = collectionsFile.readText()
            json.decodeFromString<Map<String, IconifyApiCollection>>(jsonString)
        } catch (e: Exception) {
            Timber.e(e, "Failed to read cached collections")
            null
        }
    }

    suspend fun clearCache() = withContext(Dispatchers.IO) {
        try {
            memoryCache.clear()
            iconsDir.deleteRecursively()
            iconsDir.mkdirs()
            collectionsFile.delete()
            metadataFile.delete()
        } catch (e: Exception) {
            Timber.e(e, "Failed to clear cache")
        }
    }

    suspend fun getCacheSize(): Long = withContext(Dispatchers.IO) {
        try {
            cacheDir.walkTopDown().filter { it.isFile }.sumOf { it.length() }
        } catch (e: Exception) {
            0L
        }
    }

    suspend fun getCachedIconCount(): Int = withContext(Dispatchers.IO) {
        try {
            iconsDir.listFiles()?.size ?: 0
        } catch (e: Exception) {
            0
        }
    }

    suspend fun cleanOldIcons(maxAgeMillis: Long = 7 * 24 * 60 * 60 * 1000L) =
        withContext(Dispatchers.IO) {
            try {
                val metadata = getCacheMetadata()
                val now = System.currentTimeMillis()
                metadata.accessTimes.entries.toList().forEach { (iconId, lastAccess) ->
                    if (now - lastAccess > maxAgeMillis) {
                        val fileName = iconId.replace(":", "_").replace("/", "_") + ".svg"
                        val file = File(iconsDir, fileName)
                        if (file.exists() && file.delete()) {
                            metadata.accessTimes.remove(iconId)
                            memoryCache.remove(iconId)
                        }
                    }
                }
                saveCacheMetadata(metadata)
            } catch (e: Exception) {
                Timber.e(e, "Failed to clean old icons")
            }
        }

    private suspend fun updateAccessTime(iconId: String) = withContext(Dispatchers.IO) {
        try {
            val metadata = getCacheMetadata()
            metadata.accessTimes[iconId] = System.currentTimeMillis()
            if (metadata.accessTimes.size % 10 == 0) {
                saveCacheMetadata(metadata)
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to update access time")
        }
    }

    @kotlinx.serialization.Serializable
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
