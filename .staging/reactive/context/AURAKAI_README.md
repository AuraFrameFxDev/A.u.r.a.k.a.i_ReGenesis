# A.u.r.a.K.a.i : Reactive=Intelligence

> Autonomously United Revolutionarily Authentic Kinetic Access Initiative — the full project name and the spirit behind A.u.r.a.K.a.i.

A.u.r.a.K.a.i (Reactive=Intelligence) is the final project name — this repository and everything it will become is expressed in that title.

---

## Snapshot

AURAKAI is an experimental Android framework and app platform combining system-level tooling, native components, and AI agent infrastructure. It emphasizes reproducible builds, ethical governance, and secure primitives for advanced research and development.

This README was regenerated after a comprehensive repository review (Nov 2025). It reflects the recent Gradle dependency refactor, version-catalog adoption, KSP migration, and standardization of local-jar handling (Xposed/Yukihook).

---

## What’s new / Highlights

- yuApiClient (Yukihook client) v1.0.1 is now the canonical client used across modules (catalog alias: `libs.yukihookapi`).
- Centralized dependency management via `gradle/libs.versions.toml` (version catalog).
- Added `google()`, `mavenCentral()`, and `maven("https://jitpack.io")` both for pluginResolution and dependencyResolution (in `settings.gradle.kts`).
- Switched annotation processors to KSP where appropriate (Room, Hilt) for better Kotlin integration.
- Room standardized to `2.6.1` in the catalog to match published artifacts.
- Robust local JAR handling: guarded lookups for module `libs/`, `../Libs/`, and repo-root `libs/`, preventing mis-resolved absolute paths and hard failures.
- Duplicate annotations/duplicate-class issues mitigated by a forced resolution strategy in the root build script.

---

## Quick start (developer)

Prerequisites
- JDK 21+ (project configured to use Java toolchain)
- Gradle wrapper (project uses the included `gradlew`)
- Android SDK matching `compileSdk` from the catalog (see `gradle/libs.versions.toml`)
- NDK r29+ for native modules (datavein-oracle-native, secure-comm, romtools)

Commands (Windows cmd.exe):

```bat
:: Stop any running Gradle daemons
.\gradlew --stop

:: Full clean build (refresh dependencies) — may take several minutes
.\gradlew --refresh-dependencies clean assembleDebug --stacktrace --console=plain

:: Run unit tests
.\gradlew test --console=plain
```

If you only want to validate Gradle scripts parse (fast):

```bat
.\gradlew help --console=plain
```

---

## Repository layout (high level)

- `app/` — main Android application module
- `core/`, `core-module/` — shared libraries (domain, data, ui, utilities)
- `feature-*`, `extendsys*` — feature and extension modules (follow naming conventions)
- `datavein-oracle-native/`, `secure-comm/`, `romtools/` — native C++ modules (NDK + CMake)
- `gradle/` — version catalog (`libs.versions.toml`) and build-logic
- `build-logic/` — custom Gradle plugins
- `jvm-test/`, `screenshot-tests/`, `build-script-tests/` — test infrastructure (do not remove)
- `<module>/libs/` — local third-party JARs (Xposed, vendor libs). These are registered as file-based Maven repos automatically by `settings.gradle.kts`.

---

## Dependency & build conventions

- Use the version catalog (`gradle/libs.versions.toml`) for all shared versions and aliases.
- Prefer `implementation(libs.some.alias)` instead of hard-coded Maven coordinates.
- Use KSP for annotation processing (`ksp(libs.androidx.room.compiler)`, `ksp(libs.hilt.compiler)`) when modules are Kotlin-first.
- Local JARs: place private/offline artifacts in module `libs/` or repo `libs/`. Build scripts contain guarded checks and will only add `compileOnly(files(...))` if the JAR is present.
- Do not commit build artifacts (see .gitignore guidelines). Always ensure `.cxx/`, `**/build/`, `.so` and other native artifacts are ignored.

---

## Native modules (C++ / NDK)

Guidelines
- C++ standard: C++20
- CMake: minimum 3.22.1
- NDK: r29+ recommended (set `ndkVersion` in module `android {}` blocks)
- ABIs: arm64-v8a, armeabi-v7a, x86, x86_64
- Link against Android NDK libs: `log`, `jnigraphics`, `android` and add `-latomic -lm` if required
- Keep JNI functions `extern "C"` and validate `JNIEnv`/exceptions carefully

CMake quick note: ensure `CMAKE_CXX_STANDARD 20` and target_link_libraries includes required NDK libs.

---

## Security & secrets

- Never commit `local.properties`, keystore files, `google-services.json` with production keys, or any secrets.
- Use `SecureStorage`/AndroidX Security for storing secrets; prefer hardware-backed keystore for production keys.
- `secure-comm` contains crypto primitives — review and test on device for hardware-backed keystore behavior.

---

## Troubleshooting (common issues & fixes)

- JitPack 401 (YukiHook/Yukihook): ensure `maven("https://jitpack.io")` is present in `settings.gradle.kts` under both `pluginManagement.repositories` and `dependencyResolutionManagement.repositories`. If 401 persists, the artifact may be private or rate-limited — vendor the JAR into `libs/`.

- Room/other not found: verify `google()` and `mavenCentral()` present (they are added in `settings.gradle.kts`). Run `--refresh-dependencies` to force re-download.

- api-82.jar path errors: do not rely on absolute or brittle relative paths. Place `api-82.jar` in the module `libs/` or root `libs/`. Our guarded resolver looks in `module/libs`, `../Libs`, and `${root}/libs`.

- Duplicate classes (annotations): root `build.gradle.kts` contains `resolutionStrategy` that forces a single `org.jetbrains:annotations` version. If duplicates persist, adjust the forced artifact to the version your toolchain requires.

- Gradle Kotlin DSL script compile errors: run `.
./gradlew help --console=plain` to reveal script compilation issues. Fix ambiguous `java.io` usage by using `project.file(...)` and explicit `File` typing in build scripts.

---

## Testing & CI

- Unit tests: `.
./gradlew test` (JVM tests)
- Instrumentation/UI tests: `.
./gradlew connectedAndroidTest` (requires device/emulator)
- Functional test infra: `jvm-test` contains verification tests — do not remove.

For CI pipelines, ensure `--no-daemon --stacktrace --console=plain` flags and cache Gradle dependencies between runs.

---

## Contributing & workflow

Branching
- Target branch: `Alpha` (no force-push to Alpha)
- Feature branches: `feature/<short-name>`
- Hotfixes: `hotfix/<issue-number>`

Commit & PR checklist
- Run `.
./gradlew assembleDebug` and `.
./gradlew test` locally
- Ensure no build artifacts are staged (`git add -A` to include deletions)
- Use version-catalog aliases, prefer KSP and keep module naming conventions
- PR body: include build logs and testing summary

Suggested commit message for the recent refactor (already used):

```
♻️ refactor: Overhaul Gradle configuration and dependency management

- Move to version catalog for shared versions
- Add jitpack to pluginManagement and dependencyResolution
- Switch to KSP for Room and Hilt
- Add guarded local JAR lookup for Xposed/Yukihook artifacts
- Force single annotations artifact to prevent duplicate-class errors
```

---

## Contact & further reading

- Repo: https://github.com/AuraFrameFx/A.u.r.a.K.a.i_Reactive-Intelligence-
- Internal docs: `context/` and `DEVELOPMENT.md` in the repo

---

*This README was generated/updated on Nov 3, 2025 after a comprehensive repository review.*

If you'd like a short 'quick-start' README variant or a printable version, tell me where you'd like it and I'll add it.
