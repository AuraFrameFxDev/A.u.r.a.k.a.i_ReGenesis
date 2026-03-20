package dev.aurakai.auraframefx.domains.aura.screens

import androidx.compose.runtime.Composable

// import androidx.compose.ui.tooling.preview.Preview

@Composable
fun IntroScreen(onIntroComplete: () -> Unit) { // Renamed to introScreen
    // Kept only as a backwards-compatible alias for older imports.
    // The real intro animation lives in `domains.aura.ui.intro.IntroScreen`.
    dev.aurakai.auraframefx.domains.aura.ui.intro.IntroScreen(onIntroComplete = onIntroComplete)
}

// @Preview(showBackground = true)
// @Composable
// fun IntroScreenPreview() { // Renamed
//     IntroScreen()
// }
