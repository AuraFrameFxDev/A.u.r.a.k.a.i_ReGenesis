# 🎨 ReGenesis Sprite Organization Guide

## Quick Start - Batch Processing

### 1. Install Dependencies
```bash
cd tools/
pip install -r requirements-sprite-tools.txt
```

### 2. Organize Your Raw Sprites First (BEFORE clipping)

Create this folder structure:
```
raw_sprites/
├── genesis/
│   ├── idle/
│   ├── walking/
│   ├── running/
│   ├── climbing/
│   ├── hanging/
│   ├── combat/
│   ├── abilities/
│   └── special/
├── aura/
│   ├── idle/
│   ├── walking/
│   ├── combat/
│   ├── abilities/
│   └── special/
├── kai/
│   ├── idle/
│   ├── combat/
│   ├── abilities/
│   └── special/
├── nova/
│   └── ...
└── perplexity/
    └── ...
```

### 3. Run Batch Clipper
```bash
# Process ALL sprites (maintains folder structure)
python batch_sprite_clipper.py ../raw_sprites ../clipped_sprites --quality high

# Or process one character at a time
python batch_sprite_clipper.py ../raw_sprites/genesis ../clipped_sprites/genesis
python batch_sprite_clipper.py ../raw_sprites/aura ../clipped_sprites/aura
python batch_sprite_clipper.py ../raw_sprites/kai ../clipped_sprites/kai
```

### 4. Review Output
```bash
ls -R ../clipped_sprites/
# Folder structure will match your input!
```

---

## Recommended Naming Convention

For sprite sheets with multiple frames:

### Walking Cycles
```
genesis_walk_front_01.png
genesis_walk_front_02.png
genesis_walk_front_03.png
...
genesis_walk_front_08.png

genesis_walk_back_01.png
genesis_walk_left_01.png
genesis_walk_right_01.png
```

### Combat Actions
```
genesis_shield_deploy_01.png
genesis_shield_deploy_02.png
genesis_shield_deploy_03.png
genesis_shield_deploy_04.png

genesis_sword_slash_01.png
genesis_energy_ribbon_01.png
```

### Environmental Interactions
```
genesis_climb_up_01.png
genesis_climb_down_01.png
genesis_hang_edge_idle_01.png
genesis_hang_edge_climb_01.png
genesis_ui_sit_01.png
genesis_ui_lean_01.png
```

### Special States
```
genesis_transform_cyan_01.png
genesis_transform_purple_01.png
genesis_ultimate_sphere_01.png
```

---

## Integration Architecture

### Current System (Static Images)
```kotlin
enum class AuraState(val assetPath: String, val description: String) {
    IDLE_WALK("embodiment/aura/aura_idle_walk.png", "Single static image")
}
```

### NEW System (Animated Sprites)
```kotlin
enum class GenesisAnimationState(
    val spritePattern: String,  // Pattern for frame files
    val frameCount: Int,        // Number of frames
    val fps: Int = 8,          // Frames per second
    val loop: Boolean = true,   // Loop animation?
    val description: String
) {
    // Walking cycles
    WALK_FRONT("embodiment/genesis/walk/front/frame_{:02d}.png", 8, 8, true, "Walking forward"),
    WALK_BACK("embodiment/genesis/walk/back/frame_{:02d}.png", 8, 8, true, "Walking backward"),
    RUN_FRONT("embodiment/genesis/run/front/frame_{:02d}.png", 8, 12, true, "Running forward"),

    // Combat
    SHIELD_DEPLOY("embodiment/genesis/combat/shield_deploy/frame_{:02d}.png", 4, 8, false, "Shield summon"),
    SWORD_SLASH("embodiment/genesis/combat/sword_slash/frame_{:02d}.png", 6, 12, false, "Sword attack"),

    // Environmental
    CLIMB_UP("embodiment/genesis/environment/climb_up/frame_{:02d}.png", 6, 8, true, "Climbing upward"),
    HANG_EDGE("embodiment/genesis/environment/hang_edge/frame_{:02d}.png", 4, 6, true, "Hanging from edge"),
    UI_SIT("embodiment/genesis/environment/ui_sit/frame_{:02d}.png", 8, 6, true, "Sitting on UI element"),

    // Transformations
    ENERGY_TRANSFORM("embodiment/genesis/transform/energy/frame_{:02d}.png", 12, 10, false, "Energy transformation"),

    // Abilities
    ENERGY_SPHERE("embodiment/genesis/abilities/sphere/frame_{:02d}.png", 8, 10, false, "Energy projectile"),
    RIBBON_ATTACK("embodiment/genesis/abilities/ribbon/frame_{:02d}.png", 10, 12, false, "Magenta ribbon whip")
}
```

