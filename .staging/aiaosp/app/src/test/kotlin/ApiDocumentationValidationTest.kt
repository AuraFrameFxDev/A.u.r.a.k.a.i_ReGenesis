package docs

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.streams.asSequence

/**
 * Testing library/framework: JUnit4 (org.junit). These tests are plain JVM unit tests.
 *
 * Purpose: Validate critical structure and content of the API documentation introduced/modified in the PR:
 * - Presence of top-level heading and key sections
 * - Mermaid dependency graph block
 * - Kotlin code fences and representative APIs/interfaces
 * - Error handling section semantics
 * - Absence of obvious structural regressions (e.g., unmatched code fences)
 *
 * Strategy:
 * - Locate the markdown doc by scanning the repository for the unique top heading.
 * - Run regex-based validations on the document contents.
 * - Keep tests resilient to minor cosmetic changes while asserting essential semantics.
 */
class ApiDocumentationValidationTest {

    private fun readApiDoc(): String {
        // Search the repo for the unique heading line
        val root: Path = Paths.get("").toAbsolutePath()
        val heading = Regex("""^#\s*📡\s*API Documentation\s*-\s*AOSP ReGenesis\s*$""")
        var docPath: Path? = null

        Files.walk(root).use { stream ->
            val candidates = stream.asSequence()
                .filter { Files.isRegularFile(it) }
                .filter {
                    it.fileName.toString().endsWith(".md", ignoreCase = true)
                            || it.fileName.toString().endsWith(".markdown", ignoreCase = true)
                            || it.fileName.toString().equals("README", ignoreCase = true)
                            || it.fileName.toString().equals("README.md", ignoreCase = true)
                }
                .take(10_000) // safety
                .toList()

            for (p in candidates) {
                val content = Files.readAllLines(p, StandardCharsets.UTF_8).joinToString("\n")
                if (heading.containsMatchIn(content)) {
                    docPath = p
                    break
                }
            }
        }

        // If not found, fail with a clear message
        assertNotNull(
            "API documentation file with heading '# 📡 API Documentation - AOSP ReGenesis' was not found in repo.",
            docPath
        )
        val content = Files.readAllLines(docPath, StandardCharsets.UTF_8).joinToString("\n")
        assertTrue("API documentation appears to be empty.", content.isNotBlank())
        return content
    }

    @Test
    fun `doc contains the top-level heading and table of contents anchors`() {
        val doc = readApiDoc()

        assertTrue(
            "Missing top-level heading.",
            Regex(
                """^#\s*📡\s*API Documentation\s*-\s*AOSP ReGenesis\s*$""",
                RegexOption.MULTILINE
            ).containsMatchIn(doc)
        )

        val expectedTocAnchors = listOf(
            "🏗️ Architecture Overview",
            "🔒 Security API",
            "☁️ Cloud Integration API",
            "🎨 UI Components API",
            "📱 ROM Tools API",
            "🧠 Core Module API",
            "📊 Data Layer API",
            "🔧 Utilities API",
            "🔗 Error Handling",
            "📝 Usage Examples"
        )

        expectedTocAnchors.forEach { anchor ->
            assertTrue(
                "Table of Contents should reference '$anchor'.",
                doc.contains("[$anchor](") || doc.contains("[$anchor] (#") || doc.contains(anchor)
            )
        }
    }

    @Test
    fun `architecture section includes mermaid dependency graph`() {
        val doc = readApiDoc()
        val mermaidBlock = Regex("""```mermaid\s+graph\s+TD[\s\S]*?```""", RegexOption.MULTILINE)
        assertTrue(
            "Architecture section should contain a mermaid 'graph TD' block.",
            mermaidBlock.containsMatchIn(doc)
        )
        // Basic module arrows presence
        listOf("A\\[app\\]", "B\\[core-module\\]").forEach { node ->
            assertTrue(
                "Mermaid diagram should reference node $node",
                Regex(node).containsMatchIn(doc)
            )
        }
    }

