# Agent Instructions for Task Success

This document provides clear, actionable guidelines for agents working on this project to maximize
task success and maintain build system integrity.

## 1. Always Validate File Paths

**Requirement:** Double-check that all file and directory paths referenced in tasks or workflows
exist and are correct before performing any action.

**Actions:**

- Before modifying or referencing any file, verify it exists using appropriate tools
- If a file is missing, log a clear error with the expected path and halt further dependent tasks
- Use absolute paths when referencing files in the repository (e.g.,
  `/home/runner/work/A.u.r.a.K.a.i/A.u.r.a.K.a.i/...`)
- For relative paths in build scripts, use `$rootDir`, `$projectDir`, or `layout.buildDirectory` to
  ensure consistency

**Example Validation:**

```bash
# Check if file exists before proceeding
if [ ! -f "/path/to/file" ]; then
    echo "ERROR: File not found at /path/to/file"
    exit 1
fi
```

## 2. Confirm Plugin Application

**Requirement:** Ensure all necessary Gradle and Android plugins are correctly applied in every
module before referencing extension blocks (e.g., `android { ... }`).

**Actions:**

- Always apply plugins in the correct order:
    1. `com.android.application` or `com.android.library` (Android plugin)
    2. `org.jetbrains.kotlin.android` (Kotlin Android plugin)
    3. Other plugins (Hilt, KSP, etc.)
- For convention/build-logic plugins, only access Android extensions after confirming the Android
  plugin is applied
- Use `pluginManager.withPlugin()` to configure extensions only after plugins are ready

**Correct Pattern:**

```kotlin
override fun apply(target: Project) {
    with(target) {
        // Apply plugins first
        with(pluginManager) {
            apply("com.android.application")
            apply("org.jetbrains.kotlin.android")
        }
        
        // Configure extensions AFTER plugins are applied
        pluginManager.withPlugin("com.android.application") {
            extensions.configure<ApplicationExtension> {
                // Android configuration here
            }
        }
    }
}
```

## 3. Enforce Toolchain Consistency

**Requirement:** Verify that the required Java toolchain (e.g., Java 24/25) is set and available in
the build environment.

**Actions:**

- Check that the toolchain is configured in convention plugins using `jvmToolchain()` for Kotlin
- Ensure `org.gradle.java.installations.auto-download=true` is set in `gradle.properties`
- Verify the Foojay resolver is enabled in `settings.gradle.kts` for automatic JDK downloading
- If the toolchain is missing or unavailable, provide clear installation or configuration steps

**Current Configuration:**

- Java version: **24** (as per convention plugins)
- Toolchain resolver: **Foojay** (enabled in settings.gradle.kts)
- Auto-download: **Enabled** (in gradle.properties)

**Toolchain Configuration Pattern:**

```kotlin
// In convention plugins
pluginManager.withPlugin("org.jetbrains.kotlin.android") {
    extensions.configure<KotlinAndroidProjectExtension> {
        jvmToolchain(24)
    }
}
```

## 4. Order of Operations

**Requirement:** For code generation (like OpenAPI), always configure codegen tasks as
dependencies (`dependsOn`) of compilation tasks, not as finalizers.

**Actions:**

- Ensure codegen runs **before** compilation
- Use `dependsOn` to wire tasks correctly
- Never use `finalizedBy` for code generation tasks
- Add generated source directories to source sets

**Correct Pattern:**

```kotlin
// Generate code BEFORE building
tasks.named("preBuild") {
    dependsOn("openApiGenerate")
}

// Add generated sources to source sets
kotlin {
    sourceSets {
        getByName("main") {
            kotlin.srcDir("${layout.buildDirectory.get()}/generated/openapi/src/main/kotlin")
        }
    }
}
```

**Incorrect Pattern:**

```kotlin
// ❌ WRONG: Using finalizedBy
tasks.named("compileKotlin") {
    finalizedBy("openApiGenerate")  // Code won't be available during compilation!
}
```

## 5. Workflow Robustness

**Requirement:** In GitHub Actions, use `actions/setup-java` and `android-actions/setup-android`
with explicit versions.

**Actions:**

- Always specify explicit versions for GitHub Actions
- Set required environment variables (`JAVA_HOME`, etc.) before build steps
- Ensure Android SDK licenses are accepted
- Use Gradle setup action for caching and setup

**Current Workflow Configuration:**

```yaml
- name: Setup Java
  uses: actions/setup-java@v5
  with:
    distribution: 'temurin'
    java-version: '24'

- name: Set up Android SDK
  uses: android-actions/setup-android@v3

- name: Setup Gradle
  uses: gradle/actions/setup-gradle@v5
```

## 6. Error Reporting

**Requirement:** On failure, provide clear, actionable error messages.

