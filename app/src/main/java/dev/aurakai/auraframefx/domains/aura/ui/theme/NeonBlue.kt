package dev.aurakai.auraframefx.domains.aura.ui.theme

import androidx.compose.ui.graphics.Color

// Legacy alias kept temporarily for compatibility. Prefer `NeonBlue` in Theme.kt.
@Deprecated(
    "Use dev.aurakai.auraframefx.ui.theme.NeonBlue from Theme.kt instead",
    level = DeprecationLevel.WARNING
)
val NeonBlueLegacy: Color = Color(0xFF00C2FF)

// Intentionally do NOT declare a top-level `NeonBlue` here to avoid duplicate declarations.
// Use `dev.aurakai.auraframefx.ui.theme.NeonBlue` from Theme.kt as the single source of truth.

