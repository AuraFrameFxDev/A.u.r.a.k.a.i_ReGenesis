package dev.aurakai.auraframefx.domains.aura.screens

import androidx.compose.runtime.Composable
import dev.aurakai.auraframefx.domains.aura.ui.intro.VideoIntroScreen

@Composable
fun IntroScreen(
    onComplete: () -> Unit = {}
) {
    VideoIntroScreen(onComplete = onComplete)
}

// @Preview(showBackground = true)
// @Composable
// fun IntroScreenPreview() { // Renamed
//     IntroScreen()
// }
