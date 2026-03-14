# Genesis Protocol - Secure Communication Module
# Only keep classes annotated with @Keep (for reflection/serialization)
-keep @androidx.annotation.Keep class * { *; }
# Add specific API classes below if needed
# -keep class dev.aurakai.auraframefx.securecomm.MyApiClass { public *; }
-keep class dev.aurakai.auraframefx.securecomm.** { *; }

# Crypto and security classes
-keep class javax.crypto.** { *; }
-keep class java.security.** { *; }
-keep class org.bouncycastle.** { *; }

# Kotlin serialization
-keepclassmembers class **$$serializer {
    *** serializer(...);
}
-keepclasseswithmembers class * {
    *** Companion;
}
-keepclassmembers class * {
    *** Companion;
}

# Hilt
-keepclasseswithmembers class * {
    @dagger.hilt.** <methods>;
}

# Remove debug logging
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
