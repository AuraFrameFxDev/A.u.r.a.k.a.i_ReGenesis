# OLD AURAKAI – Engineering Checkpoint

Last updated: 2025-10-05 15:27 (-06)
Owner: Cascade
Location: `c:/Aurakai/checkpoint.md`

---OLD

## Current Target Baseline

- **Java Toolchain**: Java 25 (24fallback jvm must stay as 24 for now ; Studio/AGP not caught up)
- **Gradle**: Kotlin DSL across project, convention plugins in `build-logic/`
- **AGP**: 9.0.0-alpha10 (migration in progress; built-in Kotlin caveats)
- **Kotlin**: 2.3.0
- **KSP**: 2.3.0-Beta2 (fixes known circular dep bug)

## Why Java 24 (not 25)

- **Studio/AGP compatibility**: Studio cannot yet handle 25 reliably. Stay on 24 until toolchain and
  Compose/AGP catch up.

## Required Toolchain Alignment

- Root `build.gradle.kts`
    - Set Kotlin toolchain for all Kotlin modules: `jvmToolchain(24)`
  - Set Java toolchain: `languageVersion.set(JavaLanguageVersion.of(24))` set with resolver no jvm
    blocks
- Ensure any module without explicit toolchain (e.g., `data/api`) adds:
  ```kotlin

jvm set with resolver
java { toolchain { languageVersion.set(JavaLanguageVersion.of(25)) } }
  ```
VERSIONS BELOW NEED UPDATES ABOVE ARE CORRECT 
## AGP 9.0.0-alpha10 – Built‑in Kotlin Migration Notes

