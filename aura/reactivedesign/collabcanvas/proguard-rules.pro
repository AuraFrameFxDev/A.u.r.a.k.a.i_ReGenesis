# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Genesis Protocol - Collaborative Canvas Module
# Only keep necessary classes for reflection/serialization/JNI


# If you use @Keep annotation, uncomment below:
# -keep @dev.aurakai.auraframefx.common.Keep class * { *; }

# Canvas and drawing classes (only if accessed via reflection/JNI)
# -keep class android.graphics.Canvas { *; }
# -keep class androidx.compose.ui.graphics.Color { *; }

# Real-time collaboration classes (scope to used classes)
-keep class kotlinx.coroutines.flow.StateFlow { *; }
-keep class kotlinx.serialization.Serializable { *; }

# WebSocket and networking (scope to used classes)
-keep class okhttp3.WebSocket { *; }
-keep class retrofit2.Call { *; }

# Kotlin serialization (scope to models)
-keepclassmembers class dev.aurakai.auraframefx.collabcanvas.model.**$serializer {
    *** serializer(...);
}

# Compose runtime (only if accessed via reflection)
# -keep class androidx.compose.runtime.Composable { *; }

# Hilt
-keepclasseswithmembers class * {
    @dagger.hilt.** <methods>;
}

# Remove debug and logging
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Removed overly broad keep rules
# -keep class dev.aurakai.auraframefx.collabcanvas.** { *; }
# -keep class android.graphics.** { *; }
# -keep class androidx.compose.ui.graphics.** { *; }
# -keep class kotlinx.coroutines.flow.** { *; }
# -keep class kotlinx.serialization.** { *; }
# -keep class okhttp3.** { *; }
# -keep class retrofit2.** { *; }
# -keep class androidx.compose.runtime.** { *; }
# -keep class androidx.compose.ui.** { *; }

# Only keep classes annotated with @Keep (for reflection/serialization)
-keep @androidx.annotation.Keep class * { *; }
# Add specific API classes below if needed
# -keep class dev.aurakai.collabcanvas.MyApiClass { public *; }
