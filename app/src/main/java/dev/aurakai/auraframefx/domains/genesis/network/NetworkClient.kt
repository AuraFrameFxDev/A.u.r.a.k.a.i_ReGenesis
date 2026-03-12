package dev.aurakai.auraframefx.domains.genesis.network

import android.content.Context
import dev.aurakai.auraframefx.domains.aura.AuraNetwork
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.io.File

/**
 * Centralized network client for handling all API communications.
 * Manages OkHttp and Retrofit instances with proper configuration.
 *
 * Note: This class is provided by NetworkModule via factory method.
 * Do not add @Inject or @Singleton annotations here.
 */
class NetworkClient(
    private val context: Context,
    @AuraNetwork private val okHttpClient: OkHttpClient,
    @AuraNetwork private val retrofit: Retrofit,
    private val authInterceptor: AuthInterceptor,
    private val connectivityManager: NetworkConnectivityManager
) {
    private val cacheSize = (10 * 1024 * 1024).toLong() // 10 MB
    private val cacheDir = File(context.cacheDir, "http_cache")
    private val cache = Cache(cacheDir, cacheSize)

    /**
     * Creates an API service instance for the specified service interface.
     * @param service The service interface class
     * @return An implementation of the service interface
     */
    fun <T> createService(service: Class<T>): T {
        return retrofit.create(service)
    }

    /**
     * Clears the HTTP cache.
     */
    fun clearCache() {
        cache.evictAll()
    }

    /**
     * Gets the current cache size in bytes.
     */
    fun getCacheSize(): Long {
        return cache.size()
    }
}

