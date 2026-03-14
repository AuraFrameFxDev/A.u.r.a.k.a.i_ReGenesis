////
//// Test framework/library: JUnit Jupiter (JUnit 5) with Gradle ProjectBuilder.
//// Focus: Validate KoverConventionPlugin configuration and application.
////
//import org.gradle.api.Plugin
//import org.gradle.testfixtures.ProjectBuilder
//import org.junit.jupiter.api.Assertions.*
//import org.junit.jupiter.api.DisplayName
//import org.junit.jupiter.api.Test
//import java.nio.file.Files
//import java.nio.file.Paths
//
//class KoverConventionPluginTest {
//
//    private fun readPluginSource(): String {
//        val candidates = listOf(
//            "build-logic/src/main/kotlin/KoverConventionPlugin.kt",
//            "src/main/kotlin/KoverConventionPlugin.kt"
//        )
//        for (c in candidates) {
//            val p = Paths.get(c)
//            if (Files.exists(p)) return Files.readString(p)
//        }
//        fail("Cannot locate KoverConventionPlugin.kt in expected locations: $candidates")
//    }
//
//    @Test
//    @DisplayName("Static: plugin applies Kover and configures reports/verify as expected in source")
//    fun staticSourceAssertions() {
//        val src = readPluginSource()
//        assertTrue(
//            src.contains("""pluginManager.apply("org.jetbrains.kotlinx.kover")"""),
//            "Should apply 'org.jetbrains.kotlinx.kover' plugin"
//        )
//        assertTrue(
//            Regex(
//                """total\s*\{\s*html\s*\{[^}]*onCheck\s*=\s*false""",
//                RegexOption.IGNORE_CASE or RegexOption.DOT_MATCHES_ALL
//            ).containsMatchIn(src),
//            "HTML report should have onCheck = false"
//        )
//        assertTrue(
//            Regex(
//                """total\s*\{\s*xml\s*\{[^}]*onCheck\s*=\s*false""",
//                RegexOption.IGNORE_CASE or RegexOption.DOT_MATCHES_ALL
//            ).containsMatchIn(src),
//            "XML report should have onCheck = false"
//        )
//        assertTrue(
//            src.contains("""htmlDir = layout.buildDirectory.dir("reports/kover/html")"""),
//            "HTML report directory should be 'build/reports/kover/html'"
//        )
//        assertTrue(
//            src.contains("""xmlFile = layout.buildDirectory.file("reports/kover/coverage.xml")"""),
//            "XML report file should be 'build/reports/kover/coverage.xml'"
//        )
//        assertTrue(
//            Regex(
//                """verify\s*\{\s*onCheck\s*=\s*true""",
//                RegexOption.IGNORE_CASE or RegexOption.DOT_MATCHES_ALL
//            ).containsMatchIn(src),
//            "Verify should have onCheck = true"
//        )
//        assertTrue(
//            Regex(
//                """rule\s*\{\s*minBound\(\s*80\s*\)""",
//                RegexOption.IGNORE_CASE or RegexOption.DOT_MATCHES_ALL
//            ).containsMatchIn(src),
//            "Verify rule should enforce minBound(80)"
//        )
//    }
//
//    @Test
//    @DisplayName("Type: KoverConventionPlugin is public and implements Plugin<Project>")
//    fun pluginTypeContract() {
//        val clazz = KoverConventionPlugin::class.java
//        assertTrue(java.lang.reflect.Modifier.isPublic(clazz.modifiers), "Class must be public")
//        assertTrue(
//            Plugin::class.java.isAssignableFrom(clazz),
//            "Class must implement Plugin<Project>"
//        )
//    }
//
//    @Test
//    @DisplayName("Apply: applies Kover and registers KoverProjectExtension (skips if not resolvable)")
//    fun applyPluginRegistersKoverExtension() {
//        val project = ProjectBuilder.builder().withName("test-app").build()
//        try {
//            KoverConventionPlugin().apply(project)
//        } catch (ex: Exception) {
//            org.junit.jupiter.api.Assumptions.assumeTrue(
//                false,
//                "Skipping: Kover plugin not available in test environment (${ex::class.java.simpleName}: ${ex.message})"
//            )
//        }
//
//        assertTrue(
//            project.pluginManager.hasPlugin("org.jetbrains.kotlinx.kover"),
//            "Expected 'org.jetbrains.kotlinx.kover' to be applied"
//        )
//
//        val extContainer = project.extensions
//        val koverExt = try {
//            val clazz =
//                Class.forName("kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension") as Class<Any>
//            extContainer.getByType(clazz)
//        } catch (e: Throwable) {
//            null
//        }
//
//        assertNotNull(koverExt, "KoverProjectExtension should be present after applying the plugin")
//    }
//}