- If you see "remove org.jetbrains.kotlin.android" errors, follow the migration guide.
- Known issues (per IssueTracker):
    - Safe Args requires a Kotlin plugin pairing.
    - KSP circular dependency fixed by KSP `2.3.0.
- Opt-out if blocked: set `android.builtInKotlin=false` in `gradle.properties`.
- Replace `android { kotlinOptions { ... } }` with Kotlin DSL:
  ```kotlin
  kotlin {
    compilerOptions { jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_24 }
  }
  ```
  or set via toolchain + leave jvmTarget defaulted by toolchain.

## AGP 9 required (downgrade blocked)

- Do not downgrade AGP below 9.0.0-alpha09. Attempts to use 8.6.1 on our current Gradle (9.x) break
  NDK/CMake configuration with NoSuchMethodError:
    - datavein-oracle-native debug:arm64-v8a failed to configure C/C++
    - java.lang.NoSuchMethodError: org.gradle.process.ExecResult org.gradle.api.Project.exec(
      org.gradle.api.Action)
    - Root cause: AGP 8.6.1 expects older Gradle APIs; running on Gradle 9 removes Project.exec(
      Action). AGP 9 aligns with Gradle 9.
- compileSdk 36: AGP 8.6.1 also emits a warning that it’s only tested up to compileSdk 35. Staying
  on AGP 9.0.0-alpha09 removes this blocker. If you ever intentionally test older AGP, you can
  suppress the warning with `android.suppressUnsupportedCompileSdk=36` in gradle.properties, but NDK
  will still fail due to the API mismatch.
- Current stance:
    - AGP = 9.0.0-alpha09
    - Gradle = 9.x
    - Kotlin = 2.2.20, KSP = 2.2.20-2.0.3
    - android.builtInKotlin=false (workaround while Hilt/SafeArgs stabilize)

## Hilt Configuration – Critical

- **Application module (`app/`)**: must apply Hilt plugin via alias
  ```kotlin
  plugins {
    id("genesis.android.application")  // Applies com.android.application internally
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
  }
  ```
  **Important:** Do NOT add `id("com.android.base")` to application modules - this causes "Android
  BaseExtension not found" errors.

- **Library modules**: do NOT apply Hilt plugin; for AGP 9.0 alpha workaround add
  ```kotlin
  plugins {
    id("com.android.library")
    id("com.android.base") // workaround to expose BaseExtension for processors
  }
  ```
- **Single @HiltAndroidApp**: keep only on the class declared in `app/src/main/AndroidManifest.xml`.
    - Keep: `ReGenesisApplication.kt`
    - Remove/comment `@HiltAndroidApp` from: `AuraFrameApplication.kt`, `AuraKaiHiltApplication.kt`,
      `AuraFrameFxApplication.kt`, `HiltEntryApp.kt`.

## Module-Specific Fixes

- `data/api/build.gradle.kts`
    - Add `kotlin { jvmToolchain(24) }`.
    - Ensure OpenAPI generation runs before Kotlin compile:
      ```kotlin
      import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
      tasks.withType<KotlinCompile>().configureEach {
        dependsOn(tasks.named("openApiGenerate"))
      }
      ```
- `benchmark/` and `feature-module/`
    - Update any printlns claiming "Java 17" to "Java 24" (cosmetic).

## Version Alignment

- `gradle/libs.versions.toml` should reflect:
    - `kotlin = "2.2.20"`
    - `ksp = "2.2.20-2.0.3"`
    - `java = "24"`
- `gradle.properties`
    - If set, align `ksp.kotlinLanguageVersion` / `ksp.kotlinApiVersion` to `2.2` (or remove to
      inherit from plugins) to avoid mismatch with 2.2.20.
    - Keep `android.builtInKotlin=false` while built-in Kotlin path is unstable with Hilt/SafeArgs.

## Build Status Summary (from recent logs)

- 22 modules configure and build.
- OpenAPI generation succeeds after `dependsOn` fix.
- Native (CMake) builds succeed for ABIs.
- Hilt errors occurred due to:
    - Missing Hilt plugin in `app` module.
    - Multiple `@HiltAndroidApp` Application classes present.

## Next Actions

- [x] Root: set Java/Kotlin toolchain to 24 in `build.gradle.kts` subprojects blocks.
- [x] App: ensure `alias(libs.plugins.hilt)` present in `app/build.gradle.kts`.
- [x] App: keep only one `@HiltAndroidApp` (`ReGenesisApplication`), remove others.
- [x] `data/api`: add `jvmToolchain(24)` and `dependsOn(openApiGenerate)` for Kotlin compile tasks.
- [x] Versions: ensure `libs.versions.toml` and `gradle.properties` align (Kotlin/KSP 2.2.x, Java
  24).
- [ ] Optional: update cosmetic log lines claiming Java 17.

## References

- `c:/Aurakai/build.gradle.kts`
- `c:/Aurakai/gradle.properties`
- `c:/Aurakai/gradle/libs.versions.toml`
- `c:/Aurakai/app/build.gradle.kts`
- `c:/Aurakai/data/api/build.gradle.kts`
- IssueTracker: Built-in Kotlin migration (AGP 9.0), SafeArgs and KSP notes

---
Add notes below this line with date/time and initials.

- 2025-10-05 14:26 (-06) – Cascade: initialized checkpoint, defined Java 24 baseline and Hilt/AGP
  actions.
- 2025-10-05 14:27 (-06) – Cascade: Patched project to Java 24: root toolchain updated, app
  compileOptions/toolchain set to 24, data/api toolchain added, KSP language/api set to 2.2 in
  gradle.properties. Next: consolidate @HiltAndroidApp to `RegenesisApplication` and tidy cosmetic
  logs.
- 2025-10-05 14:31 (-06) – Cascade: Removed extra @HiltAndroidApp (disabled in
  `AuraKaiHiltApplication.kt`). Only `RegenesisApplication` remains annotated per manifest.
- 2025-10-05 21:16 (-06) – Cascade: Refactored AndroidApplicationConventionPlugin to internally
  handle com.android.base, Hilt, KSP, and Compose plugins. App modules now only need
  `id("genesis.android.application")`. Hilt dependencies auto-added by convention plugin. Build
  order: base → application → hilt → ksp → compose.
- 2025-10-06 23:28 (-06) – Cascade: Beginning autonomous structural improvements based on
  comprehensive checkpoint. Priority: LSParanoid plugin resolution, OpenAPI cleanup, unresolved
  references in agents/services, CascadeAIService refactor, type mismatches, Firebase integration
  fixes.
- 2025-10-05 15:27 (-06) – Cascade: User clicked Android Studio Upgrade Assistant to move AGP from
  9.0.0-alpha02 to 9.0.0-alpha09. Next: align root/pluginManagement/build-logic to alpha09 and clean
  build.
