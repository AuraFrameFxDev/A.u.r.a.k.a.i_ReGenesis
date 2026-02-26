package dev.aurakai.auraframefx.domains.aura.ui.components.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily

/**
 * 🛰️ NEURAL LINK SIDEBAR
 * A futuristic Command Deck that replaces the traditional bubble.
 */
@Composable
fun NeuralLinkSidebarUI(
    isVisible: Boolean,
    onVisibleChange: (Boolean) -> Unit,
    onActionClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp), // Adjust width as needed, poster says 380dp but 280dp might be better for mobile sidebar
        contentAlignment = Alignment.CenterEnd
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = expandHorizontally(expandFrom = Alignment.End) + fadeIn(),
            exit = shrinkHorizontally(shrinkTowards = Alignment.End) + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 100.dp, horizontal = 16.dp)
                    .width(380.dp) // Following the poster's "380dp slide-out bridge" literally if width allows
                    .clip(RoundedCornerShape(topStart = 40.dp, bottomStart = 40.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .border(
                        1.dp,
                        Brush.verticalGradient(
                            listOf(Color(0xFF00FFFF), Color(0xFFFF00FF))
                        ),
                        RoundedCornerShape(topStart = 40.dp, bottomStart = 40.dp)
                    )
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "NEURAL LINK",
                    fontFamily = LEDFontFamily,
                    color = Color.Cyan,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 4.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    "SIDEBAR COMMAND DECK",
                    fontFamily = LEDFontFamily,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 10.sp,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                SidebarCommandItem(
                    "VOICE",
                    Icons.Default.Mic,
                    Color(0xFF00FFFF)
                ) { onActionClick("VOICE") }
                SidebarCommandItem(
                    "CONNECT",
                    Icons.Default.Link,
                    Color(0xFF00FFFF)
                ) { onActionClick("CONNECT") }
                SidebarCommandItem(
                    "ASSIGN",
                    Icons.Default.PersonAdd,
                    Color(0xFFFF00FF)
                ) { onActionClick("ASSIGN") }
                SidebarCommandItem(
                    "DESIGN",
                    Icons.Default.Brush,
                    Color(0xFFFF00FF)
                ) { onActionClick("DESIGN") }
                SidebarCommandItem(
                    "CREATE",
                    Icons.Default.AddBox,
                    Color(0xFFFF00FF)
                ) { onActionClick("CREATE") }

                Spacer(modifier = Modifier.height(32.dp))

                // Close button/tab
                IconButton(onClick = { onVisibleChange(false) }) {
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        "Close",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        // Handle trigger (invisible area or small tab on the side)
        if (!isVisible) {
            Box(
                modifier = Modifier
                    .width(12.dp)
                    .fillMaxHeight(0.3f)
                    .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF00FFFF).copy(alpha = 0.5f),
                                Color(0xFFFF00FF).copy(alpha = 0.5f)
                            )
                        )
                    )
                    .clickable { onVisibleChange(true) }
            )
        }
    }
}

@Composable
private fun SidebarCommandItem(
    label: String,
    icon: ImageVector,
    glowColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() }
            .height(60.dp),
        color = Color.Transparent,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, glowColor.copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(glowColor.copy(alpha = 0.1f), Color.Transparent)
                    )
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.padding(start = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = glowColor,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = label,
                    color = Color.White,
                    fontFamily = LEDFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }
        }
    }
}
