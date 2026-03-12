package dev.aurakai.auraframefx.domains.aura.ui.theme.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.domains.aura.ui.theme.AuraFrameFXTheme
import dev.aurakai.auraframefx.domains.aura.ui.theme.picker.ColorBlendrPicker

@Preview(showBackground = true)
@Composable
fun ColorBlendrPickerPreview() {
    AuraFrameFXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            var selectedColor by remember { mutableStateOf(Color.Cyan) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Selected Color",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Color preview box
                Surface(
                    modifier = Modifier
                        .size(100.dp)
                        .padding(bottom = 32.dp),
                    color = selectedColor,
                    shape = MaterialTheme.shapes.medium,
                    shadowElevation = 8.dp
                ) {}

                // Color picker button
                Text(
                    text = "Tap to change color",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // The actual ChromaCore picker
                ColorBlendrPicker(
                    initialColor = selectedColor,
                    onColorSelected = { selectedColor = it },
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}

