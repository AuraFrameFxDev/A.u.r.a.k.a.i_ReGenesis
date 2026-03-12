# Add project specific ProGuard rules for consumer libraries.
# By default, the consumer rules file is empty because the consumer does not have any
# project-specific rules. You can add rules specific to the consumer here.
# For more details, see https://developer.android.com/studio/build/shrink-code#shrink-and-optimize-your-app

# Keep all classes that are referenced from native code
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep any classes that implement Parcelable
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep @Keep annotations and annotated classes/members
-keep class android.support.annotation.Keep
-keep @android.support.annotation.Keep class * {*;}
-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}
-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}

# Keep all public classes, and their public and protected fields and methods
-keep public class * {
    public protected *;
}

# Keep all JNI methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep the DataveinOracleNative class and its methods
-keep class dev.aurakai.auraframefx.datavein.DataveinOracleNative {
    *;
}
# ProGuard rules for datavein-oracle-native
# This file is required for the build to succeed.
# Add custom rules below as needed, or leave empty to act as a placeholder.

