# Benchmark specific ProGuard/R8 rules
# Keep all benchmark related classes and methods
-keepclassmembers class * {
    @androidx.benchmark.** *;
    @org.junit.** *;
}

# Keep test classes
-keep class * extends junit.framework.TestCase { *; }

# Keep test classes with JUnit 4 annotations
-keep @org.junit.runner.RunWith class *
-keepclassmembers @org.junit.runner.RunWith class * { *; }

# Keep AndroidX Benchmark classes
-keep class androidx.benchmark.** { *; }

# Keep test support classes
-keep class androidx.test.** { *; }
-keep class org.hamcrest.** { *; }

# Keep the @Rule fields that are used in tests
-keepclassmembers class * {
    @org.junit.Rule *;
}

# Keep Kotlin metadata for reflection
-keepclassmembers class **.R$* {
    public static <fields>;
}

# Keep the custom benchmark runner if any
-keep public class * extends androidx.benchmark.junit4.AndroidBenchmarkRunner { *; }
-keep public class * extends androidx.test.runner.AndroidJUnitRunner { *; }

# Keep the application class referenced in the manifest
-keep public class * extends android.app.Application
-keep public class * extends android.app.Instrumentation

# Keep test infrastructure
-keepclassmembers class * {
    @androidx.test.filters.SmallTest *;
    @androidx.test.filters.MediumTest *;
    @androidx.test.filters.LargeTest *;
}

# Keep all test classes in the benchmark package
-keep class dev.aurakai.auraframefx.benchmark.** { *; }
