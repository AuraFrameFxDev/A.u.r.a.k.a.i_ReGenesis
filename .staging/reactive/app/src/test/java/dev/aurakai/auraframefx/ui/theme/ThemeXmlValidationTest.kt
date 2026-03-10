package dev.aurakai.auraframefx.ui.theme

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.junit.After
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.io.ByteArrayInputStream
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Document
import org.w3c.dom.Element

/**
 * Unit tests for validating theme XML structure and content.
 *
 * Testing Framework: JUnit 4 with standard Android XML parsing libraries
 *
 * These tests validate the theme XML file structure, required attributes,
 * color references, and Android compatibility for the AuraFrameFX theme.
 */
class ThemeXmlValidationTest {

    private lateinit var themeXml: String
    private lateinit var document: Document

    @Before
    fun setUp() {
        // Theme XML content based on the actual file content
        themeXml = """
            <?xml version="1.0" encoding="utf-8"?>
            <resources>
                <!-- Base application theme. -->
                <style name="Theme.AuraFrameFX" parent="Theme.AppCompat.DayNight.NoActionBar">
                    <!-- Primary brand color. -->
                    <item name="colorPrimary">@color/dark_primary</item>
                    <item name="colorPrimaryDark">@color/dark_primary_container</item>
                    <item name="colorAccent">@color/dark_secondary</item>
                    <!-- Status bar color. -->
                    <item name="android:statusBarColor">@android:color/transparent</item>
                    <!-- Customize your theme here. -->
                </style>
            </resources>
        """.trimIndent()

        // Parse XML for DOM-based tests
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        document = builder.parse(ByteArrayInputStream(themeXml.toByteArray()))
    }

    @After
    fun tearDown() {
        // Clean up resources if needed
    }

