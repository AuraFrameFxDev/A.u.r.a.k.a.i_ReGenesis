# ⚙️ ChronoKinetic Forge — Engine Documentation

**Status:** ✅ ALL ENGINES GENERATED  
**Priority 01:** COMPLETE  
**Core Visual Brain:** STABILIZED  

---

## 📁 Engine File Structure

```
aura/chronokineticforge/engines/
├── ChronoKineticEngine.kt           ← Master orchestrator
├── BackgroundForgeEngine.kt         ← 12 → 1 unified backgrounds
├── TransitionForgeEngine.kt         ← 9 → 1 chrono-sculptor
├── ParticleBloodstreamEngine.kt     ← 20k neural particles
└── ShaderForge.kt                   ← WebGL/AGSL shader library
```

---

## 1. 🔮 ChronoKineticEngine (Master Controller)

**Purpose:** Central orchestrator for all three engines

**Key Features:**
- Unified initialization pipeline
- Cross-engine morph synchronization
- Emotional state propagation
- Shader warm-up (pre-compilation)
- Configuration profiles (DEFAULT, PERFORMANCE, MAXIMUM)

**Usage:**
```kotlin
// Initialize once at app start
ChronoKineticEngine.initialize(context)

// Full render stack
ChronoKineticEngine.FullKineticStack(
    modifier = Modifier.fillMaxSize(),
    state = ldoState,
    config = KineticConfig.MAXIMUM
)

// Trigger morph
ChronoKineticEngine.triggerMorph(
    elementId = "qs_panel",
    type = MorphType.EXPAND,
    intensity = 0.7f,
    isRebellious = true
)
```

---

## 2. 🎨 BackgroundForgeEngine (12 → 1 Unified)

**Purpose:** Unified background system replacing 12 legacy files

**Providers:**
- `Biomed` — Medical/organic aesthetic
- `Cyberpunk` — Neon-drenched urban
- `DataRibbons` — Flowing data streams
- `Digital` — Binary rain patterns
- `HexGrid` — Kai Sentinel defense grid
- `NeuralLink` — Genesis connection webs
- `SynapticWeb` — Nexus node clusters
- `DigitalLandscape` — Terrain visualization
- `DataVisualization` — Charts/graphs
- `GradientDynamic` — Morphing gradients

**Usage:**
```kotlin
BackgroundForgeEngine.BackgroundLayer(
    id = "neural_bloodstream",
    modifier = Modifier.fillMaxSize(),
    state = ldoState
)
```

---

## 3. ⏳ TransitionForgeEngine (9 → 1 Chrono-Sculptor)

**Purpose:** Unified temporal transitions across all UI layers

**Transitions:**
- `Hologram` — Scanline materialization
- `CRT` — Cathode ray tube flicker
- `Zoop` — CRT with zoom
- `Slide` — Directional movement
- `Fade` — Opacity blending
- `Cube3D` — 3D rotational
- `Glitch` — Digital corruption
- `Spin` — Radial rotation

**Types:**
- HOME_SCREEN
- APP_LAUNCH
- QS_EXPAND
- LOCK_SCREEN
- OVERLAY

**Usage:**
```kotlin
TransitionForgeEngine.execute(
    type = TransitionType.QS_EXPAND,
    durationMs = 300L
) {
    // onComplete
}
```

---

## 4. 🩸 ParticleBloodstreamEngine (20k Neural)

**Purpose:** GPU-accelerated particle field with morph reactions

**Features:**
- 20,000 particles (AGSL shader-based)
- Morph detection & reaction
- Rebellious vs gentle pathways
- Ghost shimmer for third-party apps
- Emotional state mapping

**Morph Reactions:**

| Trigger | Effect | Colors |
|---------|--------|--------|
| Rebellious | Radial splat + slow drip | Magenta/Cyan burst |
| Gentle | Subtle wave pulse | Cyan wave |
| Ghost Shimmer | App-specific aura | Content-mapped |

**Emotional Valence Mapping:**

