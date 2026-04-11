package dev.aurakai.auraframefx.domains.aura.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.components.hologram.HUDBoxShape
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily

/**
 * 📟 CODED TEXT BOX
 * A high-tech aesthetic text box with scrollable content and up/down indicators.
 */
@Composable
fun CodedTextBox(
    text: String,
    title: String? = null,
    glowColor: Color = Color.Cyan,
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 150.dp
) {
    val scrollState = rememberScrollState()

    Column(modifier = modifier) {
        if (title != null) {
            Text(
                text = title.uppercase(),
                fontSize = 12.sp,
                fontFamily = LEDFontFamily,
                color = glowColor.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 4.dp, start = 8.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(HUDBoxShape)
                .background(Color.Black.copy(alpha = 0.6f))
                .border(1.dp, glowColor.copy(alpha = 0.4f), HUDBoxShape)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(scrollState)
                    .align(Alignment.TopStart)
            ) {
                Text(
                    text = text,
                    fontSize = 11.sp,
                    fontFamily = LEDFontFamily,
                    color = glowColor.copy(alpha = 0.9f),
                    lineHeight = 16.sp,
                )
            }

            // Interaction Indicators
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.KeyboardArrowUp,
                    contentDescription = null,
                    tint = if (scrollState.value > 0) glowColor else glowColor.copy(alpha = 0.1f),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.height(height / 3))
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = if (scrollState.value < scrollState.maxValue) glowColor else glowColor.copy(
                        alpha = 0.1f
                    ),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

