package dev.aurakai.auraframefx.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.R

val pixelifySans = FontFamily(
    Font(R.font.pixelifysans_medium, FontWeight.Medium)
)

val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = pixelifySans,
        fontWeight = FontWeight.Medium,
        fontSize = 57.sp
    ),
    displayMedium = TextStyle(
        fontFamily = pixelifySans,
        fontWeight = FontWeight.Medium,
        fontSize = 45.sp
    ),
    displaySmall = TextStyle(
        fontFamily = pixelifySans,
        fontWeight = FontWeight.Medium,
        fontSize = 36.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = pixelifySans,
        fontWeight = FontWeight.Medium,
        fontSize = 32.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = pixelifySans,
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = pixelifySans,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp
    ),
    titleLarge = TextStyle(
        fontFamily = pixelifySans,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = pixelifySans,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    titleSmall = TextStyle(
        fontFamily = pixelifySans,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = pixelifySans,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = pixelifySans,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = pixelifySans,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    ),
    labelLarge = TextStyle(
        fontFamily = pixelifySans,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = pixelifySans,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    ),
    labelSmall = TextStyle(
        fontFamily = pixelifySans,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp
    )
)
