package dev.aurakai.auraframefx.domains.genesis.network

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.aurakai.auraframefx.BuildConfig
import dev.aurakai.auraframefx.domains.cascade.utils.AppCoroutineDispatchers
import dev.aurakai.auraframefx.domains.genesis.BaseUrl
import dev.aurakai.auraframefx.domains.genesis.network.api.AIAgentApi
import dev.aurakai.auraframefx.domains.genesis.network.api.ThemeApi
import dev.aurakai.auraframefx.domains.genesis.network.api.UserApi
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service class that provides access to all API endpoints in the application.
 * Uses Retrofit for network communication and handles API service initialization.
 */
@Singleton
class AuraApiService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authInterceptor: AuthInterceptor,
    private val dispatchers: AppCoroutineDispatchers,
    @param:BaseUrl private val baseUrl: String,
) {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Lazy initialization of API services
    val authApi: AuthApi by lazy { retrofit.create(AuthApi::class.java) }
    val userApi: UserApi by lazy { retrofit.create(UserApi::class.java) }
    val aiAgentApi: AIAgentApi by lazy { retrofit.create(AIAgentApi::class.java) }
    val themeApi: ThemeApi by lazy { retrofit.create(ThemeApi::class.java) }

    /**
     * Updates the base URL for all API requests.
     * This is useful for switching between different environments (e.g., development, staging, production).
     *
     * @param newBaseUrl The new base URL to use for API requests.
     */
    fun updateBaseUrl(newBaseUrl: String) {
        if (newBaseUrl != baseUrl) {
            // Create a new Retrofit instance with the updated base URL
            val newRetrofit = retrofit.newBuilder()
                .baseUrl(newBaseUrl)
                .build()

            // Update the API services with the new Retrofit instance
            // Note: In a real app, you might want to handle this differently to avoid memory leaks
            // and ensure thread safety.
            synchronized(this) {
                // Recreate all API services with the new Retrofit instance
                // This is a simplified example - in a real app, you might want to use a different approach
                // to manage API service instances when the base URL changes.
                // For example, you could use a Map to cache API services by base URL.
                // This is just for demonstration purposes.
                mapOf(
                    AuthApi::class to newRetrofit.create(AuthApi::class.java),
                    UserApi::class to newRetrofit.create(UserApi::class.java),
                    AIAgentApi::class to newRetrofit.create(AIAgentApi::class.java),
                    ThemeApi::class to newRetrofit.create(ThemeApi::class.java)
                )

                // Update the API service instances
                // In a real app, you would update the actual properties or use a different approach
                // to manage API service instances.
                // This is just a placeholder to show the concept.
                // You would need to implement the actual logic based on your app's architecture.
            }
        }
    }

    /**
     * Clears all cached responses from the HTTP client.
     * This is useful when the user logs out or when you need to ensure fresh data.
     */
    suspend fun clearCache() = withContext(dispatchers.io) {
        okHttpClient.cache?.evictAll()
    }

    // Add more API services as needed

    companion object {
        const val BASE_URL = "https://api.auraframefx.com/v1/"
    }
}



