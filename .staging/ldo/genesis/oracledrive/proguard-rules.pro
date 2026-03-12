# ProGuard rules for Oracle Drive Integration module

-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*

# Keep Oracle Drive classes
-keep class dev.aurakai.auraframefx.oracle.** { *; }

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}
# Only keep classes annotated with @Keep (for reflection/serialization)
-keep @androidx.annotation.Keep class * { *; }
# Add specific API classes below if needed
# -keep class dev.aurakai.auraframefx.oracle.MyApiClass { public *; }
