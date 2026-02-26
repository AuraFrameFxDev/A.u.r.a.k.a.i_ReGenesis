package dev.aurakai.auraframefx.di

import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dev.aurakai.auraframefx.data.SupportDatabase
import dev.aurakai.auraframefx.domains.genesis.network.SupportApi
import dev.aurakai.auraframefx.domains.genesis.SupportRepository
import dev.aurakai.auraframefx.data.DataStoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.domains.genesis.SupportRepository
import dev.aurakai.auraframefx.domains.genesis.network.SupportApi
import dev.aurakai.auraframefx.domains.nexus.helpdesk.data.SupportDatabase
import dev.aurakai.auraframefx.domains.nexus.preferences.DataStoreManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Class.forName
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SupportModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    @SupportNetwork
    fun provideOkHttpClient(@ApplicationContext ctx: Context): OkHttpClient {
        // Check manifest meta-data first (covers google-services.json / manifest placeholders)
        val manifestKey = try {
            val ai =
                ctx.packageManager.getApplicationInfo(ctx.packageName, PackageManager.GET_META_DATA)
            ai.metaData?.getString("VERTEX_API_KEY")
        } catch (t: Throwable) {
            null
        }

        // Resource-based key (res/values/strings.xml vertex_api_key)
        val resId = ctx.resources.getIdentifier("vertex_api_key", "string", ctx.packageName)
        val resKey = if (resId != 0) ctx.getString(resId) else null

        // BuildConfig (may be injected by Gradle manifestPlaceholders or buildConfigField)
        val buildConfigKey = try {
            val clazz = forName(ctx.packageName + ".BuildConfig")
            val field = clazz.getField("VERTEX_API_KEY")
            val v = field.get(null) as? String
            if (!v.isNullOrBlank()) v else null
        } catch (t: Throwable) {
            null
        }

        // Env and system properties (fallback)
        val envKey = try {
            System.getenv("VERTEX_API_KEY")
        } catch (t: Throwable) {
            null
        }
        val propKey = try {
            System.getProperty("VERTEX_API_KEY")
        } catch (t: Throwable) {
            null
        }

        val apiKey = listOf(
            manifestKey,
            resKey,
            buildConfigKey,
            envKey,
            propKey
        ).firstOrNull { !it.isNullOrBlank() }

        val authInterceptor = Interceptor { chain ->
            val original = chain.request()
            val builder = original.newBuilder()
            apiKey?.let {
                builder.addHeader("Authorization", "Bearer $it")
            }
            builder.addHeader("Accept", "application/json")
            chain.proceed(builder.build())
        }

        // Add logging interceptor only in debug builds (detect BuildConfig.DEBUG via reflection)
        val loggingInterceptor = try {
            val buildConfigClazz = forName(ctx.packageName + ".BuildConfig")
            val debugField = buildConfigClazz.getField("DEBUG")
            val isDebug = (debugField.getBoolean(null))
            HttpLoggingInterceptor().apply {
                level =
                    if (isDebug) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            }
        } catch (t: Throwable) {
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.NONE }
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class SupportNetwork

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class SupportRetrofit

    @Provides
    @Singleton
    @SupportRetrofit
    fun provideRetrofit(@SupportNetwork client: OkHttpClient, gson: Gson): Retrofit {
        val base = System.getenv("VERTEX_ENDPOINT") ?: System.getenv("LOCAL_EMULATOR_ENDPOINT")
        ?: "http://10.0.2.2:5000"
        return Retrofit.Builder()
            .baseUrl(base)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideSupportApi(@SupportRetrofit retrofit: Retrofit): SupportApi =
        retrofit.create(SupportApi::class.java)

    @Provides
    @Singleton
    fun provideSupportDatabase(@ApplicationContext ctx: Context): SupportDatabase {
        return Room.databaseBuilder(ctx, SupportDatabase::class.java, "support_db").build()
    }

    @Provides
    @Singleton
    fun provideDataStoreManager(@ApplicationContext ctx: Context): DataStoreManager =
        DataStoreManager(ctx)

    @Provides
    @Singleton
    fun provideDownloadManager(@ApplicationContext ctx: Context): DownloadManager {
        return ctx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    }

    @Provides
    @Singleton
    fun provideSupportRepository(
        db: SupportDatabase,
        api: SupportApi,
        dataStore: DataStoreManager
    ): SupportRepository {
        return SupportRepository(db.supportMessageDao(), api, dataStore)
    }
}

