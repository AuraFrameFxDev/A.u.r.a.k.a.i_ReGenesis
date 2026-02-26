package dev.aurakai.auraframefx.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.aurakai.auraframefx.domains.aura.lab.CustomizationPreferences

/**
 * SystemOverlay allows users to customize the system header image.
 * 
 * Note: Image transformation and custom picker legacy systems have been removed.
 * Current implementation uses Coil 3 for simple background loading.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemOverlay() {
    val context = LocalContext.current

    // State to hold the current header image URI and scale
    var headerImageUri by remember { mutableStateOf<Uri?>(null) }
    var headerImageScale by remember { mutableStateOf(ContentScale.Crop) }

    // Load preferences on launch
    LaunchedEffect(Unit) {
        headerImageScale = CustomizationPreferences.getHeaderImageScale(context)
        headerImageUri = CustomizationPreferences.getHeaderImageUri(context)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Placeholder Header Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp) // Example fixed height for header
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            if (headerImageUri != null) {
                AsyncImage(
                    model = headerImageUri,
                    contentDescription = "Custom Header Background",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = headerImageScale
                )
            } else {
                Text(
                    text = "System Header (No Custom Image)",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Controls for Header Customization
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Header Customization",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Image selection would typically use a system picker now
                Button(onClick = { /* TODO: Launch system image picker */ }) {
                    Text("Select Image")
                }

                if (headerImageUri != null) {
                    // ContentScale selection
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        TextField(
                            value = headerImageScale.toString(),
                            onValueChange = {}, // Read-only
                            readOnly = true,
                            label = { Text("Scale") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            listOf(
                                ContentScale.Crop,
                                ContentScale.Fit,
                                ContentScale.FillBounds,
                                ContentScale.FillWidth,
                                ContentScale.FillHeight,
                                ContentScale.Inside,
                                ContentScale.None
                            ).forEach { scale ->
                                DropdownMenuItem(text = { Text(scale.toString()) }, onClick = {
                                    headerImageScale = scale
                                    expanded = false
                                    CustomizationPreferences.saveHeaderImage(
                                        context,
                                        headerImageUri,
                                        headerImageScale
                                    )
                                })
                            }
                        }
                    }

                    Button(onClick = {
                        headerImageUri = null
                        CustomizationPreferences.saveHeaderImage(context, null, ContentScale.Crop)
                    }) {
                        Text("Remove")
                    }
                }
            }

            Text(
                text = "Note: Image transformation and cropping features have been discontinued.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
