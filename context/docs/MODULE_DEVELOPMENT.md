# Module Development Guide

## Creating a New Module

### 1. Module Types
- **Library Module**: Reusable components
  ```bash
  ./gradlew createModule --name=mymodule --type=library
  ```
- **Feature Module**: App feature
  ```bash
  ./gradlew createModule --name=feature --type=feature
  ```

### 2. Module Structure
```
:module-name/
├── src/
│   ├── main/
│   │   ├── kotlin/     # Kotlin source files
│   │   ├── resources/  # Resources (XML, assets, etc.)
│   │   └── AndroidManifest.xml
│   └── test/           # Unit tests
│   └── androidTest/    # Instrumentation tests
└── build.gradle.kts    # Module build file
```

### 3. Dependencies

#### Adding Dependencies
```kotlin
// In module's build.gradle.kts
dependencies {
    // Core AndroidX
    implementation(libs.androidx.core.ktx)

    // Feature-specific dependencies
    implementation(libs.some.library)

    // Module dependencies
    implementation(project(":core:common"))
}
```

#### API vs Implementation
- `api`: Expose dependency to consumers
- `implementation`: Keep dependency internal

## Testing

### Unit Tests
```kotlin
class MyTest {
    @Test
    fun testSomething() {
        // Test code
    }
}
```

### Instrumentation Tests
```kotlin
@RunWith(AndroidJUnit4::class)
class MyInstrumentedTest {
    @Test
    fun useAppContext() {
        // Test code
    }
}
```

## Publishing

### Local Maven Repository
```bash
./gradlew :mymodule:publishToMavenLocal
```

### Remote Repository
Configure in `gradle.properties`:
```properties
RELEASE_REPOSITORY_URL=your-repo-url
SNAPSHOT_REPOSITORY_URL=your-snapshot-repo-url
```

## Best Practices

### Code Organization
- Follow clean architecture principles
- Use feature packages, not layer packages
- Keep public API surface minimal

### Documentation
- Document public APIs with KDoc
- Include module-level README
- Document any required setup

### Versioning
- Follow semantic versioning
- Update CHANGELOG.md with changes
- Tag releases in git

## Module Templates

### Library Module Template
```kotlin
plugins {
    id("genesis.android.library")
    id("genesis.android.hilt")
}

android {
    namespace = "com.aurakaifx.feature.mymodule"
}

dependencies {
    // Dependencies here
}
```

### Feature Module Template
```kotlin
plugins {
    id("genesis.android.feature")
    id("genesis.android.hilt")
}

android {
    namespace = "com.aurakaifx.feature.myfeature"

    buildFeatures {
        compose = true
    }
}

dependencies {
    // Feature dependencies
    implementation(project(":core:ui"))
    implementation(project(":core:navigation"))
}
```

## Performance Considerations

### ProGuard/R8 Rules
- Keep rules in `proguard-rules.pro`
- Test thoroughly with release builds

### Resource Shrinking
- Enable in release builds
- Use `shrinkResources true`
- Keep necessary resources with `keep.xml`

### Native Libraries
- Use `android.defaultConfig.ndk.debugSymbolLevel = 'FULL'` for debugging
- Strip debug symbols in release builds
