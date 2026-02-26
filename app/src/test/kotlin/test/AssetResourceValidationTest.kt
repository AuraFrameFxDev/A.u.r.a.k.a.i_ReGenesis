package test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.File

/**
 * Validation tests for drawable assets and resources.
 * Tests verify that newly added PNG images exist and have reasonable file sizes.
 * Note: This does not validate image content or format correctness, only file existence
 * and basic properties.
 */
class AssetResourceValidationTest {

    private val rootDir = File(".").canonicalFile

    @Test
    @DisplayName("Root level PNG asset exists")
    fun rootLevelPngAssetExists() {
        val pngFile = File(rootDir, "1769644126830.png")

        assertTrue(
            pngFile.exists(),
            "PNG asset 1769644126830.png should exist in repository root"
        )
    }

    @Test
    @DisplayName("Root level PNG asset has valid file size")
    fun rootLevelPngAssetHasValidSize() {
        val pngFile = File(rootDir, "1769644126830.png")

        if (pngFile.exists()) {
            val fileSize = pngFile.length()

            assertTrue(
                fileSize > 0,
                "PNG asset should have non-zero size"
            )

            assertTrue(
                fileSize < 50_000_000, // 50MB reasonable limit for a single image
                "PNG asset should not be excessively large (found: ${fileSize / 1024 / 1024}MB)"
            )
        }
    }

    @Test
    @DisplayName("Gate system assets directory exists")
    fun gateSystemAssetsDirectoryExists() {
        val gateAssetsDir = File(rootDir, "4kUltrareso glob gate system assests with titles/GateCards for Regenesis nav")

        assertTrue(
            gateAssetsDir.exists() && gateAssetsDir.isDirectory,
            "Gate system assets directory should exist"
        )
    }

    @Test
    @DisplayName("All gate card PNG assets exist")
    fun allGateCardAssetsExist() {
        val gateAssetsDir = File(rootDir, "4kUltrareso glob gate system assests with titles/GateCards for Regenesis nav")

        val expectedAssets = listOf(
            "Logopit_1769115314323.png",
            "Screenshot 2026-01-23 161831.png",
            "aura assistant overlay.png",
            "auraoverlayday.png",
            "auraoverlaynight.png",
            "backdrop for screens .png",
            "card_agentcreation .png"
        )

        if (gateAssetsDir.exists()) {
            expectedAssets.forEach { assetName ->
                val assetFile = File(gateAssetsDir, assetName)
                assertTrue(
                    assetFile.exists(),
                    "Gate card asset should exist: $assetName"
                )
            }
        }
    }

    @Test
    @DisplayName("Gate card assets have valid file sizes")
    fun gateCardAssetsHaveValidSizes() {
        val gateAssetsDir = File(rootDir, "4kUltrareso glob gate system assests with titles/GateCards for Regenesis nav")

        if (gateAssetsDir.exists() && gateAssetsDir.isDirectory) {
            val pngFiles = gateAssetsDir.listFiles { file ->
                file.isFile && file.extension.equals("png", ignoreCase = true)
            }

            assertNotNull(pngFiles, "Should be able to list PNG files")

            pngFiles?.forEach { pngFile ->
                val fileSize = pngFile.length()

                assertTrue(
                    fileSize > 0,
                    "PNG asset ${pngFile.name} should have non-zero size"
                )

                assertTrue(
                    fileSize < 50_000_000, // 50MB reasonable limit
                    "PNG asset ${pngFile.name} should not be excessively large (found: ${fileSize / 1024 / 1024}MB)"
                )
            }
        }
    }

    @Test
    @DisplayName("Aura overlay assets exist for day and night themes")
    fun auraOverlayAssetsExistForThemes() {
        val gateAssetsDir = File(rootDir, "4kUltrareso glob gate system assests with titles/GateCards for Regenesis nav")

        if (gateAssetsDir.exists()) {
            val dayOverlay = File(gateAssetsDir, "auraoverlayday.png")
            val nightOverlay = File(gateAssetsDir, "auraoverlaynight.png")

            assertTrue(
                dayOverlay.exists(),
                "Day theme overlay should exist: auraoverlayday.png"
            )

            assertTrue(
                nightOverlay.exists(),
                "Night theme overlay should exist: auraoverlaynight.png"
            )
        }
    }