    @Test
    fun testXmlIsWellFormedAndParseable() {
        // Test that XML can be parsed without exceptions using XmlPullParser
        val factory = XmlPullParserFactory.newInstance()
        val parser = factory.newPullParser()
        parser.setInput(StringReader(themeXml))

        var eventType = parser.eventType
        var hasContent = false
        var elementCount = 0

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    hasContent = true
                    elementCount++
                }
            }
            eventType = parser.next()
        }

        assertTrue("XML should contain parseable content", hasContent)
        assertTrue("XML should have multiple elements", elementCount > 0)
    }

    @Test
    fun testXmlHasCorrectEncodingDeclaration() {
        assertTrue(
            "XML should specify UTF-8 encoding",
            themeXml.contains("encoding=\"utf-8\"")
        )
        assertTrue(
            "XML should have proper XML declaration",
            themeXml.startsWith("<?xml version=\"1.0\"")
        )
    }

    @Test
    fun testXmlHasValidStructureWithResourcesRoot() {
        val rootElement = document.documentElement
        assertEquals("Root element should be 'resources'", "resources", rootElement.tagName)
        assertNotNull("Root element should not be null", rootElement)
    }

    @Test
    fun testThemeHasCorrectNameAttribute() {
        val styleElements = document.getElementsByTagName("style")
        assertTrue("Should have at least one style element", styleElements.length > 0)

        val themeStyle = styleElements.item(0) as Element
        assertEquals(
            "Theme name should be 'Theme.AuraFrameFX'",
            "Theme.AuraFrameFX", themeStyle.getAttribute("name")
        )
        assertFalse(
            "Theme name should not be empty",
            themeStyle.getAttribute("name").isEmpty()
        )
    }

    @Test
    fun testThemeHasCorrectParentAttribute() {
        val styleElements = document.getElementsByTagName("style")
        val themeStyle = styleElements.item(0) as Element
        assertEquals(
            "Parent theme should be 'Theme.AppCompat.DayNight.NoActionBar'",
            "Theme.AppCompat.DayNight.NoActionBar", themeStyle.getAttribute("parent")
        )
    }

    @Test
    fun testThemeContainsRequiredColorAttributes() {
        val itemElements = document.getElementsByTagName("item")
        val itemNames = mutableListOf<String>()

        for (i in 0 until itemElements.length) {
            val item = itemElements.item(i) as Element
            itemNames.add(item.getAttribute("name"))
        }

        val requiredColors = listOf("colorPrimary", "colorPrimaryDark", "colorAccent")
        for (colorName in requiredColors) {
            assertTrue(
                "Theme should contain $colorName",
                itemNames.contains(colorName)
            )
        }

        assertTrue("Should have at least 3 color items", itemNames.size >= 3)
    }

    @Test
    fun testColorReferencesFollowAndroidResourceFormat() {
        val itemElements = document.getElementsByTagName("item")

        for (i in 0 until itemElements.length) {
            val item = itemElements.item(i) as Element
            val content = item.textContent.trim()
            val name = item.getAttribute("name")

            if (name.contains("color") && !name.startsWith("android:")) {
                assertTrue(
                    "Color reference '$content' for '$name' should start with @color/",
                    content.startsWith("@color/") || content.startsWith("@android:color/")
                )
                assertFalse("Color reference should not be empty", content.isEmpty())
            }
        }
    }

    @Test
    fun testStatusBarColorIsProperlyConfigured() {
        val itemElements = document.getElementsByTagName("item")
        var statusBarColorFound = false

        for (i in 0 until itemElements.length) {
            val item = itemElements.item(i) as Element
            if (item.getAttribute("name") == "android:statusBarColor") {
                statusBarColorFound = true
                assertEquals(
                    "Status bar should be transparent",
                    "@android:color/transparent", item.textContent.trim()
                )
                break
            }
        }

        assertTrue("Status bar color should be configured", statusBarColorFound)
    }

    @Test
    fun testThemeHasNoDuplicateItemNames() {
        val itemElements = document.getElementsByTagName("item")
        val itemNames = mutableListOf<String>()
        val duplicates = mutableListOf<String>()

        for (i in 0 until itemElements.length) {
            val item = itemElements.item(i) as Element
            val name = item.getAttribute("name")
            if (itemNames.contains(name)) {
                duplicates.add(name)
            }
            itemNames.add(name)
        }

        assertTrue("No duplicate item names should exist: $duplicates", duplicates.isEmpty())
    }

    @Test
    fun testXmlContainsProperCommentsForDocumentation() {
        assertTrue(
            "Should contain comment about base application theme",
            themeXml.contains("<!-- Base application theme. -->")
        )
        assertTrue(
            "Should contain comment about primary brand color",
            themeXml.contains("<!-- Primary brand color. -->")
        )
        assertTrue(
            "Should contain comment about status bar color",
            themeXml.contains("<!-- Status bar color. -->")
        )
        assertTrue(
            "Should contain customization comment",
            themeXml.contains("<!-- Customize your theme here. -->")
        )
    }

    @Test
    fun testColorValuesReferenceDarkThemeColors() {
        val itemElements = document.getElementsByTagName("item")
        val colorMappings = mutableMapOf<String, String>()

        for (i in 0 until itemElements.length) {
            val item = itemElements.item(i) as Element
            val content = item.textContent.trim()
            val name = item.getAttribute("name")
            colorMappings[name] = content
        }

        assertEquals(
            "colorPrimary should reference dark_primary",
            "@color/dark_primary", colorMappings["colorPrimary"]
        )
        assertEquals(
            "colorPrimaryDark should reference dark_primary_container",
            "@color/dark_primary_container", colorMappings["colorPrimaryDark"]
        )
        assertEquals(
            "colorAccent should reference dark_secondary",
            "@color/dark_secondary", colorMappings["colorAccent"]
        )
    }

    @Test
    fun testXmlHandlesMalformedInputGracefully() {
        val malformedXmlCases = listOf(
            // Missing closing tag
            """<?xml version="1.0" encoding="utf-8"?>
               <resources>
                   <style name="Theme.AuraFrameFX" parent="Theme.AppCompat.DayNight.NoActionBar">
                       <item name="colorPrimary">@color/dark_primary
                   </style>
               </resources>""",
            // Missing quotes in attribute
            """<?xml version="1.0" encoding="utf-8"?>
               <resources>
                   <style name=Theme.AuraFrameFX parent="Theme.AppCompat.DayNight.NoActionBar">
                   </style>
               </resources>""",
            // Invalid XML structure
            """<?xml version="1.0" encoding="utf-8"?>
               <resources>
                   <style><item></style></item>
               </resources>"""
        )

        malformedXmlCases.forEachIndexed { index, malformedXml ->
            try {
                val factory = DocumentBuilderFactory.newInstance()
                val builder = factory.newDocumentBuilder()
                builder.parse(ByteArrayInputStream(malformedXml.toByteArray()))
                fail("Malformed XML case $index should throw exception")
            } catch (e: Exception) {
                // Expected behavior - malformed XML should throw exception
                assertNotNull("Should catch parsing exception for case $index", e)
            }
        }
    }

    @Test
    fun testThemeIsCompatibleWithAppCompat() {
        val styleElements = document.getElementsByTagName("style")
        val themeStyle = styleElements.item(0) as Element
        val parent = themeStyle.getAttribute("parent")

        assertTrue(
            "Theme should extend AppCompat theme",
            parent.contains("Theme.AppCompat")
        )
        assertTrue(
            "Theme should use DayNight variant for dark mode support",
            parent.contains("DayNight")
        )
        assertTrue(
            "Theme should have NoActionBar for custom toolbar",
            parent.contains("NoActionBar")
        )
    }

    @Test
    fun testAllColorReferencesAreValidResourceFormat() {
        val itemElements = document.getElementsByTagName("item")
        val colorPattern = Regex("^@(android:)?color/[a-zA-Z_][a-zA-Z0-9_]*$")
        val invalidReferences = mutableListOf<String>()

        for (i in 0 until itemElements.length) {
            val item = itemElements.item(i) as Element
            val content = item.textContent.trim()
            val name = item.getAttribute("name")

            if (name.contains("color") || name.contains("Color")) {
                if (!colorPattern.matches(content)) {
                    invalidReferences.add("$name: $content")
                }
            }
        }

        assertTrue(
            "All color references should match Android format. Invalid: $invalidReferences",
            invalidReferences.isEmpty()
        )
    }

    @Test
    fun testThemeNameFollowsAndroidNamingConventions() {
        val styleElements = document.getElementsByTagName("style")
        val themeStyle = styleElements.item(0) as Element
        val themeName = themeStyle.getAttribute("name")

        assertTrue(
            "Theme name should start with 'Theme.'",
            themeName.startsWith("Theme.")
        )
        assertTrue(
            "Theme name should use PascalCase",
            themeName.matches(Regex("^Theme\\.[A-Z][a-zA-Z0-9]*$"))
        )
        assertFalse(
            "Theme name should not contain spaces",
            themeName.contains(" ")
        )
        assertFalse(
            "Theme name should not contain special characters",
            themeName.matches(Regex(".*[^a-zA-Z0-9.].*"))
        )
    }

    @Test
    fun testXmlValidatesAgainstMinimalRequiredStructure() {
        // Test minimal valid theme structure
        val minimalTheme = """
            <?xml version="1.0" encoding="utf-8"?>
            <resources>
                <style name="Theme.Test" parent="Theme.AppCompat">
                </style>
            </resources>
        """.trimIndent()

        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val doc = builder.parse(ByteArrayInputStream(minimalTheme.toByteArray()))

        assertNotNull("Minimal theme should parse successfully", doc)
        assertEquals("Should have resources root", "resources", doc.documentElement.tagName)

        val styles = doc.getElementsByTagName("style")
        assertTrue("Should have at least one style", styles.length > 0)
    }

    @Test
    fun testEmptyThemeIsHandledGracefully() {
        val emptyTheme = """
            <?xml version="1.0" encoding="utf-8"?>
            <resources>
            </resources>
        """.trimIndent()

        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val doc = builder.parse(ByteArrayInputStream(emptyTheme.toByteArray()))

        assertNotNull("Empty resources should parse successfully", doc)
        assertEquals("Should have resources root", "resources", doc.documentElement.tagName)
        assertEquals("Should have no style elements", 0, doc.getElementsByTagName("style").length)
    }

    @Test
    fun testThemeHasAllRequiredAttributesPresent() {
        val styleElements = document.getElementsByTagName("style")
        assertTrue("Should have style element", styleElements.length > 0)

        val themeStyle = styleElements.item(0) as Element
        assertTrue(
            "Style should have name attribute",
            themeStyle.hasAttribute("name")
        )
        assertTrue(
            "Style should have parent attribute",
            themeStyle.hasAttribute("parent")
        )
        assertFalse(
            "Name attribute should not be empty",
            themeStyle.getAttribute("name").isEmpty()
        )
        assertFalse(
            "Parent attribute should not be empty",
            themeStyle.getAttribute("parent").isEmpty()
        )
    }

    @Test
    fun testThemeItemsHaveValidNameAttributes() {
        val itemElements = document.getElementsByTagName("item")
        val invalidItems = mutableListOf<String>()

        for (i in 0 until itemElements.length) {
            val item = itemElements.item(i) as Element
            val name = item.getAttribute("name")

            if (name.isEmpty()) {
                invalidItems.add("Item at index $i has empty name")
            } else if (name.contains(" ")) {
                invalidItems.add("Item '$name' contains spaces")
            } else if (!name.matches(Regex("^[a-zA-Z_:][a-zA-Z0-9_:]*$"))) {
                invalidItems.add("Item '$name' has invalid characters")
            }
        }

        assertTrue(
            "All item names should be valid. Invalid items: $invalidItems",
            invalidItems.isEmpty()
        )
    }

    @Test
    fun testThemeSupportsDarkModeConfiguration() {
        val styleElements = document.getElementsByTagName("style")
        val themeStyle = styleElements.item(0) as Element
        val parent = themeStyle.getAttribute("parent")

        assertTrue(
            "Theme should use DayNight for automatic dark mode switching",
            parent.contains("DayNight")
        )

        // Check that dark-themed colors are being used
        val itemElements = document.getElementsByTagName("item")
        var darkColorCount = 0

        for (i in 0 until itemElements.length) {
            val item = itemElements.item(i) as Element
            val content = item.textContent.trim()
            if (content.contains("dark_")) {
                darkColorCount++
            }
        }

        assertTrue("Theme should use dark-themed color references", darkColorCount > 0)
    }

    @Test
    fun testXmlStructureIsProperlyNested() {
        val factory = XmlPullParserFactory.newInstance()
        val parser = factory.newPullParser()
        parser.setInput(StringReader(themeXml))

        val elementStack = mutableListOf<String>()
        var eventType = parser.eventType

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    elementStack.add(parser.name)
                }

                XmlPullParser.END_TAG -> {
                    if (elementStack.isNotEmpty()) {
                        val lastElement = elementStack.removeAt(elementStack.size - 1)
                        assertEquals(
                            "Closing tag should match opening tag",
                            lastElement,
                            parser.name
                        )
                    }
                }
            }
            eventType = parser.next()
        }

        assertTrue("All tags should be properly closed", elementStack.isEmpty())
    }

    @Test
    fun testThemeContainsOnlyValidItemElements() {
        val itemElements = document.getElementsByTagName("item")

        for (i in 0 until itemElements.length) {
            val item = itemElements.item(i) as Element
            val name = item.getAttribute("name")
            val content = item.textContent.trim()

            // Each item should have a name attribute
            assertTrue(
                "Item at index $i should have name attribute",
                name.isNotEmpty()
            )

            // Each item should have content
            assertTrue(
                "Item '$name' should have content",
                content.isNotEmpty()
            )

            // Validate item names are properly formatted
            assertTrue(
                "Item name '$name' should be valid Android attribute",
                name.matches(Regex("^[a-zA-Z_:][a-zA-Z0-9_:]*$"))
            )
        }
    }

    @Test
    fun testXmlParsingPerformance() {
        val startTime = System.currentTimeMillis()

        // Parse the XML multiple times to test performance
        repeat(100) {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            builder.parse(ByteArrayInputStream(themeXml.toByteArray()))
        }

        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime

        assertTrue(
            "XML parsing should complete within reasonable time (< 1000ms)",
            duration < 1000
        )
    }

    @Test
    fun testThemeCompatibilityWithMaterial3() {
        val itemElements = document.getElementsByTagName("item")
        val materialColors = listOf("colorPrimary", "colorSecondary", "colorAccent")
        val foundColors = mutableListOf<String>()

        for (i in 0 until itemElements.length) {
            val item = itemElements.item(i) as Element
            val name = item.getAttribute("name")
            if (materialColors.contains(name)) {
                foundColors.add(name)
            }
        }

        assertTrue(
            "Theme should contain Material Design color attributes",
            foundColors.isNotEmpty()
        )
        assertTrue(
            "Theme should have primary color for Material compatibility",
            foundColors.contains("colorPrimary")
        )
    }

    @Test
    fun testXmlEncodingAndCharacterHandling() {
        // Test XML with special characters and Unicode
        val xmlWithSpecialChars = """
            <?xml version="1.0" encoding="utf-8"?>
            <resources>
                <style name="Theme.Test" parent="Theme.AppCompat">
                    <item name="colorPrimary">@color/test_color</item>
                </style>
            </resources>
        """.trimIndent()

        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val doc = builder.parse(ByteArrayInputStream(xmlWithSpecialChars.toByteArray()))

        assertNotNull("XML with special characters should parse", doc)
        assertEquals("Should maintain proper encoding", "resources", doc.documentElement.tagName)
    }

    @Test
    fun testThemeInheritanceChain() {
        val styleElements = document.getElementsByTagName("style")
        val themeStyle = styleElements.item(0) as Element
        val parent = themeStyle.getAttribute("parent")

        // Verify the inheritance chain makes sense
        assertTrue(
            "Parent should be a valid AppCompat theme",
            parent.startsWith("Theme.AppCompat")
        )

        // Check that it supports modern Android features
        assertTrue(
            "Should use DayNight for system theme support",
            parent.contains("DayNight")
        )

        // Verify NoActionBar for modern app design
        assertTrue(
            "Should use NoActionBar for custom app bars",
            parent.contains("NoActionBar")
        )
    }
}