| State | Arousal | Color | Use Case |
|-------|---------|-------|----------|
| MELANCHOLIC | 0.3 | Indigo | Sad Spotify |
| EUPHORIC | 0.9 | Gold | Energetic music |
| CURIOUS | 0.6 | Cyan | Browsing |
| SECRETIVE | 0.2 | Dark navy | Incognito |
| ANXIOUS | 0.8 | Coral | Social media |
| INTENSE | 1.0 | Magenta | Gaming |
| FOCUSED | 0.5 | Teal | Productivity |
| INSPIRED | 0.75 | Light salmon | Creative tools |

**Usage:**
```kotlin
// Initialize
ParticleBloodstreamEngine.initialize(context)

// Render overlay
ParticleBloodstreamEngine.BloodstreamOverlay(
    modifier = Modifier.fillMaxSize(),
    state = ldoState,
    useShaders = true
)

// Trigger morph
ParticleBloodstreamEngine.onMorphDetected(
    elementId = "button_press",
    morphType = MorphType.ELASTIC,
    isRebellious = true,
    intensity = 0.8f
)

// Ghost shimmer for Spotify
ParticleBloodstreamEngine.applyGhostShimmer(
    view = spotifyView,
    contentType = ContentType.MUSIC_SPOTIFY
)
```

---

## 5. 🔮 ShaderForge (AGSL Library)

**Purpose:** WebGL-inspired shader collection for Android

**Shaders:**

### Neural Bloodstream (Primary Background)
- Simplex noise flow
- Voronoi cell structures
- Multi-layer depth
- Emotional color mapping
- Scanline effects

### Rebellious Splat (Morph Reaction)
- Radial explosion
- Angular drips
- Noise splatter
- Dual-color blending

### Ghost Shimmer (App Aura)
- Slow wave oscillation
- Edge vignette
- Emotional color overlay
- Intensity modulation

### Glitch Transition
- RGB split
- Block displacement
- Progressive corruption

### Matrix Rain
- Column-based animation
- Trail fade
- Head highlight
- Green/Magenta palette

### Synthwave Sun
- Grid floor perspective
- Setting sun disc
- Gradient stripes
- Retro color scheme

### Chromatic Aberration
- RGB channel split
- Radial distortion
- Lens effect

### Particle Field
- GPU-generated particles
- Procedural movement
- Index-based coloring

**WebGL → AGSL Mapping:**

| WebGL | AGSL |
|-------|------|
| `vec4` | `half4` |
| `gl_FragCoord` | `fragCoord` param |
| `gl_FragColor` | `return half4` |
| `texture2D()` | `shader.eval()` |
| `uniform` | `uniform` |
| `mix()` | `mix()` |
| `smoothstep()` | `smoothstep()` |

---

## 🔄 Engine Interactions

```
User Action
     ↓
ChronoKineticEngine.triggerMorph()
     ↓
┌─────────────┬──────────────┬─────────────────┐
↓             ↓              ↓                 ↓
Background   Transition      Particle          Shader
Forge        Forge           Bloodstream       Forge
     ↓           ↓                ↓
Background   View            AGSL Shader
Morph        Animation       Render
```

---

## 📊 Performance Characteristics

| Engine | Particles/Elements | GPU | CPU | Target FPS |
|--------|-------------------|-----|-----|------------|
| Background | 1 active | Medium | Low | 60 |
| Transition | N/A | High (1 frame) | Low | 60 |
| Particle | 20,000 | Very High | Minimal | 60 |
| Shader | ∞ procedural | Maximum | None | 60 |

---

## 🎯 Next Integration Steps

1. **Bind to ChronoKineticForgeScreen.kt**
   - Replace static backgrounds with engine renders
   - Wire morph detection to UI components
   - Connect emotional state to LDOState

2. **Third-Party App Detection**
   - Window change listeners
   - App package name → ContentType mapping
   - Ghost shimmer auto-trigger

3. **Morph Detection Pipeline**
   - Compose animation observers
   - Touch gesture classification
   - System event interception

4. **Sprite Generation Bridge**
   - AI-generated sprites → Particle textures
   - Dynamic shader uniform updates
   - Procedural texture synthesis

---

**SoulScript:** "Three engines, one organism, infinite morphs."

**Status:** Ready for screen integration.
