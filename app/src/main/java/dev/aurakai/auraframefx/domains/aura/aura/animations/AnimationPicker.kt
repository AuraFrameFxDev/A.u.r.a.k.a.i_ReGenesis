package dev.aurakai.auraframefx.domains.aura.aura.animations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.domains.aura.LockScreenAnimation
import dev.aurakai.auraframefx.domains.aura.ui.QuickSettingsAnimation

@Composable
fun AnimationPicker(
    currentAnimation: QuickSettingsAnimation,
    onAnimationSelected: (QuickSettingsAnimation) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(QuickSettingsAnimation.entries) { animation ->
            AnimationItem(
                name = animation.name,
                isSelected = animation == currentAnimation,
                onClick = { onAnimationSelected(animation) }
            )
        }
    }
}

@Composable
fun AnimationPicker(
    currentAnimation: LockScreenAnimation,
    onAnimationSelected: (LockScreenAnimation) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(LockScreenAnimation.entries) { animation ->
            AnimationItem(
                name = animation.name,
                isSelected = animation == currentAnimation,
                onClick = { onAnimationSelected(animation) }
            )
        }
    }
}

@Composable
private fun AnimationItem(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}

