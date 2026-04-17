package dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.color.iconify.iconify

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 🎨 ICON PICKER — ChromaCore Icon Browser
 *
 * Composable icon picker for selecting Iconify icons within the Design Studio.
 *
 * Stub implementation — replace with real Iconify browser when the
 * Iconify Android SDK is integrated.
 */
@Composable
fun IconPicker(
    iconifyService: IconifyService,
    currentIcon: String?,
    onIconSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val icons = remember { iconifyService.getAllIconIds() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Icon Picker",
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (icons.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No icons available.\nIntegrate Iconify SDK to enable icon browsing.",
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                modifier = Modifier.fillMaxSize()
            ) {
                items(icons) { iconId ->
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(48.dp)
                            .clickable { onIconSelected(iconId) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = iconId.take(2), color = Color.White, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}
