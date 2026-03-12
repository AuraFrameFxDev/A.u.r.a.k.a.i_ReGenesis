package dev.aurakai.auraframefx.domains.aura.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import coil3.compose.AsyncImage
import dev.aurakai.auraframefx.domains.aura.ui.theme.LEDFontFamily
import kotlin.math.absoluteValue

/**
 * 🎠 REGENESIS SUB-GATE CAROUSEL
 * 
 * A high-end replacement for the legacy carousel.
 * Uses HorizontalPager with 3D transformations for a premium feel.
 */
@Composable
fun DomainSubGateCarousel(
    subGates: List<SubGateCard>,
    onGateSelected: (SubGateCard) -> Unit,
    useStyleB: Boolean = false,
    cardHeight: Dp = 300.dp,
    domainColor: Color = Color(0xFF00E5FF),
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { subGates.size })
    val context = LocalContext.current

    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 48.dp),
        pageSpacing = 16.dp
    ) { page ->
        val gate = subGates[page]
        val pageOffset =
            ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue

        val scale = lerp(0.85f, 1f, 1f - pageOffset.coerceIn(0f, 1f))
        val alpha = lerp(0.5f, 1f, 1f - pageOffset.coerceIn(0f, 1f))
        val rotationY = lerp(
            0f,
            30f,
            pageOffset.coerceIn(0f, 1f)
        ) * (if (page < pagerState.currentPage) 1f else -1f)

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(cardHeight)
                .graphicsLayer {
                    this.scaleX = scale
                    this.scaleY = scale
                    this.alpha = alpha
                    this.rotationY = rotationY
                    this.cameraDistance = 8 * density
                }
                .clickable { onGateSelected(gate) },
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.6f)
            ),
            border = CardDefaults.outlinedCardBorder().copy(
                width = 2.dp,
                brush = androidx.compose.ui.graphics.SolidColor(domainColor.copy(alpha = 0.8f))
            )
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Background Image (Style A or B)
                val drawableName = if (useStyleB) gate.styleBDrawable else gate.styleADrawable
                val resId = context.resources.getIdentifier(
                    drawableName,
                    "drawable",
                    context.packageName
                )

                if (resId != 0) {
                    AsyncImage(
                        model = resId,
                        contentDescription = gate.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Overlay Gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.8f)
                                )
                            )
                        )
                )

                // Info
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(24.dp)
                ) {
                    Text(
                        text = gate.title.uppercase(),
                        fontFamily = LEDFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = gate.subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = domainColor,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "ACCESS GATE",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier
                            .background(
                                color = domainColor.copy(alpha = 0.2f),
                                shape = MaterialTheme.shapes.extraSmall
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
