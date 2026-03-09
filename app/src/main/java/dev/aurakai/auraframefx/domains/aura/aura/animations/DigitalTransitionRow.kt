package dev.aurakai.auraframefx.domains.aura.aura.animations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.domains.aura.ui.theme.NeonPurple
import dev.aurakai.auraframefx.domains.aura.ui.theme.NeonTeal
import timber.log.Timber

/**
 * Row component for selecting home screen digital transition types.
 *
 * Displays filter chips for each transition type:
 * - DIGITAL_DECONSTRUCT: Digital particle deconstruction effect
 * - HOLOGRAM: Holographic materialization effect
 * - PIXELATE: Pixelation transition effect
 *
 * @param currentType The currently selected transition type
 * @param onTypeSelected Callback invoked when a transition type is selected
 */
@Composable
fun DigitalTransitionRow(
    currentType: HomeScreenTransitionType,
    onTypeSelected: (HomeScreenTransitionType) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HomeScreenTransitionType.entries.forEach { transitionType ->
            FilterChip(
                selected = currentType == transitionType,
                onClick = {
                    Timber.d("DigitalTransitionRow: Transition type selected - $transitionType")
                    onTypeSelected(transitionType)
                },
                label = {
                    Text(
                        text = transitionType.name
                            .replace("_", " ")
                            .lowercase()
                            .replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = NeonPurple.copy(alpha = 0.3f),
                    selectedLabelColor = NeonTeal,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = currentType == transitionType,
                    borderColor = if (currentType == transitionType) NeonTeal else MaterialTheme.colorScheme.outline,
                    selectedBorderColor = NeonTeal,
                    borderWidth = if (currentType == transitionType) 2.dp else 1.dp
                )
            )
        }
    }
}