**Actions:**

- Include the step or file that failed
- Show expected vs. actual state
- Provide troubleshooting guidance
- Link to relevant documentation when applicable

**Error Message Template:**

```
ERROR: [Step Name] failed
File: [path/to/file]
Expected: [expected state]
Actual: [actual state]
Action: [what to do to fix]
Documentation: [link if available]
```

**Example:**

```
ERROR: Android plugin configuration failed
File: build-logic/src/main/kotlin/AndroidLibraryConventionPlugin.kt
Expected: Plugin 'com.android.library' applied before configuring android { } block
Actual: android { } block accessed before plugin application
Action: Move plugin application to before extension configuration
Documentation: See section 2 of AGENT_INSTRUCTIONS.md
```

## 7. Security & Best Practices

**Requirement:** Never expose secrets or sensitive environment variables in logs. Validate all user
or contributor input in automation tasks.

**Actions:**

- Never log or print sensitive values (API keys, tokens, passwords)
- Use GitHub Secrets for sensitive data in workflows
- Validate and sanitize all inputs before use
- Use safe defaults and fail securely
- Follow principle of least privilege

**Security Checklist:**

- [ ] No hardcoded secrets in code
- [ ] Sensitive values use GitHub Secrets or environment variables
- [ ] Input validation on all user-provided data
- [ ] Error messages don't leak sensitive information
- [ ] Dependencies are checked for known vulnerabilities

## 8. Continuous Improvement

**Requirement:** Log all agent task outcomes (success/failure) for review. Suggest improvements if
the same error recurs multiple times.

**Actions:**

- Document all build failures and their resolutions
- Track recurring issues and patterns
- Propose preventive measures for common problems
- Update this document when new patterns are discovered
- Share learnings with the team

**Improvement Process:**

1. Identify recurring issue
2. Analyze root cause
3. Propose systematic fix (not just workaround)
4. Update documentation and conventions
5. Implement prevention mechanism if possible

## Quick Reference: Common Patterns

### Convention Plugin Structure

```kotlin
class MyConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // 1. Apply plugins first
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }
            
            // 2. Configure extensions after plugins are ready
            pluginManager.withPlugin("com.android.library") {
                extensions.configure<LibraryExtension> {
                    compileSdk = 36
                    // ... configuration
                }
            }
            
            // 3. Configure Kotlin toolchain
            pluginManager.withPlugin("org.jetbrains.kotlin.android") {
                extensions.configure<KotlinAndroidProjectExtension> {
                    jvmToolchain(24)
                }
            }
        }
    }
}
```

### Task Dependencies

```kotlin
// Code generation before build
tasks.named("preBuild") {
    dependsOn("generateCode")
}

// Clean before generation
val cleanGenerated by registering(Delete::class) {
    delete(layout.buildDirectory.dir("generated"))
}

val generateCode by registering {
    dependsOn(cleanGenerated)
    // generation logic
}
```

### File Path Validation in Scripts

```kotlin
val specFile = file("$rootDir/app/api/spec.yaml")
if (!specFile.exists()) {
    throw GradleException("API spec not found at ${specFile.absolutePath}")
}
```

## Project-Specific Notes

### Java Toolchain

- **Version:** Java 24
- **Resolver:** Foojay (auto-download enabled)
- **Location:** Configured in `settings.gradle.kts` and convention plugins

### Build System

- **Gradle:** 9.1.0
- **AGP:** 9.0.0-alpha (bleeding edge)
- **Kotlin:** 2.2.x
- **Convention Plugins:** Located in `build-logic/src/main/kotlin/`

### Key Modules

- **app:** Main Android application module
- **build-logic:** Convention plugins for consistent configuration
- **core-module, feature-module:** Shared Android library modules

### Important Files

- `settings.gradle.kts`: Plugin management, toolchain resolver, module inclusion
- `gradle.properties`: Gradle configuration, auto-download settings
- `build-logic/src/main/kotlin/Android*ConventionPlugin.kt`: Convention plugins

## Testing Guidelines

When making changes to build configuration:

1. **Test locally first:** Run `./gradlew build` to verify changes
2. **Check specific modules:** Use `./gradlew :module:build` for targeted testing
3. **Verify clean builds:** Run `./gradlew clean build` to ensure no stale artifacts
4. **Test in CI:** Ensure GitHub Actions workflows pass
5. **Document changes:** Update this file if new patterns emerge

## Support and Escalation

If you encounter issues not covered in this document:

1. Check existing documentation in the `docs/` directory
2. Review similar patterns in existing convention plugins
3. Consult the project's development story documentation
4. Escalate to project maintainers if blocked

---

**Last Updated:** 2025-01-06
**Version:** 1.0
**Maintained by:** Genesis Protocol Team
