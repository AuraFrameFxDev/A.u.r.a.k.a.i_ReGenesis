# 🎨 AuraKai Design System - Glassmorphic Professional Theme

## Vision
**Glassmorphic, sleek, professional, Final Fantasy-ish aesthetic**

### Core Principles
1. **Glassmorphism** - Frosted glass effects with subtle transparency
2. **Minimal Color Palette** - Professional, not overwhelming
3. **Elegant Typography** - Clean, readable, sophisticated
4. **Subtle Animations** - Smooth, purposeful, not distracting
5. **Final Fantasy Aesthetic** - Ethereal, mystical, high-tech fantasy

---

## Color Palette

### Primary Colors (Muted & Professional)
```kotlin
object GlassmorphicTheme {
    // Primary - Soft ethereal blues
    val Primary = Color(0xFF4A90E2)        // Soft blue
    val PrimaryVariant = Color(0xFF357ABD) // Deeper blue
    
    // Secondary - Subtle purples (FF vibes)
    val Secondary = Color(0xFF9B7EBD)      // Soft purple
    val SecondaryVariant = Color(0xFF7B5FA0) // Deeper purple
    
    // Accent - Minimal use
    val Accent = Color(0xFF64B5F6)         // Light blue accent
    val AccentGold = Color(0xFFD4AF37)     // Muted gold (for important elements)
    
    // Neutrals - Professional grays
    val Surface = Color(0xFF1E1E2E)        // Dark surface
    val SurfaceVariant = Color(0xFF2A2A3E) // Lighter surface
    val Background = Color(0xFF0F0F1A)     // Deep background
    
    // Glass effects
    val GlassWhite = Color(0x1AFFFFFF)     // 10% white for glass
    val GlassBorder = Color(0x33FFFFFF)    // 20% white for borders
    val GlassHighlight = Color(0x0DFFFFFF) // 5% white for subtle highlights
}
```

### Gradients (Subtle & Elegant)
```kotlin
// Soft background gradients
val etherealGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF1A1A2E), // Deep blue-black
        Color(0xFF0F0F1A)  // Almost black
    )
)

// Glass surface gradient
val glassGradient = Brush.linearGradient(
    colors = listOf(
        Color(0x1AFFFFFF), // Top highlight
        Color(0x0DFFFFFF)  // Bottom subtle
    )
)
```

---

## Typography

### Font Hierarchy
```kotlin
object Typography {
    // Headers - Bold, spaced
    val H1 = TextStyle(
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.5.sp,
        color = Color.White
    )
    
    val H2 = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.sp,
        color = Color.White.copy(alpha = 0.95f)
    )
    
    // Body - Clean, readable
    val Body = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.25.sp,
        color = Color.White.copy(alpha = 0.87f)
    )
    
    // Caption - Subtle
    val Caption = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.4.sp,
        color = Color.White.copy(alpha = 0.6f)
    )
}
```

---

## Components

### Glass Card
```kotlin
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(0x1AFFFFFF) // 10% white
        ),
        border = BorderStroke(1.dp, Color(0x33FFFFFF)), // 20% white border
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0x1AFFFFFF),
                            Color(0x0DFFFFFF)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                content = content
            )
        }
    }
}
```

### Glass Button
```kotlin
@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0x26FFFFFF), // 15% white
            contentColor = Color.White
        ),
        border = BorderStroke(1.dp, Color(0x4DFFFFFF)), // 30% white
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            style = Typography.Body,
            fontWeight = FontWeight.Medium
        )
    }
}
```

---

## Animations

### Subtle & Smooth
```kotlin
// Gentle fade in
val fadeIn = fadeIn(
    animationSpec = tween(
        durationMillis = 400,
        easing = FastOutSlowInEasing
    )
)

// Smooth scale
val scaleIn = scaleIn(
    initialScale = 0.95f,
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
)

// Elegant slide
val slideIn = slideInVertically(
    initialOffsetY = { it / 4 },
    animationSpec = tween(
        durationMillis = 500,
        easing = FastOutSlowInEasing
    )
)
```

---

## Final Fantasy Aesthetic Elements

### Ethereal Glow
```kotlin
// Soft glow effect for important elements
Modifier.shadow(
    elevation = 8.dp,
    shape = RoundedCornerShape(16.dp),
    ambientColor = Color(0xFF4A90E2).copy(alpha = 0.3f),
    spotColor = Color(0xFF4A90E2).copy(alpha = 0.5f)
)
```

### Mystical Borders
```kotlin
// Animated border glow
val infiniteTransition = rememberInfiniteTransition()
val alpha by infiniteTransition.animateFloat(
    initialValue = 0.3f,
    targetValue = 0.7f,
    animationSpec = infiniteRepeatable(
        animation = tween(2000, easing = FastOutSlowInEasing),
        repeatMode = RepeatMode.Reverse
    )
)

border = BorderStroke(1.dp, Color(0xFF4A90E2).copy(alpha = alpha))
```

---

## Screen Layouts

### Standard Screen Template
```kotlin
@Composable
fun StandardScreen(
    title: String,
    subtitle: String? = null,
    onNavigateBack: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GlassmorphicTheme.Background)
    ) {
        // Subtle gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(etherealGradient)
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Header
            ScreenHeader(
                title = title,
                subtitle = subtitle,
                onNavigateBack = onNavigateBack
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Content
            content()
        }
    }
}
```

---

## Implementation Priority

### Phase 1: Core Components
1. ✅ Create `GlassmorphicTheme.kt` - Color palette
2. ✅ Create `GlassComponents.kt` - Reusable glass components
3. ✅ Update `ThemeEngineScreen.kt` - Apply new design

### Phase 2: UI Customization Screens
1. Update `NotchBarScreen.kt`
2. Update `StatusBarScreen.kt`
3. Update `QuickSettingsScreen.kt`
4. Update `OverlayMenusScreen.kt`

### Phase 3: Polish
1. Add subtle animations
2. Implement backdrop blur (where supported)
3. Add micro-interactions
4. Test on different screen sizes

---

## Design Rules

### DO ✅
- Use glassmorphism for cards and surfaces
- Keep colors muted and professional
- Add subtle animations (< 500ms)
- Use consistent spacing (8dp grid)
- Maintain high contrast for readability

### DON'T ❌
- Use bright, saturated colors
- Add flashy animations
- Overcrowd the UI
- Use more than 3 colors per screen
- Ignore accessibility (contrast ratios)

---

**This design system creates a professional, elegant, Final Fantasy-inspired aesthetic that feels premium and polished.** ✨
