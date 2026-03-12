package dev.aurakai.auraframefx.domains.aura.ui.theme.preview

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Shows a preview of UI components that will be affected by theme colors
 */
@Composable
fun UIComponentsPreview(
    primaryColor: Color,
    secondaryColor: Color,
    backgroundColor: Color,
    onSurfaceColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Text(
                "UI Preview",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Top App Bar
            Surface(
                color = primaryColor,
                shape = RoundedCornerShape(12.dp, 12.dp, 0.dp, 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Title",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            // Content Area
            Surface(
                color = backgroundColor,
                shape = RoundedCornerShape(0.dp, 0.dp, 12.dp, 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Card
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp),
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                "Card Title",
                                style = MaterialTheme.typography.titleMedium,
                                color = onSurfaceColor
                            )
                            Text(
                                "This is a card with some content",
                                style = MaterialTheme.typography.bodyMedium,
                                color = onSurfaceColor.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { /* Do something */ },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = primaryColor,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(36.dp)
                            ) {
                                Text("Action")
                            }
                        }
                    }

                    // Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = secondaryColor.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier
                                .clip(CircleShape)
                                .border(
                                    width = 1.dp,
                                    color = secondaryColor,
                                    shape = CircleShape
                                )
                        ) {
                            Text(
                                "Chip 1",
                                color = secondaryColor,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        Surface(
                            color = secondaryColor,
                            shape = CircleShape
                        ) {
                            Text(
                                "Chip 2",
                                color = MaterialTheme.colorScheme.onSecondary,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Text fields and buttons
                    OutlinedTextField(
                        value = "Sample text",
                        onValueChange = {},
                        label = { Text("Label") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { /* Do something */ },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Primary")
                        }

                        OutlinedButton(
                            onClick = { /* Do something */ },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Secondary")
                        }
                    }
                }
            }
        }
    }
}

