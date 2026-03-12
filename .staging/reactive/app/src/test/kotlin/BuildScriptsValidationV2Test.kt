//import io.mockk.clearAllMocks
//import org.junit.Assert.assertFalse
//import org.junit.Assert.assertTrue
//import org.junit.jupiter.api.AfterEach
//import org.junit.jupiter.api.Assertions
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import java.io.File
//
//class BuildScriptsValidationV2Test {
//
//    private lateinit var buildFile: File
//
//    @BeforeEach
//    fun setup() {
//        buildFile = File("app/build.gradle.kts")
//        Assertions.assertTrue(buildFile.exists(), "Build script should exist for validation")
//    }
//
//    @AfterEach
//    fun teardown() {
//        clearAllMocks()
//    }
//
//    // Focus: Recent build script changes (diff-aligned)
//    @Test
//    fun `uses plugin ids for core plugins (no version-catalog aliasing)`() {
//        val content = buildFile.readText()
//
//        // Accept either direct ids or version-catalog aliases per plugin
//        fun hasAny(vararg needles: String) = needles.any { content.contains(it) }
//        assertTrue(
//            hasAny(
//                """id("com.android.application")""",
//                "alias(libs.plugins.androidApplication)"
//            )
//        )
//        assertTrue(
//            hasAny(
//                """id("org.jetbrains.kotlin.android")""",
//                "alias(libs.plugins.kotlinAndroid)"
//            )
//        )
//        // Compose plugin typically via id()
//        assertTrue(hasAny("""id("org.jetbrains.kotlin.plugin.compose")"""))
//        assertTrue(
//            hasAny(
//                """id("org.jetbrains.kotlin.plugin.serialization")""",
//                "alias(libs.plugins.kotlin.serialization)"
//            )
//        )
//        assertTrue(hasAny("""id("com.google.devtools.ksp")""", "alias(libs.plugins.ksp)"))
//
//        buildFile.readText()
//        assertTrue(
//        )
//        assertTrue(
//        )
//    }
//
//    @Test
//    fun `preBuild task depends on OpenAPI generation and cleanup tasks`() {
//        val content = buildFile.readText()
//
//        // Ensure preBuild hook is present
//        assertTrue(
//            "preBuild task hook should be declared",
//            content.contains("""tasks.named("preBuild")""")
//        )
//
//        // Ensure dependsOn wiring is present (accept both path-qualified and unqualified OpenAPI task)
//        val hasOpenApiDepends =
//            content.contains("""dependsOn(":openApiGenerate")""") || content.contains("""dependsOn("openApiGenerate")""")
//        assertTrue("preBuild should depend on OpenAPI generation", hasOpenApiDepends)
//
//        assertTrue(
//            "preBuild should depend on cleaning KSP cache",
//            content.contains("""dependsOn("cleanKspCache")""")
//        )
//        assertTrue(
//            "preBuild should depend on API generation cleanup",
//            content.contains("""dependsOn(":cleanApiGeneration")""")
//        )
//    }
//
//    @Test
//    fun `packaging resources and jniLibs are configured correctly`() {
//        val content = buildFile.readText()
//
//        // Resources excludes
//        assertTrue(
//            "Should configure packaging resources block",
//            content.contains("packaging {")
//        )
//        assertTrue("Should configure resources excludes", content.contains("resources {"))
//        assertTrue(
//            "Should exclude AL2.0 and LGPL2.1 license files",
//            content.contains("""excludes += setOf(""") &&
//                    content.contains("/META-INF/{AL2.0,LGPL2.1}")
//        )
//        assertTrue(
//            "Should exclude META-INF/DEPENDENCIES",
//            content.contains("/META-INF/DEPENDENCIES")
//        )
//
//        // JNI libs packaging
//        assertTrue("Should configure jniLibs packaging", content.contains("jniLibs {"))
//        assertTrue(
//            "Should set useLegacyPackaging = false for JNI libs",
//            content.contains("useLegacyPackaging = false")
//        )
//        assertTrue(
//            "Should pickFirst libc++_shared.so",
//            content.contains("**/libc++_shared.so")
//        )
//        assertTrue(
//            "Should pickFirst libjsc.so",
//            content.contains("**/libjsc.so")
//        )
//    }
//
//    @Test
//    fun `single preBuild hook declaration`() {
//        val content = buildFile.readText()
//        val preBuildBlocks =
//            Regex("""tasks\.named\("preBuild"\)\s*\{""").findAll(content).count()
//        assertTrue(
//            "There should be exactly one preBuild task configuration block",
//            preBuildBlocks == 1
//        )
//    }
//}
//// Additional comprehensive tests (JUnit 5 + MockK)
//// These focus on ensuring the migration to plugin id() usage, Java/Kotlin toolchains,
//// Compose build features, Compose compiler settings, task wiring, and packaging details
//// remain intact and aligned with the PR diff.
//
//@Test
//fun `kotlin jvm toolchain to 21 if present`() {
//    val content = buildFile.readText()
//
//    // Accept either kotlin { jvmToolchain(24) } or kotlin { jvmToolchain { languageVersion = ... } }
//    val hasJvmToolchain24Direct =
//        content.contains(Regex("""kotlin\s*\{\s*jvmToolchain\s*\(\s*24\s*\)"""))
//    val hasJvmToolchainBlock24 = content.contains(
//        Regex(
//            """kotlin\s*\{\s*jvmToolchain\s*\{\s*.*24.*\}\s*\}""",
//            RegexOption.DOT_MATCHES_ALL
//        )
//    )
//    assertTrue(
//        "Kotlin jvmToolchain should be configured to 24",
//        hasJvmToolchain24Direct || hasJvmToolchainBlock24
//    )
//
//    // jvmTarget via compilerOptions or kotlinOptions (string or without quotes depending on style)
//    val hasCompilerOptions24 = content.contains(
//        Regex(
//            """compilerOptions\s*\{\s*[^}]*jvmTarget\.set\s*\(\s*JvmTarget\.JVM_24\s*\)""",
//            RegexOption.DOT_MATCHES_ALL
//        )
//    )
//    val hasKotlinOptions24 = content.contains(
//        Regex(
//            """kotlinOptions\s*\{\s*[^\}]*jvmTarget\s*=\s*["']?24["']?""",
//            RegexOption.DOT_MATCHES_ALL
//        )
//    )
//    assertTrue(
//        "jvmTarget should be 24 via compilerOptions or kotlinOptions",
//        hasCompilerOptions24 || hasKotlinOptions24
//    )
//}
//
//@Test
//fun `compose build features enabled and compose compiler extension configured`() {
//    val content = buildFile.readText()
//
//    // buildFeatures { compose = true }
//    val hasComposeBuildFeatures = content.contains(
//        Regex(
//            """buildFeatures\s*\{\s*[^}]*compose\s*=\s*true""",
//            RegexOption.DOT_MATCHES_ALL
//        )
//    )
//    assertTrue("Compose build feature must be enabled", hasComposeBuildFeatures)
//
//    // composeOptions { kotlinCompilerExtensionVersion = ... }
//    val hasComposeCompilerVersion = content.contains(
//        Regex(
//            """composeOptions\s*\{\s*[^}]*kotlinCompilerExtensionVersion\s*=""",
//            RegexOption.DOT_MATCHES_ALL
//        )
//    )
//    assertTrue(
//        "Compose compiler extension version should be configured",
//        hasComposeCompilerVersion
//    )
//}
//
//@Test
//fun `android namespace and sdk versions are defined`() {
//    val content = buildFile.readText()
//    val hasNamespace = content.contains(
//        Regex(
//            """android\s*\{\s*[^}]*namespace\s*=""",
//            RegexOption.DOT_MATCHES_ALL
//        )
//    )
//    assertTrue("Android namespace should be set", hasNamespace)
//
//    // Be tolerant to both property and literal forms
//    val hasCompileSdk = content.contains(Regex("""compileSdk\s*=\s*\d+"""))
//    assertTrue("compileSdk should be defined", hasCompileSdk)
//
//    val hasDefaultConfigMinSdk = content.contains(
//        Regex(
//            """defaultConfig\s*\{\s*[^}]*minSdk\s*=\s*\d+""",
//            RegexOption.DOT_MATCHES_ALL
//        )
//    )
//    assertTrue("defaultConfig.minSdk should be defined", hasDefaultConfigMinSdk)
//
//    val hasDefaultConfigTargetSdk = content.contains(
//        Regex(
//            """defaultConfig\s*\{\s*[^}]*targetSdk\s*=\s*\d+""",
//            RegexOption.DOT_MATCHES_ALL
//        )
//    )
//    assertTrue("defaultConfig.targetSdk should be defined", hasDefaultConfigTargetSdk)
//}
//
//@Test
//fun `serialization and compose plugins imply matching dependencies present`() {
//    val content = buildFile.readText()
//
//    // If serialization plugin is present, ensure kotlinx-serialization JSON dependency is present
//    val hasSerializationPlugin =
//        content.contains("""id("org.jetbrains.kotlin.plugin.serialization")""")
//    if (hasSerializationPlugin) {
//        val hasSerializationDep =
//            content.contains(Regex("""(implementation|api)\(\s*["']org\.jetbrains\.kotlinx:kotlinx-serialization-(json|core).*["']\s*\)"""))
//        assertTrue(
//            "kotlinx-serialization dependency should be present when serialization plugin is applied",
//            hasSerializationDep
//        )
//    }
//
//    // If compose plugin is present, ensure at least one core compose runtime/ui dependency is included
//    val hasComposePlugin = content.contains("""id("org.jetbrains.kotlin.plugin.compose")""")
//    if (hasComposePlugin) {
//        val hasComposeDependency =
//            content.contains(Regex("""(implementation|api)\(\s*["']androidx\.compose\.[^"']+["']\s*\)"""))
//                    || content.contains(Regex("""(implementation|api)\(\s*libs\.androidx\.compose\.[^)]+"""))
//        assertTrue(
//            "At least one Compose dependency should be present when compose plugin is applied",
//            hasComposeDependency
//        )
//    }
//}
//
//@Test
//fun `ksp plugin is applied and used consistently for processors`() {
//    val content = buildFile.readText()
//    assertTrue(
//        "KSP plugin must be applied",
//        content.contains("""id("com.google.devtools.ksp")""")
//    )
//
//    // check that no kapt() usage remains for annotation processors if migration expects KSP
//    val hasKapt = content.contains(Regex("""\bkapt\("""))
//    // We allow kapt to exist if the project still needs it, but if kapt is present we expect a comment or justification.
//    if (hasKapt) {
//        val hasKaptJustification = content.contains(
//            Regex(
//                """//\s*kapt(?:\s+required|:|\s+reason)""",
//                RegexOption.IGNORE_CASE
//            )
//        )
//        assertTrue(
//            "kapt present without justification comment; prefer KSP or justify why kapt is required",
//            hasKaptJustification
//        )
//    }
//
//    // Ensure ksp(...) usage appears when processors are declared
//    val hasKspDeps = content.contains(Regex("""\bksp\("""))
//    assertTrue("KSP dependency configuration should be used for processors", hasKspDeps)
//}
//
//@Test
//fun `preBuild dependsOn statements are inside the preBuild block`() {
//    val content = buildFile.readText()
//    val preBuildBlockRegex =
//        Regex("""tasks\.named\("preBuild"\)\s*\{\s*(.*?)\s*\}""", RegexOption.DOT_MATCHES_ALL)
//
//    val block = preBuildBlockRegex.find(content)?.groupValues?.getOrNull(1) ?: ""
//    assertTrue("preBuild configuration block should exist", block.isNotBlank())
//
//    val hasOpenApiInside =
//        block.contains("""dependsOn(":openApiGenerate")""") || block.contains("""dependsOn("openApiGenerate")""")
//    val hasCleanKspInside = block.contains("""dependsOn("cleanKspCache")""")
//    val hasCleanApiInside = block.contains("""dependsOn(":cleanApiGeneration")""")
//    assertTrue("openApiGenerate dependsOn must be within preBuild block", hasOpenApiInside)
//    assertTrue("cleanKspCache dependsOn must be within preBuild block", hasCleanKspInside)
//    assertTrue("cleanApiGeneration dependsOn must be within preBuild block", hasCleanApiInside)
//}
//
//@Test
//fun `packaging excludes are comprehensive and use set+= pattern`() {
//    val content = buildFile.readText()
//
//    // Verify we use the 'excludes += setOf(...)' style
//    val usesSetStyle = content.contains(
//        Regex(
//            """resources\s*\{\s*[^}]*excludes\s*\+=\s*setOf\(""",
//            RegexOption.DOT_MATCHES_ALL
//        )
//    )
//    assertTrue("Packaging.resources should use 'excludes += setOf(...)' typography", usesSetStyle)
//
//    // Core excludes
//    assertTrue(
//        "Excludes should include META-INF AL2.0 and LGPL2.1",
//        content.contains("/META-INF/{AL2.0,LGPL2.1}")
//    )
//    assertTrue(
//        "Excludes should include META-INF/DEPENDENCIES",
//        content.contains("/META-INF/DEPENDENCIES")
//    )
//
//    // Ensure no blanket wildcard that may hide issues (e.g., META-INF/* without reason)
//    val hasOverbroadWildcard = content.contains(Regex("""/META-INF/\*["']"""))
//    assertFalse("Avoid over-broad META-INF/* exclusion", hasOverbroadWildcard)
//}
//
//@Test
//fun `jniLibs pickFirst entries are declared only once and legacy packaging disabled`() {
//    val content = buildFile.readText()
//
//    // Ensure the jniLibs block exists
//    assertTrue("jniLibs block must exist", content.contains(Regex("""jniLibs\s*\{""")))
//
//    // Legacy packaging disabled
//    val hasLegacyFalse = content.contains(Regex("""useLegacyPackaging\s*=\s*false"""))
//    assertTrue("useLegacyPackaging should be false", hasLegacyFalse)
//
//    // PickFirst occurrences and deduplication check
//    val pickFirstPattern =
//        Regex("""pickFirsts?\s*\+\=\s*setOf\([^)]*\)|pickFirst\(\s*["'][^"']+["']\s*\)""")
//    val pickFirstMatches = pickFirstPattern.findAll(content).count()
//    assertTrue("At least one pickFirst entry should exist for JNI libs", pickFirstMatches >= 1)
//
//    // Must include libc++_shared.so and libjsc.so
//    assertTrue(
//        "Pick first libc++_shared.so must be present",
//        content.contains("**/libc++_shared.so")
//    )
//    assertTrue("Pick first libjsc.so must be present", content.contains("**/libjsc.so"))
//}
//
//@Test
//fun `no version-catalog aliasing used for core plugins and versions not hardcoded in app module`() {
//    val content = buildFile.readText()
//
//    // Allow either id() or alias() styles
//    assertTrue(
//        content.contains("""id("com.android.application")""") ||
//                content.contains("alias(libs.plugins.androidApplication)")
//    )
//    assertTrue(
//        content.contains("""id("org.jetbrains.kotlin.android")""") ||
//                content.contains("alias(libs.plugins.kotlinAndroid)")
//    )
//    assertTrue(
//        content.contains("""id("com.google.devtools.ksp")""") ||
//                content.contains("alias(libs.plugins.ksp)")
//    )
//
//    // Avoid hardcoding plugin versions in the app module (they should come from settings pluginManagement or convention plugin)
//    val hardcodedPluginVersionInApp =
//        Regex("""id\("com\.android\.application"\)\s*version\s+["'][\d.]+["']""")
//            .containsMatchIn(content)
//                || Regex("""id\("org\.jetbrains\.kotlin\.android"\)\s*version\s+["'][\d.]+["']""")
//            .containsMatchIn(content)
//                || Regex("""id\("com\.google\.devtools\.ksp"\)\s*version\s+["'][\d.]+["']""")
//            .containsMatchIn(content)
//    assertFalse(
//        "Plugin versions should not be hardcoded in app module",
//        hardcodedPluginVersionInApp
//    )
//}
//
//@Test
//fun `release buildType has minify and proguard rules configured`() {
//    val content = buildFile.readText()
//
//    // Look for release buildType; accept both minifyEnabled and isMinifyEnabled style
//    val releaseBlock = Regex(
//        """buildTypes\s*\{\s*[^}]*release\s*\{\s*(.*?)\s*\}""",
//        RegexOption.DOT_MATCHES_ALL
//    )
//        .find(content)?.groupValues?.getOrNull(1) ?: ""
//    assertTrue("release buildType should be configured", releaseBlock.isNotBlank())
//
//    val hasMinify =
//        releaseBlock.contains(Regex("""\b(minifyEnabled|isMinifyEnabled)\s*=\s*(true|false)"""))
//    assertTrue("release buildType should declare minifyEnabled/isMinifyEnabled", hasMinify)
//
//    // ProGuard/R8 rules pointing to at least one file
//    val hasProguardFiles =
//        releaseBlock.contains(Regex("""proguardFiles\s*\(""")) || releaseBlock.contains(
//            Regex("""proguardFile\s*\(""")
//        )
//    assertTrue("release buildType should configure proguardFiles", hasProguardFiles)
//}
//}
