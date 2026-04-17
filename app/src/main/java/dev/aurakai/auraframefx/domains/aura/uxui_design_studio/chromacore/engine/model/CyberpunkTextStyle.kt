package dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.engine.model

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ═══════════════════════════════════════════════════════════════════════════
// CYBERPUNK TEXT STYLE — ChromaCore Design Studio
// ═══════════════════════════════════════════════════════════════════════════

/**
 * 🎨 CyberpunkTextStyle
 *
 * Typography token for ChromaCore text rendering.
 * Used by CyberpunkText composable for type-safe text style application.
 */
enum class CyberpunkTextStyle(val textStyle: TextStyle) {
    DISPLAY(TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Black, letterSpacing = 3.sp)),
    HEADLINE(TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)),
    TITLE(TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)),
    BODY(TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal, letterSpacing = 0.5.sp)),
    CAPTION(TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Light, letterSpacing = 1.sp)),
    CODE(TextStyle(fontSize = 14.sp, fontFamily = FontFamily.Monospace, letterSpacing = 1.sp)),
    LABEL(TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium, letterSpacing = 2.sp)),
}