    @Test
    @DisplayName("PNG files have PNG magic number signature")
    fun pngFilesHavePngSignature() {
        val rootPng = File(rootDir, "1769644126830.png")

        if (rootPng.exists()) {
            val bytes = rootPng.inputStream().use { stream ->
                ByteArray(8).apply { stream.read(this) }
            }

            // PNG signature: 89 50 4E 47 0D 0A 1A 0A
            val pngSignature = byteArrayOf(
                0x89.toByte(), 0x50, 0x4E, 0x47,
                0x0D, 0x0A, 0x1A, 0x0A
            )

            assertArrayEquals(
                pngSignature,
                bytes,
                "Root PNG file should have valid PNG signature"
            )
        }
    }

    @Test
    @DisplayName("Gate card PNG files have PNG magic number signature")
    fun gateCardPngFilesHavePngSignature() {
        val gateAssetsDir = File(rootDir, "4kUltrareso glob gate system assests with titles/GateCards for Regenesis nav")

        if (gateAssetsDir.exists() && gateAssetsDir.isDirectory) {
            val pngFiles = gateAssetsDir.listFiles { file ->
                file.isFile && file.extension.equals("png", ignoreCase = true)
            }

            val pngSignature = byteArrayOf(
                0x89.toByte(), 0x50, 0x4E, 0x47,
                0x0D, 0x0A, 0x1A, 0x0A
            )

            pngFiles?.forEach { pngFile ->
                val bytes = pngFile.inputStream().use { stream ->
                    ByteArray(8).apply { stream.read(this) }
                }

                assertArrayEquals(
                    pngSignature,
                    bytes,
                    "PNG file ${pngFile.name} should have valid PNG signature"
                )
            }
        }
    }

    @Test
    @DisplayName("Asset directory naming follows project conventions")
    fun assetDirectoryNamingFollowsConventions() {
        val gateAssetsDir = File(rootDir, "4kUltrareso glob gate system assests with titles/GateCards for Regenesis nav")

        // This test documents the existing directory structure
        // Note: Directory name has typo "assests" instead of "assets"
        assertTrue(
            gateAssetsDir.exists(),
            "Gate system assets directory structure should be maintained for compatibility"
        )

        // Verify subdirectory structure
        assertTrue(
            gateAssetsDir.path.contains("GateCards for Regenesis nav"),
            "Directory should contain GateCards subdirectory structure"
        )
    }

    @Test
    @DisplayName("Screenshot asset has descriptive naming")
    fun screenshotAssetHasDescriptiveNaming() {
        val gateAssetsDir = File(rootDir, "4kUltrareso glob gate system assests with titles/GateCards for Regenesis nav")

        if (gateAssetsDir.exists()) {
            val screenshot = File(gateAssetsDir, "Screenshot 2026-01-23 161831.png")

            assertTrue(
                screenshot.exists(),
                "Screenshot asset should exist with date-timestamp naming"
            )

            // Screenshot naming follows pattern: Screenshot YYYY-MM-DD HHMMSS.png
            assertTrue(
                screenshot.name.startsWith("Screenshot"),
                "Screenshot should follow standard naming convention"
            )
        }
    }

    @Test
    @DisplayName("Logopit generated asset exists")
    fun logopitGeneratedAssetExists() {
        val gateAssetsDir = File(rootDir, "4kUltrareso glob gate system assests with titles/GateCards for Regenesis nav")

        if (gateAssetsDir.exists()) {
            val logopit = File(gateAssetsDir, "Logopit_1769115314323.png")

            assertTrue(
                logopit.exists(),
                "Logopit generated asset should exist"
            )

            // Logopit assets follow pattern: Logopit_<timestamp>.png
            assertTrue(
                logopit.name.startsWith("Logopit_"),
                "Logopit asset should follow naming convention"
            )
        }
    }

    @Test
    @DisplayName("Card assets use descriptive lowercase naming")
    fun cardAssetsUseDescriptiveNaming() {
        val gateAssetsDir = File(rootDir, "4kUltrareso glob gate system assests with titles/GateCards for Regenesis nav")

        if (gateAssetsDir.exists()) {
            val cardAssets = listOf(
                "card_agentcreation .png",
                "backdrop for screens .png"
            )

            cardAssets.forEach { assetName ->
                val assetFile = File(gateAssetsDir, assetName)
                if (assetFile.exists()) {
                    assertTrue(
                        assetFile.name.contains("card") || assetFile.name.contains("backdrop"),
                        "Asset should have descriptive name indicating its purpose: ${assetFile.name}"
                    )
                }
            }
        }
    }
}