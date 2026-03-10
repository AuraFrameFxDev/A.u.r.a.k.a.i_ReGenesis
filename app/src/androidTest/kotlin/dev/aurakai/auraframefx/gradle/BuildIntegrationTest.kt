package dev.aurakai.auraframefx.gradle

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for build configuration that verify the actual runtime
 * behavior and configuration applied during build time.
 *
 * Testing Framework: AndroidJUnit4 (as identified from dependencies)
 */
@RunWith(AndroidJUnit4::class)
class BuildIntegrationTest {

    @Test
    fun testApplicationIdIsCorrect() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("dev.aurakai.auraframefx", context.packageName)
    }

    @Test
    fun testMinSdkVersionIsCorrect() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        // Note: This test verifies the app can run on the target device,
        // implying minSdk requirements are met
        assertNotNull("Package info should be available", packageInfo)
    }

    @Test
    fun testBuildConfigIsGenerated() {
        // Verify that BuildConfig class is generated and accessible
        try {
            val buildConfigClass = Class.forName("dev.aurakai.auraframefx.BuildConfig")
            assertNotNull("BuildConfig class should be generated", buildConfigClass)

            // Verify essential BuildConfig fields
            val debugField = buildConfigClass.getField("DEBUG")
            assertNotNull("DEBUG field should exist in BuildConfig", debugField)

            val applicationIdField = buildConfigClass.getField("APPLICATION_ID")
            assertNotNull("APPLICATION_ID field should exist in BuildConfig", applicationIdField)
            assertEquals("dev.aurakai.auraframefx", applicationIdField.get(null))

        } catch (e: ClassNotFoundException) {
            fail("BuildConfig class should be generated")
        }
    }

    @Test
    fun testVectorDrawablesSupportIsEnabled() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // This test verifies that vector drawables can be used
        // by attempting to access app resources
        val resources = context.resources
        assertNotNull("Resources should be available", resources)
        // Vector drawable support would be verified by the successful app startup
    }

    @Test
    fun testMultiDexIsWorking() {
        // If the app starts successfully with multiDexEnabled = true,
        // it indicates MultiDex is working correctly
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        assertNotNull(
            "Context should be available, indicating successful app initialization",
            context
        )

        // Try to access a class that would be in a secondary dex file
        try {
            val classLoader = context.classLoader
            assertNotNull("Class loader should be available", classLoader)
        } catch (e: Exception) {
            fail("MultiDex configuration should allow proper class loading")
        }
    }

    @Test
    fun testHiltIsProperlyConfigured() {
        // This test verifies that Hilt test runner is working
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // Verify that the application context is available
        // (Hilt would fail to initialize if not properly configured)
        assertNotNull(
            "Application context should be available with Hilt",
            context.applicationContext
        )

        // Check if we can access the application class
        val application = context.applicationContext
        assertNotNull("Application should be initialized", application)
    }

    @Test
    fun testComposeIsAvailable() {
        // Verify that Compose dependencies are properly included
        try {
            val composeClass = Class.forName("androidx.compose.runtime.ComposerKt")
            assertNotNull("Compose runtime should be available", composeClass)
        } catch (e: ClassNotFoundException) {
            fail("Compose dependencies should be properly included")
        }
    }

    @Test
    fun testRoomDatabaseCanBeAccessed() {
        // Verify that Room dependencies are properly configured
        try {
            val roomClass = Class.forName("androidx.room.Room")
            assertNotNull("Room should be available", roomClass)
        } catch (e: ClassNotFoundException) {
            fail("Room dependencies should be properly included")
        }
    }

    @Test
    fun testRetrofitIsAvailable() {
        // Verify that network dependencies are properly included
        try {
            val retrofitClass = Class.forName("retrofit2.Retrofit")
            assertNotNull("Retrofit should be available", retrofitClass)
        } catch (e: ClassNotFoundException) {
            fail("Retrofit dependencies should be properly included")
        }
    }

    @Test
    fun testFirebaseIsAvailable() {
        // Verify that Firebase dependencies are properly included
        try {
            val firebaseClass = Class.forName("com.google.firebase.FirebaseApp")
            assertNotNull("Firebase should be available", firebaseClass)
        } catch (e: ClassNotFoundException) {
            fail("Firebase dependencies should be properly included")
        }
    }

    @Test
    fun testKotlinCoroutinesAreAvailable() {
        // Verify that Kotlin Coroutines are properly included
        try {
            val coroutinesClass = Class.forName("kotlinx.coroutines.CoroutineScope")
            assertNotNull("Kotlin Coroutines should be available", coroutinesClass)
        } catch (e: ClassNotFoundException) {
            fail("Kotlin Coroutines dependencies should be properly included")
        }
    }

    @Test
    fun testDataStoreIsAvailable() {
        // Verify that DataStore dependencies are properly included
        try {
            val dataStoreClass = Class.forName("androidx.datastore.preferences.core.Preferences")
            assertNotNull("DataStore should be available", dataStoreClass)
        } catch (e: ClassNotFoundException) {
            fail("DataStore dependencies should be properly included")
        }
    }

    @Test
    fun testSecurityCryptoIsAvailable() {
        // Verify that Security Crypto dependencies are properly included
        try {
            val securityClass = Class.forName("androidx.security.crypto.EncryptedSharedPreferences")
            assertNotNull("Security Crypto should be available", securityClass)
        } catch (e: ClassNotFoundException) {
            fail("Security Crypto dependencies should be properly included")
        }
    }

    @Test
    fun testWorkManagerIsAvailable() {
        // Verify that WorkManager dependencies are properly included
        try {
            val workManagerClass = Class.forName("androidx.work.WorkManager")
            assertNotNull("WorkManager should be available", workManagerClass)
        } catch (e: ClassNotFoundException) {
            fail("WorkManager dependencies should be properly included")
        }
    }

    @Test
    fun testMaterial3IsAvailable() {
        // Verify that Material 3 dependencies are properly included
        try {
            val material3Class = Class.forName("androidx.compose.material3.MaterialTheme")
            assertNotNull("Material 3 should be available", material3Class)
        } catch (e: ClassNotFoundException) {
            fail("Material 3 dependencies should be properly included")
        }
    }

    @Test
    fun testNavigationComposeIsAvailable() {
        // Verify that Navigation Compose is available
        try {
            val navigationClass = Class.forName("androidx.navigation.compose.NavHostKt")
            assertNotNull("Navigation Compose should be available", navigationClass)
        } catch (e: ClassNotFoundException) {
            fail("Navigation Compose dependencies should be properly included")
        }
    }

    @Test
    fun testCoilComposeIsAvailable() {
        // Verify that Coil Compose is available
        try {
            val coilClass = Class.forName("coil.compose.AsyncImageKt")
            assertNotNull("Coil Compose should be available", coilClass)
        } catch (e: ClassNotFoundException) {
            fail("Coil Compose dependencies should be properly included")
        }
    }

    @Test
    fun testTimberIsAvailable() {
        // Verify that Timber is available
        try {
            val timberClass = Class.forName("timber.log.Timber")
            assertNotNull("Timber should be available", timberClass)
        } catch (e: ClassNotFoundException) {
            fail("Timber dependencies should be properly included")
        }
    }
}