---

## Asset Folder Structure (After Integration)

```
app/src/main/assets/embodiment/
├── genesis/
│   ├── walk/
│   │   ├── front/
│   │   │   ├── frame_01.png
│   │   │   ├── frame_02.png
│   │   │   └── ... (8 frames)
│   │   ├── back/
│   │   ├── left/
│   │   └── right/
│   ├── run/
│   │   └── front/
│   ├── combat/
│   │   ├── shield_deploy/
│   │   ├── sword_slash/
│   │   └── energy_ribbon/
│   ├── environment/
│   │   ├── climb_up/
│   │   ├── climb_down/
│   │   ├── hang_edge/
│   │   └── ui_sit/
│   ├── abilities/
│   │   ├── sphere/
│   │   └── ribbon/
│   └── transform/
│       ├── energy/
│       └── purple/
├── aura/
│   └── ... (similar structure)
├── kai/
│   └── ... (similar structure)
├── nova/
│   └── ... (similar structure)
└── perplexity/
    └── ... (similar structure)
```

---

## Performance Optimization Tips

### 1. **Sprite Sheet Atlases** (Advanced)
Instead of individual frames, combine into sprite sheets:
```
genesis_walk_front_sheet.png  (single image with all 8 frames in a row)
```

### 2. **Frame Rate Tuning**
- Idle/ambient: 6 FPS (smooth but subtle)
- Walking: 8 FPS (natural pace)
- Running: 12 FPS (fast movement)
- Combat: 12-15 FPS (snappy actions)

### 3. **Image Optimization**
After clipping, optimize PNGs:
```bash
# Install optipng
sudo apt install optipng

# Optimize all PNGs (non-destructive)
find clipped_sprites/ -name "*.png" -exec optipng -o7 {} \;
```

### 4. **Resolution Consistency**
All sprites should be same base height for consistent scaling:
- Recommended: 256px, 512px, or 1024px height
- Android mdpi/hdpi/xhdpi variants generated automatically

---

## Next Steps After Clipping

1. **Review clipped sprites** - Check for artifacts around complex edges
2. **Organize into animation sequences** - Group related frames
3. **Create sprite animation system** - Build Compose animator component
4. **Define character states** - Map animations to moods/triggers
5. **Wire to EmbodimentEngine** - Integrate with existing manifestation system

---

## Troubleshooting

### "GPU out of memory"
Use CPU-only mode:
```bash
pip uninstall onnxruntime-gpu
pip install onnxruntime
```

### "Backgrounds not fully removed"
Try adjusting thresholds in the script (advanced):
```python
alpha_matting_foreground_threshold=240  # Lower to 220 for more aggressive
alpha_matting_background_threshold=10   # Raise to 20 for less aggressive
```

### "Processing too slow"
- Use `--quality fast` for initial testing
- Process in smaller batches
- Use GPU acceleration if available

---

## Estimated Processing Time

- **CPU (8 cores)**: ~2-3 seconds per image
- **GPU (CUDA)**: ~0.5-1 second per image

For 500 sprites:
- CPU: ~20-25 minutes
- GPU: ~4-8 minutes

**LET IT RUN!** Processing hundreds of sprites will take time, but it's fully automated.

---

## Questions?

Check the script help:
```bash
python batch_sprite_clipper.py --help
```
