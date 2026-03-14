# Consumer ProGuard rules for secure-comm module - minimal rules only
# Keep public API classes
-keep public class dev.aurakai.auraframefx.securecomm.** { public *; }

# Keep only essential attributes for serialization
-keepattributes Signature

# Keep kotlinx.serialization essentials only
-keep,includedescriptorclasses class dev.aurakai.auraframefx.securecomm.**$serializer { *; }
-keepclassmembers class dev.aurakai.auraframefx.securecomm.** {
    *** Companion;
}