    @Test
    fun `security API includes SecureCommunication interface with core methods`() {
        val doc = readApiDoc()
        val iface = Regex("""interface\s+SecureCommunication\s*\{[\s\S]*?}""")
        assertTrue(
            "SecureCommunication interface should be documented.",
            iface.containsMatchIn(doc)
        )

        // Methods
        val methods = listOf(
            "suspend\\s+fun\\s+sendEncryptedMessage\\s*\\(",
            "suspend\\s+fun\\s+receiveEncryptedMessage\\s*\\(",
            "suspend\\s+fun\\s+establishSecureChannel\\s*\\("
        )
        methods.forEach { m ->
            assertTrue(
                "SecureCommunication should document method matching /$m/.",
                Regex(m).containsMatchIn(doc)
            )
        }
    }

    @Test
    fun `cloud integration includes OracleCloudService interface with listObjects`() {
        val doc = readApiDoc()
        assertTrue(
            "OracleCloudService interface should be present.",
            Regex("""interface\s+OracleCloudService\s*\{""").containsMatchIn(doc)
        )
        assertTrue(
            "OracleCloudService should include listObjects(path, recursive).",
            Regex("""suspend\s+fun\s+listObjects\s*\(\s*path:\s*String,\s*recursive:\s*Boolean\s*=\s*false\s*\)""")
                .containsMatchIn(doc)
        )
    }

    @Test
    fun `UI Components include AuraButton and AuraCard composables`() {
        val doc = readApiDoc()
        assertTrue(
            "AuraButton composable should be documented.",
            Regex("""@Composable\s+fun\s+AuraButton\s*\(""").containsMatchIn(doc)
        )
        assertTrue(
            "AuraCard composable should be documented.",
            Regex("""@Composable\s+fun\s+AuraCard\s*\(""").containsMatchIn(doc)
        )
        // Validate that AuraButton has 'loading' flag behavior text/code
        assertTrue(
            "AuraButton should mention 'loading' affecting enabled state.",
            doc.contains("enabled = enabled && !loading")
        )
    }

    @Test
    fun `ROM tools include RomManager with critical operations`() {
        val doc = readApiDoc()
        val expectedOps = listOf(
            "flashRom",
            "createBackup",
            "restoreBackup",
            "unlockBootloader",
            "installRecovery"
        )
        expectedOps.forEach { op ->
            assertTrue(
                "RomManager should include '$op'.",
                Regex("""suspend\s+fun\s+$op\s*\(""").containsMatchIn(doc)
            )
        }
        assertTrue(
            "RomVerifier and VerificationResult should be documented.",
            Regex("""interface\s+RomVerifier""").containsMatchIn(doc) &&
                    Regex("""sealed\s+class\s+VerificationResult""").containsMatchIn(doc)
        )
    }

    @Test
    fun `core module includes Repository interface and UserRepository snippet`() {
        val doc = readApiDoc()
        assertTrue(
            "Repository<T, ID> interface should be present.",
            Regex("""interface\s+Repository<\s*T,\s*ID\s*>\s*\{""").containsMatchIn(doc)
        )
        assertTrue(
            "UserRepository class snippet should be present.",
            Regex("""class\s+UserRepository\s+@Inject\s+constructor\(""").containsMatchIn(doc)
        )
    }

    @Test
    fun `network layer safeApiCall covers success, empty body, http error, IO, and mapped errors`() {
        val doc = readApiDoc()
        // Check presence of the function and key branches
        assertTrue(
            "safeApiCall function should be present.",
            Regex("""suspend\s+fun\s*<T>\s*safeApiCall\(""").containsMatchIn(doc)
        )

        // Branch keywords indicative of coverage
        val branchHints = listOf(
            "response.isSuccessful",
            "body != null",
            "Empty body",
            "NetworkResult.Error\\(response.code\\(\\)",
            "IOException",
            "errorHandler.handleError\\(e\\)"
        )
        branchHints.forEach { hint ->
            assertTrue(
                "safeApiCall should document branch: $hint",
                Regex(hint).containsMatchIn(doc)
            )
        }
    }

