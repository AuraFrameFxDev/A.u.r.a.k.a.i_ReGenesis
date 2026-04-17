package dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.color

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ═══════════════════════════════════════════════════════════════════════════
// CYBERPUNK TEXT COLOR — ChromaCore Design Studio
// ═══════════════════════════════════════════════════════════════════════════

/**
 * 🎨 CyberpunkTextColor
 *
 * Typed color token for ChromaCore text rendering.
 * Used by CyberpunkText composable to enforce design system color discipline.
 */
enum class CyberpunkTextColor(val color: Color) {
    CYAN(Color(0xFF00FBFF)),
    PINK(Color(0xFFFF00FF)),
    PURPLE(Color(0xFF9D00FF)),
    GREEN(Color(0xFF39FF14)),
    GOLD(Color(0xFFFFD700)),
    WHITE(Color(0xFFFFFFFF)),
    GHOST(Color(0xCCFFFFFF)),
    ERROR(Color(0xFFFF4444)),
    WARNING(Color(0xFFFFAA00)),
    INACTIVE(Color(0xFF666666)),
}

/** Neon Teal — used in design studio UI elements */
val NeonTeal = Color(0xFF00E5CC)
