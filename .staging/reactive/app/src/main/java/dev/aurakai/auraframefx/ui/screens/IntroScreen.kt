package dev.aurakai.auraframefx.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

// import androidx.compose.ui.tooling.preview.Preview

@Composable
fun IntroScreen() { // Renamed to introScreen
    // TODO: Implement the actual Intro Screen UI
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Intro Screen (Placeholder)")
    }
}

// @Preview(showBackground = true)
// @Composable
// fun IntroScreenPreview() { // Renamed
//     IntroScreen()
// }
