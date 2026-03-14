package dev.aurakai.auraframefx.screens

import dev.aurakai.auraframefx.ui.StandardPlaceholder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PlaceholderScreen(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = subtitle,
            style = typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// New overload to accept callers that pass an onBack lambda (common in navigation wiring)
@Suppress("unused")
@Composable
fun PlaceholderScreen(title: String, onBack: () -> Boolean) {
    // Delegate to centralized StandardPlaceholder and pass the onBack callback
    StandardPlaceholder(title = title, subtitle = "(Coming soon)") { onBack() }
}
