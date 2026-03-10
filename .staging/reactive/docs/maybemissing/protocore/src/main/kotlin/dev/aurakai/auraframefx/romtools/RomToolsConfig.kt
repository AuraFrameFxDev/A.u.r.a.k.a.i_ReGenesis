package dev.aurakai.auraframefx.romtools

/**
 * ROM Tools Configuration - Centralized and Type-Safe
 *
 * Replaces BuildConfig fields with compile-time constants
 * More maintainable than BuildConfig generation
 */
object RomToolsConfig {

    /** ROM Tools functionality enabled */
    const val ROM_TOOLS_ENABLED: Boolean = true

    /** Supported Android versions for ROM manipulation */
    val SUPPORTED_ANDROID_VERSIONS: List<Int> = listOf(13, 14, 15, 16)

    /** Supported CPU architectures */
    val SUPPORTED_ARCHITECTURES: List<String> = listOf(
        "arm64-v8a",
        "armeabi-v7a",
        "x86_64"
    )

    /** ROM modification operation timeouts */
    const val ROM_OPERATION_TIMEOUT_MS: Long = 30_000L

    /** Maximum ROM file size (in bytes) */
    const val MAX_ROM_FILE_SIZE: Long = 8L * 1024 * 1024 * 1024 // 8GB

    /** Supported ROM file formats */
    val SUPPORTED_ROM_FORMATS: List<String> = listOf(
        "img", "zip", "tar", "gz", "xz", "7z"
    )

    /** Live ROM editing enabled */
    const val LIVE_ROM_EDITING_ENABLED: Boolean = true

    /** Backup creation before ROM modification */
    const val AUTO_BACKUP_ENABLED: Boolean = true

    /** ROM verification checksum algorithms */
    val CHECKSUM_ALGORITHMS: List<String> = listOf(
        "SHA-256", "SHA-512", "MD5"
    )
}
