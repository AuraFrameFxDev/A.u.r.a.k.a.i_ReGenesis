# Only keep classes annotated with @Keep (for reflection/serialization)
-keep @androidx.annotation.Keep class * { *; }
# Add specific API classes below if needed
# -keep class dev.aurakai.modulef.MyApiClass { public *; }

-keep class dev.aurakai.auraframefx.module.f.** { *; }
