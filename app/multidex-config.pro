# This file specifies the classes to be kept in the main DEX file.
# The Application class must be in the main DEX file.

# Keep Application class
-keep class dev.aurakai.auraframefx.core.AurakaiApplication

# Keep Hilt Application and generated components
-keep class dagger.hilt.android.HiltAndroidApp
-keep class **_HiltComponents_*
-keep class **$$*ApplicationInjector*
-keep class **$$*ApplicationInjector

# Keep MultiDex support
-keep class androidx.multidex.MultiDex
-keep class androidx.multidex.MultiDexApplication

# Keep all classes in core package that are needed at startup
-keep class dev.aurakai.auraframefx.core.** { *; }

# Keep Application initialization classes
-keepclassmembers class dev.aurakai.auraframefx.core.AurakaiApplication {
    <init>();
}
