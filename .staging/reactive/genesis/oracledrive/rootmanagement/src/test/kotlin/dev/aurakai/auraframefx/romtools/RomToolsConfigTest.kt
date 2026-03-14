package dev.aurakai.auraframefx.romtools

import org.junit.jupiter.api.Test

class RomToolsConfigTest {
    @Test
    fun config_constants_and_collections_are_sane() {
        assertTrue(RomToolsConfig.ROM_TOOLS_ENABLED)
        assertTrue(RomToolsConfig.AUTO_BACKUP_ENABLED)
        assertTrue(RomToolsConfig.ROM_OPERATION_TIMEOUT_MS > 0)
        assertTrue(RomToolsConfig.MAX_ROM_FILE_SIZE >= 8L * 1024 * 1024 * 1024)

        assertTrue(RomToolsConfig.SUPPORTED_ANDROID_VERSIONS.contains(14))
        assertTrue(RomToolsConfig.SUPPORTED_ARCHITECTURES.contains("arm64-v8a"))
        assertTrue(RomToolsConfig.SUPPORTED_ROM_FORMATS.containsAll(listOf("img", "zip")))
        assertTrue(RomToolsConfig.CHECKSUM_ALGORITHMS.contains("SHA-256"))
    }
}