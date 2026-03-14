package dev.aurakai.auraframefx.romtools.backdrop

/**
 * Lightweight BackdropState enum for module-local usage.
 * This mirrors the app-level implementation but kept minimal to avoid heavy coupling.
 */
enum class BackdropState {
    STATIC,
    EXPLODING,
    ACTIVE,
    COMPLETING,
    VICTORY
}

