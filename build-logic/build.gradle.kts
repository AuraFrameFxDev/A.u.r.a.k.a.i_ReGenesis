plugins {
    `kotlin-dsl`
}

// ═══════════════════════════════════════════════════════════════════════════
// Prevent Android leakage into JVM-only build-logic
// ═══════════════════════════════════════════════════════════════════════════
configurations.all {
    exclude(group = "com.google.dagger", module = "hilt-android")
    exclude(group = "androidx.activity")
    exclude(group = "androidx.fragment")
    exclude(group = "androidx.lifecycle")
    exclude(group = "androidx.savedstate")
    exclude(group = "androidx.annotation")
    exclude(group = "androidx.core")
}

gradlePlugin {
    plugins {
        register("genesisApplication") {
            id = "genesis.android.application"
            implementationClass = "GenesisApplicationPlugin"
        }
        register("genesisLibrary") {
            id = "genesis.android.library"
            implementationClass = "GenesisLibraryPlugin"
        }
        register("genesisLibraryHilt") {
            id = "genesis.android.library.hilt"
            implementationClass = "GenesisLibraryHiltPlugin"
        }
    }
}

repositories {
    google()
    mavenCentral()
}

dependencies {

    compileOnly("com.android.tools.build:gradle:9.3.0-alpha01")
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.20")
}
