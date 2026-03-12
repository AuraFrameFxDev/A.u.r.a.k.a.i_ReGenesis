# AuraKai Sprite Assets & Stage Layout

## Aura Character Sprites (Root Directory)

Animation assets for Aura persona in the Mega Man-style ROM Tools backdrop.

### Available Sprites

| Sprite File | Animation State | Use Case | Size |
|-------------|----------------|----------|------|
| `aurastance.png` | Standing/Idle | Default pose on top platform | 1.1MB |
| `aura walking.png` | Walking | Movement animation | 1.1MB |
| `aurafloating.png` | Floating/Hover | When platforms move | 1.1MB |
| `aurapanic.png` | Panic/Flail | When Kai dodges attacks | 1.1MB |
| `aura4th wall.png` | 4th Wall Break | Meta interaction with user | 1.1MB |
| `aura4thwall2.png` | 4th Wall Break (Alt) | Meta interaction variant | 1.2MB |
| `aurasecret2.png` | Secret/Special | Hidden animation trigger | 1.1MB |
| `aurasnackbreak.png` | Snack Break | Idle variation | 1.1MB |

## Chutes & Ladders Stage Layout

Reference images for the Mega Man-style backdrop stage design:

### Primary Stage Reference ⭐

**`Screenshot 2025-11-09 015218.png`** (533KB) - **MAIN REFERENCE IMAGE**

This is the definitive stage layout showing:
- Construction cones flying in multiple trajectories
- Ladder systems connecting all platform levels
- Chute/slide mechanics for rapid descent
- Cyber retro pixel art aesthetic (purple grid background)
- Character sprite navigating through obstacles
- Platform collision zones and level design
- Cyberpunk cityscape background buildings
- Perfect match for our MegaManBackdropRenderer implementation

### Additional Stage Photos (Root Directory)

| File | Description |
|------|-------------|
| `photo_2025-11-06_08-03-29.jpg` | Stage layout reference |
| `photo_2025-11-07_12-39-21.jpg` | Chute mechanics reference |
| `photo_2025-11-07_12-39-21 (2).jpg` | Ladder positioning |
| `photo_2025-11-07_12-39-21 (3).jpg` | Platform layout |
| `photo_2025-11-07_12-39-22.jpg` | Full stage view |
| `photo_2025-11-07_12-39-22 (2).jpg` | Chute slide physics reference |
| `photo_2025-11-07_12-39-22 (3).jpg` | Construction cone placement |

## Implementation Files

The sprite assets integrate with:

- **MegaManBackdropRenderer.kt** - Main 60 FPS Canvas renderer
- **MegaManPalette.kt** - Cyber blue/orange color scheme
- **TeleportationEffect.kt** - Kai teleport animation when hit 3+ times
- **ConstructionCone.kt** - Physics-based cone projectiles with chute support

## Stage Mechanics

### Chutes (Red Slides)
- 2x gravity speed (800 px/s²)
- Diagonal slide at ~45° angle
- Cones accelerate when entering chute
- Kai slides down when hit

### Ladders (Steel Climbing)
- Kai climbs based on ROM operation progress (0-100%)
- Two ladder sections: 300-600px, 100-300px
- Synced to `operationProgress` state

### Platforms
- Cyber theme (dark 0xFF2B2B40, highlight 0xFF4A4A6A)
- Collision detection for cone bouncing
- Static layout (future: moving platforms)

## Animation Integration Plan

Currently using **placeholder circle rendering** for Aura and Kai.

### Next Steps:
1. Convert PNG sprites to 16x16 pixel art format
2. Create sprite sheet with 16 frames per character
3. Integrate into `drawAura()` and `drawKai()` functions
4. Add frame animation loop at 12 FPS
5. Map animation states to game events

### Budget Estimate:
- Sprite sheet conversion: ~$20
- Animation state machine: ~$15
- Sound effects (optional): ~$15

## Related Features

- **Issue #134**: Mega Man ROM Tools backdrop
- **Issue #94**: Production readiness (100% complete)
- **FEATURE_NES_CONSTRUCTION_BACKDROP.md**: Full design documentation

---

**Status**: Sprite assets committed and ready for integration
**Last Updated**: 2025-11-08
**Branch**: `claude/embodiment-assets-integration-011CUoxSK2C1H5ABJbkXrmpa`
