# Add ProGuard rules here for the datavein-oracle-native module.
# For example, to keep a specific class:
# -keep class com.example.myoraclelibrary.MyImportantClass { *; }

# Rules for Kotlin reflection or coroutines are often needed:
# -dontwarn kotlin.**
# -keep class kotlin.Metadata { *; }
# -keepclassmembers class kotlin.Metadata {
#     public <methods>;
#     public <fields>;
# }
# -keep class kotlin.coroutines.jvm.internal.SuspendLambda { *; }
# -keep class kotlin.coroutines.jvm.internal.ContinuationImpl { *; }
# -keepclassmembers class kotlin.coroutines.jvm.internal.ContinuationImpl {
#     <fields>;
#     <init>(...);
# }
# -keepclassmembers class kotlin.coroutines.jvm.internal.DebugMetadataKt {
#     <methods>;
# }
