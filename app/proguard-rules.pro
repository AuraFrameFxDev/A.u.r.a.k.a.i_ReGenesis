# ═══════════════════════════════════════════════════════════════════════════
# AURAKAI GENESIS - ProGuard/R8 Configuration
# ═══════════════════════════════════════════════════════════════════════════
#
# ⚠️ CRITICAL: Always test release builds thoroughly!
#
# This configuration protects reflection-heavy code from being stripped:
# - Hilt/Dagger dependency injection
# - Room database entities and DAOs
# - Firebase serialization
# - Kotlin serialization (@Serializable classes)
# - YukiHook / Xposed framework (ROM tools)
# - Kotlin Coroutines
#
# TESTING CHECKLIST for release builds:
# ✓ Hilt-injected classes work
# ✓ Room database queries execute
# ✓ Firebase serialization/deserialization works
# ✓ YukiHook API calls succeed (ROM tools)
# ✓ @Serializable classes serialize correctly
# ✓ Coroutines launch and complete
#
# If you encounter ClassNotFoundException, NoSuchMethodException, or
# similar errors in release builds, add specific -keep rules below.
#
# ═══════════════════════════════════════════════════════════════════════════

# Preserve line numbers for debugging release crashes
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# --- Critical Application Rules ---

# Keep the main Application class, which is the entry point.
# This is essential to prevent ClassNotFoundException at startup.
-keep public class dev.aurakai.auraframefx.aura.ui.AurakaiApplication { *; }

# Keep all classes that are referenced in the AndroidManifest.xml,
# such as Activities, Services, BroadcastReceivers, and ContentProviders.
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# Keep Hilt and Dagger classes required for dependency injection.
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
-keepclasseswithmembers class * {
    @dagger.* <fields>;
}
-keepclasseswithmembers class * {
    @dagger.* <methods>;
}

# Keep Room database entities, DAOs, and database classes.
-keep class dev.aurakai.auraframefx.data.database.entities.** { *; }

# Keep classes annotated with @Keep. This is a good practice for classes
# accessed via reflection.
-keep @androidx.annotation.Keep class * { *; }

# --- Suppress Warnings for External Libraries ---
# These rules suppress warnings about classes that are part of the libraries'
# internal implementation and are not directly used by your app.
-dontwarn com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
-dontwarn com.google.auto.service.AutoService
-dontwarn com.google.auto.value.extension.memoized.Memoized
-dontwarn com.google.common.collect.Streams
-dontwarn jakarta.servlet.ServletContainerInitializer
-dontwarn java.lang.Module
-dontwarn java.lang.module.ModuleDescriptor
-dontwarn javax.lang.model.**
-dontwarn javax.tools.**
-dontwarn com.google.api.client.**
-dontwarn com.squareup.okhttp.**
-dontwarn org.joda.time.**
-dontwarn java.lang.reflect.AnnotatedType

# --- Optimizations ---
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-dontpreverify

# --- Logging Removal ---
# Remove logging calls from release builds.
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

-assumenosideeffects class timber.log.Timber {
    public static void v(...);
    public static void i(...);
    public static void w(...);
    public static void d(...);
    public static void e(...);
}
