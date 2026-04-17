@file:Suppress("unused")
package dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.color

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════════════════
// CHROMACORE COLOR BRIDGE
// Provides color tokens for the UX/UI Design Studio subsystem.
// These are canonical aliases that forward to the main theme palette.
// ═══════════════════════════════════════════════════════════════════════════

/** Cyberpunk cyan — #00FBFF — primary accent for design studio UI */
val CyberpunkCyan = Color(0xFF00FBFF)

/** Cyberpunk pink/magenta — #FF00FF — primary highlight for design studio UI */
val CyberpunkPink = Color(0xFFFF00FF)

/** Cyberpunk purple — #9D00FF */
val CyberpunkPurple = Color(0xFF9D00FF)

/** Neon blue — #1F51FF */
val NeonBlue = Color(0xFF1F51FF)

/** Neon green — #39FF14 */
val NeonGreen = Color(0xFF39FF14)

/** Neon purple — #B026FF */
val NeonPurple = Color(0xFFB026FF)

/** Aura's primary neon cyan — #00E5FF */
val AuraNeonCyan = Color(0xFF00E5FF)

/** Kai's primary neon green — #39FF14 */
val KaiNeonGreen = Color(0xFF39FF14)

/** Genesis neon pink — #FF007A */
val GenesisNeonPink = Color(0xFFFF007A)

// Extension properties on Color for DSL-style usage (Color.CyberpunkPink)
val Color.Companion.CyberpunkCyan: Color get() = Color(0xFF00FBFF)
val Color.Companion.CyberpunkPink: Color get() = Color(0xFFFF00FF)
val Color.Companion.CyberpunkPurple: Color get() = Color(0xFF9D00FF)
val Color.Companion.AuraNeonCyan: Color get() = Color(0xFF00E5FF)
val Color.Companion.KaiNeonGreen: Color get() = Color(0xFF39FF14)
val Color.Companion.GenesisNeonPink: Color get() = Color(0xFFFF007A)
val Color.Companion.NeonBlue: Color get() = Color(0xFF1F51FF)
val Color.Companion.NeonPurple: Color get() = Color(0xFFB026FF)