    @Test
    fun `utilities include String isValidEmail and Flow throttleLatest`() {
        val doc = readApiDoc()
        assertTrue(
            "String.isValidEmail extension should be documented.",
            Regex("""fun\s+String\.isValidEmail\(\):\s*Boolean""").containsMatchIn(doc)
        )
        assertTrue(
            "Flow<T>.throttleLatest extension should be documented.",
            Regex("""fun\s*<T>\s*Flow<T>\.throttleLatest\(""").containsMatchIn(doc)
        )
    }

    @Test
    fun `error handling includes AuraError sealed class and ErrorHandler mapping`() {
        val doc = readApiDoc()
        assertTrue(
            "AuraError sealed class should be present.",
            Regex("""sealed\s+class\s+AuraError\s*:\s*Exception\(\)\s*\{""").containsMatchIn(doc)
        )
        val expectedCases =
            listOf("NetworkError", "SecurityError", "ValidationError", "UnknownError")
        expectedCases.forEach { c ->
            assertTrue(
                "AuraError should include case '$c'.",
                Regex("""data\s+class\s+$c\b|object\s+$c\b""").containsMatchIn(doc)
            )
        }
        assertTrue(
            "ErrorHandler.handleError should map common exceptions.",
            Regex("""class\s+ErrorHandler\s+@Inject\s+constructor\([^)]*\)\s*\{\s*fun\s+handleError""")
                .containsMatchIn(doc)
        )
    }

    @Test
    fun `code fences are balanced and contain kotlin or mermaid blocks`() {
        val doc = readApiDoc()
        val fenceCount = Regex("```").findAll(doc).count()
        assertTrue(
            "Code fences should be balanced (even number). Found $fenceCount.",
            fenceCount % 2 == 0
        )

        val hasKotlin = Regex("""```kotlin[\s\S]*?```""").containsMatchIn(doc)
        val hasMermaid = Regex("""```mermaid[\s\S]*?```""").containsMatchIn(doc)
        assertTrue("Documentation should include at least one Kotlin code block.", hasKotlin)
        assertTrue("Documentation should include at least one Mermaid diagram block.", hasMermaid)
    }

    @Test
    fun `usage examples include Compose login screen and ViewModel sendMessage`() {
        val doc = readApiDoc()
        assertTrue(
            "LoginScreen composable usage example should be present.",
            Regex("""fun\s+LoginScreen\s*\(\s*\)\s*\{""").containsMatchIn(doc)
        )
        assertTrue(
            "MessageViewModel sendMessage usage example should be present.",
            Regex("""class\s+MessageViewModel\s+@Inject\s+constructor\(""").containsMatchIn(doc)
        )
        assertTrue(
            "sendMessage should call sendEncryptedMessage with DeviceId.",
            doc.contains("sendEncryptedMessage(") && doc.contains("DeviceId(recipientId)")
        )
    }

    @Test
    fun `basic link integrity - internal anchors present for key sections`() {
        val doc = readApiDoc()

        // Validate that headings exist with markdown '## ' level for key sections (anchors are typically generated from them)
        val requiredH2 = listOf(
            "## 🏗️ Architecture Overview",
            "## 🔒 Security API",
            "## ☁️ Cloud Integration API",
            "## 🎨 UI Components API",
            "## 📱 ROM Tools API",
            "## 🧠 Core Module API",
            "## 📊 Data Layer API",
            "## 🔧 Utilities API",
            "## 🔗 Error Handling",
            "## 📝 Usage Examples"
        )
        requiredH2.forEach { h2 ->
            assertTrue("Missing section heading: $h2", doc.contains(h2))
        }
    }

    @Test
    fun `no obvious TODO or placeholder markers remain`() {
        val doc = readApiDoc()
        val badMarkers = listOf("TBD", "TODO:", "TODO", "FIXME", "XXX")
        val found = badMarkers.any { marker -> Regex("""\b$marker\b""").containsMatchIn(doc) }
        assertTrue("Documentation should not contain TODO/FIXME/XXX markers.", !found)
    }
}