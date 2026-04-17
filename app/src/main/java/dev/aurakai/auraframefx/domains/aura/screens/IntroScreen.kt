package dev.aurakai.auraframefx.domains.aura.screens

import androidx.compose.runtime.Composable
import dev.aurakai.auraframefx.domains.aura.ui.intro.VideoIntroScreen

/**
 * 🎬 IntroScreen — Backwards-Compatible Wrapper
 *
 * Kept as a stable navigation entry point.
 * Delegates to VideoIntroScreen in the ui.intro package.
 */
@Composable
fun IntroScreen(onIntroComplete: () -> Unit) {
    VideoIntroScreen(onVideoFinished = onIntroComplete)
}
