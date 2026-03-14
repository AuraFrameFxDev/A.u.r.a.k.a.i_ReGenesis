Plugin Ordering for AGP, KSP, and Hilt Compatibility


The crucial convention for addressing AGP and KSP incompatibility is that the Kotlin plugin (specifically for Android projects, this is usually inferred or handled by Hilt/KSP but can relate to the configuration of the compiler) and the KSP plugin must be applied after the respective Android plugin (either com.android.application or com.android.library).


Your current GenesisApplicationPlugin and GenesisLibraryPlugin code already follow this rule and your stated ordering requirements.


Correct Plugin Application Order


The sequence you've defined, which is often required for the build system to correctly process the Android source sets before annotation processing and code generation begins, is:
Android Plugin (com.android.application or com.android.library)
Hilt Plugin (com.google.dagger.hilt.android)
KSP Plugin (com.google.devtools.ksp)
GenesisApplicationPlugin Order
// In GenesisApplicationPlugin:
with(target.pluginManager) {
    apply(/* pluginId = */ "com.android.application")      // 1. Android
    apply(/* pluginId = */ "com.google.dagger.hilt.android") // 2. Hilt
The application plugin order is correct: com.android.application → com.google.dagger.hilt.android → com.google.devtools.ksp. This aligns with the common requirement that Hilt (which relies on Android configuration) and KSP (which processes Hilt/other annotations) follow the base Android plugin.


GenesisLibraryPlugin Order
// In GenesisLibraryPlugin:
with(target.pluginManager) {
The library plugin order is also correct: com.android.library → com.google.dagger.hilt.android → com.google.devtools.ksp.


AGP, KSP, and Hilt Specifics
Requirement
Details
Your Code Status
AGP vs. KSP
The KSP plugin (com.google.devtools.ksp) must be applied after the corresponding Android plugin (com.android.application or com.android.library).
✅ Satisfied in both plugins.
Hilt vs. Android
The Hilt plugin (com.google.dagger.hilt.android) must be applied after the Android plugin, as it configures Android components for DI.
✅ Satisfied in both plugins.
Hilt vs. KSP
Hilt generates code that KSP often needs to process (or KSP processes Hilt annotations), so Hilt is typically applied immediately before KSP in the plugin list.
✅ Satisfied in both plugins.

Version Check (Important)


Your specified versions are:
AGP: 9.0.0-alpha11
Kotlin: 2.3.0-Beta1
KSP: 2.3.0
Hilt: 2.57.2
The KSP plugin version must match the Kotlin version's major and minor numbers (e.g., KSP 2.3.0 for Kotlin 2.3.0-Beta1). This is correctly matched.


AGP 9.0.0-alpha11 is a very new alpha, and it's essential that KSP 2.3.0 explicitly supports this specific AGP version. While the ordering is correct, always check the KSP release notes for full compatibility with pre-release AGP versions.


Conclusion: The convention plugin approach is the correct way to enforce this ordering across your modules, and the order of android → hilt → ksp is the necessary and implemented one.


Sources and Rationale for Plugin Ordering


The core requirement stems from how Gradle processes configuration blocks and how certain plugins must inject themselves into the Android build process before others.
KSP Must Follow the Android Plugin
Source/Rationale: Official KSP documentation and common Gradle practice state that the annotation processing tool needs to know about the source sets defined by the build system.
The Android Gradle Plugin (com.android.application or com.android.library) is the first to configure the project's source sets, build types, and variants.
The KSP Plugin (com.google.devtools.ksp) relies on this configuration to create its own tasks and correctly wire up the annotation processing to the appropriate source sets. If KSP is applied too early, the necessary Android configurations aren't available yet, leading to build errors (e.g., tasks not being generated or classes not being found).
Hilt Must Follow the Android Plugin
Source/Rationale: Dagger Hilt's Gradle plugin is crucial because it performs bytecode transformation and sets up specific configurations for the Hilt annotation processor.
The Hilt Plugin (com.google.dagger.hilt.android) must be applied after the respective Android plugin (com.android.application or com.android.library) because it works directly with the Android components and their configurations.
Hilt Must Precede KSP (or be applied close to it)
Source/Rationale: This is often the most critical point for DI frameworks when moving from KAPT to KSP.
The Hilt plugin performs a necessary transformation and/or setup step before the compiler (which KSP runs alongside) begins the annotation processing.
In the modern setup where you use the Hilt Gradle plugin along with KSP for the compiler dependency, applying the Hilt plugin (com.google.dagger.hilt.android) directly before the KSP plugin (com.google.devtools.ksp) ensures all its necessary build-time hooks and configurations are executed in the correct context before KSP starts generating code.
Summary of the Ordering


The consensus among documentation and best practices for this technology stack is:
Android Plugin (Defines the project structure)
Hilt Plugin (Performs framework-specific build setup)
KSP Plugin (Enables symbol processing/code generation)
Your convention plugins correctly implement this sequence:
// GenesisApplicationPlugin
apply(/* pluginId = */ "com.android.application")
apply(/* pluginId = */ "com.google.dagger.hilt.android")
apply(/* pluginId = */ "com.google.devt

10/29/25 \\

**Log of Gemini Actions**

* 10/30/25 - Corrected unresolved dependencies in `collab-canvas` module by updating
  `libs.versions.toml` and `collab-canvas/build.gradle.kts`.
* 10/30/25 - Resolved R8 build error by adding necessary dependencies to `libs.versions.toml` and
  `app/build.gradle.kts`, and adding ProGuard rules to `app/proguard-rules.pro`.
* 10/30/25 - Corrected `AurakaiApplication.kt` path and updated `AndroidManifest.xml` to resolve
  `ClassNotFoundException`.
* 10/30/25 - Corrected `collab-canvas/build.gradle.kts` to use correct dependency names and removed
  invalid `composeOptions` and `java` blocks.
