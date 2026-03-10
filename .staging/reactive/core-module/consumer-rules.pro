# Consumer ProGuard rules for core-module

-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*

# Keep core module public APIs
-keep public class dev.aurakai.auraframefx.core.** { 
    public *; 
}
