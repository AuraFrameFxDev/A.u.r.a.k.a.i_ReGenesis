package dev.aurakai.auraframefx.aura.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import dev.aurakai.auraframefx.R // Corrected import
import dev.aurakai.auraframefx.ui.theme.AppDimensions

/**
 * Profile screen for the AuraFrameFX app
 */
/**
 * Displays the user profile screen with a centered icon, title, and placeholder details.
 *
 * Presents a vertically centered layout featuring a large person icon, a "User Profile" headline, and a placeholder message for profile details, styled according to the app's theme.
 */
/**
 * Shows a centered profile screen with a large person icon, a headline, and a placeholder details message.
 *
 * The layout fills available space, centers its content, and applies medium padding from AppDimensions.
 * Text and icon colors are taken from the current Material theme.
 */
@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(AppDimensions.spacing_medium),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = stringResource(R.string.profile),
            modifier = Modifier.size(AppDimensions.icon_size_large * 3),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(AppDimensions.spacing_medium))

        Text(
            text = stringResource(R.string.user_profile),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(AppDimensions.spacing_medium))

        Text(
            text = stringResource(R.string.profile_details_coming_soon),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}