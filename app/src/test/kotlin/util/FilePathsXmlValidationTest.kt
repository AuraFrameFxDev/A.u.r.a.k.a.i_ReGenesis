package util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class FilePathsXmlValidationTest {

    private fun loadXml(): org.w3c.dom.Document {
        // Try common locations; adjust as needed
        val candidates = listOf(
            "app/src/main/res/xml/file_paths.xml",
            "app/src/main/res/xml/filepaths.xml",
            "app/src/main/res/xml/providers_paths.xml"
        )
        val found = candidates.firstOrNull { File(it).exists() }
            ?: throw AssertionError("file_paths.xml not found in expected locations: $candidates")
        val factory = DocumentBuilderFactory.newInstance()
        factory.isNamespaceAware = false
        return factory.newDocumentBuilder().parse(File(found))
    }

    @org.junit.jupiter.api.Test
    fun xml_has_paths_root_and_expected_children() {
        val doc = loadXml()
        val root = doc.documentElement
        assertEquals("paths", root.tagName)

        fun hasTag(tag: String): Boolean =
            root.getElementsByTagName(tag).length > 0

        // Happy path: required entries present
        assertTrue(
            "Expected <external-path> entry",
            hasTag("external-path") || hasTag("external-paths")
        )
        assertTrue("Expected <cache-path> entry", hasTag("cache-path"))
        assertTrue("Expected <files-path> entry", hasTag("files-path"))
    }

    @org.junit.jupiter.api.Test
    fun xml_children_have_name_and_path_attributes() {
        val doc = loadXml()
        val root = doc.documentElement
        val tags = arrayOf("external-path", "cache-path", "files-path")
        for (t in tags) {
            val nodes = root.getElementsByTagName(t)
            for (i in 0 until nodes.length) {
                val node = nodes.item(i) as org.w3c.dom.Element
                assertTrue("$t missing 'name' attribute", node.hasAttribute("name"))
                assertTrue("$t missing 'path' attribute", node.hasAttribute("path"))
                // Edge cases: ensure path is not empty
                assertTrue("$t 'path' should not be empty", node.getAttribute("path").isNotEmpty())
            }
        }
    }
}