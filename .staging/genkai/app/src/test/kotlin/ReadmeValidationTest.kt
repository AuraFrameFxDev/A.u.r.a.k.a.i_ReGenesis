// NOTE: Testing library/framework detected: JUnit 5 (Jupiter) with Kotlin.
// Purpose: Validate README.md structure, ToC integrity, badges, code blocks balance, local links, and version consistency.

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Executable
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Locale

class ReadmeValidationTest {

    private fun repoRoot(): Path {
        var p = Paths.get("").toAbsolutePath()
        repeat(8) {
            if (Files.exists(p.resolve("README.md"))) return p
            p = p.parent ?: return Paths.get("").toAbsolutePath()
        }
        return Paths.get("").toAbsolutePath()
    }

    private fun readmePath(): Path = repoRoot().resolve("README.md")
    private fun readmeText(): String = Files.readString(readmePath(), StandardCharsets.UTF_8)
    private fun readmeLines(): List<String> =
        Files.readAllLines(readmePath(), StandardCharsets.UTF_8)

    @Test
    @DisplayName("README.md exists at repo root")
    fun readmeExists() {
        assertTrue(Files.exists(readmePath()), "README.md should exist at repository root")
    }

    @Test
    @DisplayName("README contains required top-level sections")
    fun readmeContainsRequiredSections() {
        val content = readmeText()
        val sections = listOf(
            "## 🌟 Overview",
            "## 🏗️ Architecture",
            "## 🚀 Getting Started",
            "## 📦 Module System",
            "## ⚙️ Configuration",
            "## 🛠️ Development",
            "## 📊 Build System",
            "## 🔒 Security",
            "## 📖 Documentation",
            "## 🤝 Contributing",
            "## 📜 License"
        )
        assertAll(sections.map { sec ->
            Executable {
                assertTrue(
                    content.contains(sec),
                    "Missing section: $sec"
                )
            }
        })
    }

    @Test
    @DisplayName("Table of Contents entries have matching headings")
    fun tableOfContentsEntriesHaveMatchingHeadings() {
        val lines = readmeLines()
        val tocStart = lines.indexOfFirst { it.contains("## 📋 Table of Contents") }
        assertTrue(tocStart >= 0, "Table of Contents heading not found")
        val tocLines = lines.drop(tocStart + 1)
            .takeWhile { !it.trim().startsWith("#") } // stop at next heading, not the first blank
            .filter { it.trim().startsWith("- [") }
        assertTrue(tocLines.isNotEmpty(), "No ToC entries found")

        val titles = tocLines.mapNotNull { Regex("""\[(.+?)\]\(""").find(it)?.groupValues?.get(1) }
        val headingTexts = lines.filter { it.matches(Regex("""^#{1,6}\s+.*$""")) }
            .map { it.replace(Regex("""^#{1,6}\s+"""), "") }

        fun norm(s: String) = s.lowercase(Locale.ROOT).replace(Regex("""[^a-z0-9]+"""), " ").trim()
        val normalizedHeadings = headingTexts.map(::norm)

        assertAll(titles.map { title ->
            Executable {
                val tn = norm(title)
                assertTrue(
                    normalizedHeadings.any { it.contains(tn) || tn.contains(it) },
                    "No matching heading for ToC entry: $title"
                )
            }
        })
    }

    @Test
    @DisplayName("Badges are present and correctly formatted")
    fun badgesPresent() {
        val content = readmeText()
        val required = listOf("![Build Status](", "![License](", "![API](", "![Kotlin](")
        assertAll(required.map { t ->
            Executable {
                assertTrue(
                    content.contains(t),
                    "Missing badge: $t"
                )
            }
        })
    }

    @Test
    @DisplayName("Code fences are balanced")
    fun codeFencesAreBalanced() {
        val count = Regex("```").findAll(readmeText()).count()
        assertEquals(0, count % 2, "Triple backtick count ($count) should be even")
    }

    @Test
    @DisplayName("Local links exist where applicable")
    fun localLinksExistWhereApplicable() {
        val root = repoRoot()
        val mandatory = listOf("LICENSE", "Architecture.md")
        val optional = listOf(
            "docs/YUKIHOOK_SETUP_GUIDE.md",
            "romtools/README.md",
            "core-module/Module.md",
            "build/docs/html"
        )
        assertAll(mandatory.map { path ->
            Executable {
                assertTrue(
                    Files.exists(root.resolve(path)),
                    "Missing referenced file: $path"
                )
            }
        })
        // Optional: if present, ensure valid
        assertAll(optional.map { path ->
            Executable {
                val p = root.resolve(path)
                if (Files.exists(p)) {
                    assertTrue(
                        Files.isRegularFile(p) || Files.isDirectory(p),
                        "Invalid path type for: $path"
                    )
                }
            }
        })
    }

    @Test
    @DisplayName("README versions align with versions catalog (if present)")
    fun versionsAlignWithVersionsCatalogIfPresent() {
        val root = repoRoot()
        val catalog = root.resolve("gradle/libs.versions.toml")
        assumeTrue(Files.exists(catalog), "No versions catalog; skipping")
        val toml = Files.readString(catalog, StandardCharsets.UTF_8)

        fun findToml(key: String): String? =
            Regex("""(?m)^\s*${key}\s*=\s*"([^"]+)"""").find(toml)?.groupValues?.get(1)

        val agp = findToml("agp")
        val kotlin = findToml("kotlin")
        val ksp = findToml("ksp")
        val text = readmeText()

        agp?.let {
            assertTrue(
                text.contains("| **Android Gradle Plugin** | $it"),
                "README AGP version should be $it"
            )
        }
        kotlin?.let {
            assertTrue(
                text.contains("| **Kotlin** | $it"),
                "README Kotlin version should be $it"
            )
        }
        ksp?.let {
            assertTrue(
                text.contains("| **KSP** | $it"),
                "README KSP version should be $it"
            )
        }
    }

    @Test
    @DisplayName("Gradle wrapper version matches README (if wrapper present)")
    fun gradleWrapperVersionMatchesReadmeIfWrapperPresent() {
        val root = repoRoot()
        val props = root.resolve("gradle/wrapper/gradle-wrapper.properties")
        assumeTrue(Files.exists(props), "No gradle wrapper; skipping")
        val s = Files.readString(props, StandardCharsets.UTF_8)
        val ver = Regex("""gradle-([0-9][^-/]*)-""").find(s)?.groupValues?.get(1)
        if (ver != null) {
            assertTrue(
                readmeText().contains("| **Gradle** | $ver"),
                "README Gradle version should be $ver"
            )
        }
    }
}