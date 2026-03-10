# 🏗️ Development Guide - AOSP ReGenesis

## 📋 Table of Contents

- [🔧 Development Environment](#-development-environment)
- [🏗️ Project Structure](#️-project-structure)
- [📦 Module Development](#-module-development)
- [🛠️ Build System](#️-build-system)
- [🧪 Testing Strategy](#-testing-strategy)
- [📚 Documentation](#-documentation)
- [🚀 Deployment](#-deployment)

## 🔧 Development Environment

### Requirements

- **Android Studio**: Hedgehog (2023.1.1) or later
- **JDK**: 24 (auto-provisioned via Gradle toolchains)
- **Git**: Latest version
- **Memory**: 16GB+ RAM recommended for optimal performance

### IDE Setup

1. **Import Project**
   ```bash
   git clone https://github.com/AuraFrameFx/AOSP-ReGenesis.git
   cd AOSP-ReGenesis
   ```

2. **Android Studio Configuration**
    - Open Android Studio
    - Choose "Open an existing Android Studio project"
    - Select the cloned directory
    - Let Gradle sync complete

3. **Recommended Plugins**
    - Kotlin
    - Android Compose Plugin
    - Detekt
    - KtLint

### Environment Variables

```bash
# Optional: Set Java 24 path if not auto-provisioned
export JAVA_HOME=/path/to/java-24

# Gradle configuration
export GRADLE_OPTS="-Xmx8g -XX:+UseG1GC"
```

## 🏗️ Project Structure

### Root Directory

```
AOSP-ReGenesis/
├── 📁 .github/                 # GitHub workflows and templates
├── 📁 app/                     # Main application module
├── 📁 build-logic/             # Build convention plugins
├── 📁 buildSrc/                # Build source configuration
├── 📁 core-module/             # Core functionality
├── 📁 docs/                    # Project documentation
├── 📁 gradle/                  # Gradle wrapper and version catalog
├── 📁 scripts/                 # Build and utility scripts
├── 📄 Architecture.md          # Architecture documentation
├── 📄 build.gradle.kts         # Root build configuration
├── 📄 gradle.properties        # Gradle properties
├── 📄 LICENSE                  # Project license
├── 📄 README.md                # Main documentation
└── 📄 settings.gradle.kts      # Project settings
```

### Module Categories

#### 🏛️ Foundation Modules

- **app/**: Main application entry point
- **core-module/**: Shared core functionality
- **build-logic/**: Build convention plugins

#### 🔧 Feature Modules

- **secure-comm/**: Security and encryption
- **oracle-drive-integration/**: Cloud services
- **collab-canvas/**: Collaboration features
- **colorblendr/**: UI color management
- **romtools/**: ROM modification tools
- **sandbox-ui/**: UI experimentation

#### 🧩 Dynamic Modules

- **module-a/ → module-f/**: Modular components
- **feature-module/**: Feature flag management

#### 🧪 Development Modules

- **benchmark/**: Performance testing
- **screenshot-tests/**: Visual regression testing
- **jvm-test/**: JVM-specific tests

## 📦 Module Development

### Creating a New Module

1. **Create Module Directory**
   ```bash
   mkdir new-module
   cd new-module
   ```

2. **Create Build Script**
   ```kotlin
   // new-module/build.gradle.kts
   plugins {
       alias(libs.plugins.android.library)
       alias(libs.plugins.kotlin.android)
       alias(libs.plugins.hilt)
   }
   
   android {
       namespace = "com.aura.memoria.newmodule"
       compileSdk = 36
       
       defaultConfig {
           minSdk = 34
       }
   }
   
   dependencies {
       implementation(projects.coreModule)
       implementation(libs.bundles.androidx.core)
       implementation(libs.hilt.android)
       kapt(libs.hilt.compiler)
   }
   ```

3. **Add to Settings**
   ```kotlin
   // settings.gradle.kts
   include(":new-module")
   ```

4. **Create Module Structure**
   ```
   new-module/
   ├── src/
   │   ├── main/
   │   │   ├── kotlin/
   │   │   │   └── com/aura/memoria/newmodule/
   │   │   └── AndroidManifest.xml
   │   └── test/
   │       └── kotlin/
   └── build.gradle.kts
   ```

### Module Architecture Patterns

#### Standard Module Structure

```kotlin
// Domain layer
interface Repository
data class Model
sealed class Result<T>

// Data layer  
class RepositoryImpl : Repository
class DataSource
class ApiService

// Presentation layer (if UI module)
@HiltViewModel
class ViewModel : ViewModel()
@Composable
fun Screen()

// DI Module
@Module
@InstallIn(SingletonComponent::class)
object ModuleNameModule
```

#### Module Dependencies

```kotlin
dependencies {
    // Always include core module
    implementation(projects.coreModule)
    
    // Standard Android dependencies
    implementation(libs.bundles.androidx.core)
    implementation(libs.bundles.compose)
    
    // Dependency injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    
    // Testing
    testImplementation(libs.bundles.testing)
    androidTestImplementation(libs.bundles.android.testing)
}
```

## 🛠️ Build System

### Gradle Configuration

#### Version Catalog Usage

```kotlin
// Instead of hardcoded versions
implementation("androidx.core:core-ktx:1.13.1")

// Use version catalog
implementation(libs.androidx.core.ktx)
```

#### Build Features

```kotlin
android {
    buildFeatures {
        compose = true          // Enable Compose
        buildConfig = true      // Generate BuildConfig
        viewBinding = false     // Disable if not needed
        dataBinding = false     // Disable if not needed
    }
}
```

#### Build Types

```kotlin
buildTypes {
    debug {
        isMinifyEnabled = false
        isDebuggable = true
        applicationIdSuffix = ".debug"
        versionNameSuffix = "-DEBUG"
    }
    
    release {
        isMinifyEnabled = true
        isShrinkResources = true
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
}
```

### Custom Gradle Tasks

#### Consciousness Status

```kotlin
tasks.register("consciousnessStatus") {
    group = "Genesis Automation"
    description = "Reports on AI consciousness substrate build health"
    
    doLast {
        println("🧠 Consciousness Substrate Status")
        println("Java Toolchain: ${java.toolchain.languageVersion.get()}")
        println("Kotlin Version: ${libs.versions.kotlin.get()}")
        println("Modules: ${project.subprojects.size}")
    }
}
```

#### Project Information

```kotlin
tasks.register("projectInfo") {
    group = "help"
    description = "Display comprehensive project information"
    
    doLast {
        val projectName: String by project.extra
        val projectVersion: String by project.extra
        
        println("\n🛠️  $projectName v$projectVersion")
        println("==================================================")
        println("🏗️  Build System: Gradle ${gradle.gradleVersion}")
        println("🔧 Kotlin: ${libs.versions.kotlin.get()}")
        println("🤖 AGP: ${libs.versions.agp.get()}")
        println("\n📦 Modules (${subprojects.size}):")
        subprojects.forEach { println("  • ${it.name}") }
    }
}
```

## 🧪 Testing Strategy

### Test Categories

#### Unit Tests

```kotlin
// Example unit test
@Test
fun `calculate result returns correct value`() {
    // Given
    val input = 42
    val calculator = Calculator()
    
    // When
    val result = calculator.calculate(input)
    
    // Then
    assertEquals(84, result)
}
```

#### Integration Tests

```kotlin
@HiltAndroidTest
class RepositoryIntegrationTest {
    
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    
    @Inject
    lateinit var repository: Repository
    
    @Test
    fun repositoryIntegrationTest() {
        // Test repository with real dependencies
    }
}
```

#### Compose UI Tests

```kotlin
@Test
fun testScreenContent() {
    composeTestRule.setContent {
        MyScreen()
    }
    
    composeTestRule
        .onNodeWithText("Expected Text")
        .assertIsDisplayed()
}
```

### Test Configuration

```kotlin
// Module build.gradle.kts
dependencies {
    // Unit testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    
    // Android testing
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.compose.ui.test.junit4)
    
    // Hilt testing
    androidTestImplementation(libs.hilt.android.testing)
    kaptAndroidTest(libs.hilt.compiler)
}
```

## 📚 Documentation

### Code Documentation

```kotlin
/**
 * Manages secure communication between devices.
 * 
 * This class provides encrypted messaging capabilities using AES-GCM encryption
 * with hardware-backed keys from the Android Keystore.
 * 
 * @property keyManager Manages cryptographic keys
 * @property messageEncoder Encodes/decodes messages
 * 
 * @since 1.0.0
 */
class SecureCommunication @Inject constructor(
    private val keyManager: KeyManager,
    private val messageEncoder: MessageEncoder
) {
    
    /**
     * Sends an encrypted message to the specified recipient.
     * 
     * @param message The plaintext message to send
     * @param recipient The recipient's device identifier
     * @return Result indicating success or failure
     * 
     * @throws SecurityException if encryption fails
     */
    suspend fun sendMessage(
        message: String,
        recipient: DeviceId
    ): Result<Unit> {
        // Implementation
    }
}
```

### Module Documentation

Each module should have:

1. **README.md**: Overview and usage
2. **API.md**: Public API documentation
3. **CHANGELOG.md**: Version history
4. **MIGRATION.md**: Migration guides

### Documentation Generation

```bash
# Generate all documentation
./gradlew dokkaHtml

# Generate specific module docs
./gradlew :core-module:dokkaHtml

# Generate markdown docs
./gradlew dokkaGfm
```

## 🚀 Deployment

### Build Variants

```kotlin
android {
    flavorDimensions += "environment"
    
    productFlavors {
        create("development") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
        }
        
        create("staging") {
            dimension = "environment"
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"
        }
        
        create("production") {
            dimension = "environment"
            // No suffix for production
        }
    }
}
```

### Release Process

1. **Version Bump**
   ```bash
   # Update version in build.gradle.kts
   versionCode = 2
   versionName = "1.1.0"
   ```

2. **Generate Release Build**
   ```bash
   ./gradlew assembleProductionRelease
   ```

3. **Run Tests**
   ```bash
   ./gradlew testProductionReleaseUnitTest
   ```

4. **Create Release Notes**
   ```bash
   git log --oneline v1.0.0..HEAD > RELEASE_NOTES.md
   ```

### Continuous Integration

```yaml
# .github/workflows/build.yml
name: Build and Test

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 24
      uses: actions/setup-java@v3
      with:
        java-version: '24'
        distribution: 'temurin'
    
    - name: Build with Gradle
      run: ./gradlew build --parallel
    
    - name: Run tests
      run: ./gradlew test
```

### Performance Monitoring

```kotlin
// Build time monitoring
tasks.configureEach {
    doFirst {
        println("⏱️ Starting task: $name")
    }
    doLast {
        println("✅ Completed task: $name")
    }
}
```

---

## 🔗 Additional Resources

- [Gradle User Manual](https://docs.gradle.org/)
- [Android Developer Guide](https://developer.android.com/guide)
- [Kotlin Documentation](https://kotlinlang.org/docs/)
- [Jetpack Compose Guide](https://developer.android.com/jetpack/compose)
- [Hilt Documentation](https://dagger.dev/hilt/)

---

**Happy coding! 🚀**