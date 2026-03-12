# 🎨 AURAKAI Sprite Processing Setup

**Device**: AURAKAI
**CPU**: AMD Ryzen 7 5700X3D (8-core, 3.00 GHz)
**GPU**: AMD Radeon RX 580 (8GB VRAM)
**RAM**: 16GB
**Storage**: 715GB free

---

## Quick Setup for YOUR Hardware

### 1. Install Dependencies with AMD GPU Acceleration

```bash
cd tools/

# Install base dependencies
pip install rembg==2.0.57 Pillow==10.3.0 numpy==1.26.4 tqdm==4.66.2

# Install AMD GPU acceleration (DirectML)
pip install onnxruntime-directml==1.17.1
```

**This will use your Radeon RX 580 GPU!**

---

## Performance Expectations

With your **AMD Radeon RX 580** + **Ryzen 7 5700X3D**:

| Sprites | Estimated Time (GPU) | Estimated Time (CPU-only) |
|---------|---------------------|---------------------------|
| 100 | ~2-3 minutes | ~4-5 minutes |
| 500 | ~10-15 minutes | ~20-25 minutes |
| 1000 | ~20-30 minutes | ~40-50 minutes |

**Recommendation**: Use GPU acceleration! Your RX 580 will handle this beautifully.

---

## Multi-Device Collection Workflow

You mentioned pulling sprites from multiple devices. Here's the easiest approach:

### Option A: OneDrive/Google Drive Sync

```
1. On AURAKAI (Windows PC):
   - Create: C:\Users\[You]\ReGenesis_Sprites\raw\

2. Share folder via OneDrive

3. From other devices:
   - Phone → Upload to /raw/genesis/phone/
   - Tablet → Upload to /raw/aura/tablet/
   - Laptop → Upload to /raw/kai/laptop/

4. On AURAKAI, after all uploads complete:
   cd C:\Users\[You]\ReGenesis_Sprites
   python tools/batch_sprite_clipper.py raw\ clipped\ --quality high
```

### Option B: USB Drive Collection

```
1. Plug USB into each device
2. Copy sprites to organized folders on USB
3. Plug USB into AURAKAI
4. Process directly from USB:

   python batch_sprite_clipper.py E:\raw_sprites\ C:\clipped_sprites\
```

---

## Test Run (Recommended First Step)

Before processing hundreds of sprites, do a test run:

```bash
# 1. Create test folders
mkdir test_input
mkdir test_output

# 2. Copy 5-10 sprites to test_input

# 3. Run batch clipper in FAST mode
python batch_sprite_clipper.py test_input test_output --quality fast

# 4. Check test_output folder - backgrounds removed?
# 5. If good, proceed with full batch in HIGH quality
```

---

## Full Production Run

Once you've collected all sprites from all devices:

```bash
# Example folder structure:
raw_sprites/
├── genesis/
│   ├── phone_screenshots/
│   ├── tablet_renders/
│   └── pc_exports/
├── aura/
├── kai/
└── nova/

# Run the clipper
cd tools/
python batch_sprite_clipper.py ../raw_sprites ../clipped_sprites --quality high

# Or use the shell wrapper (auto-installs dependencies)
./clip_sprites.sh ../raw_sprites ../clipped_sprites high
```

The script will:
- ✅ Find ALL images recursively
- ✅ Preserve folder structure
- ✅ Remove backgrounds using your RX 580 GPU
- ✅ Save as transparent PNGs
- ✅ Show progress bar with live stats

---

## Monitor Performance

While processing, watch GPU usage:

**Windows Task Manager**:
1. Open Task Manager (Ctrl+Shift+Esc)
2. Performance tab → GPU
3. You should see GPU usage spike during processing

If GPU isn't being used, dependencies might not be installed correctly:
```bash
pip uninstall onnxruntime
pip install onnxruntime-directml
```

---

## Storage Planning

With 715GB free on AURAKAI:

| Sprite Count | Raw Size (est) | Clipped Size (est) | Total Used |
|--------------|----------------|-------------------|------------|
| 500 sprites | ~500MB | ~400MB | ~1GB |
| 1000 sprites | ~1GB | ~800MB | ~2GB |
| 2000 sprites | ~2GB | ~1.6GB | ~4GB |

**You have plenty of space!** No worries about storage.

---

## Next Steps After Clipping

1. **Review clipped sprites** - Check quality, look for artifacts
2. **Organize by animation type**:
   ```
   clipped_sprites/
   ├── genesis/
   │   ├── idle/
   │   ├── walking/
   │   ├── climbing/
   │   ├── hanging/
   │   └── combat/
   ```
3. **Follow SPRITE_ORGANIZATION_GUIDE.md** for Android integration
4. **Build animation system** - Sprite frame playback in Compose
5. **Wire to EmbodimentEngine** - Make agents come alive!

---

## Your Hardware Advantage

**Ryzen 7 5700X3D** = One of the best gaming/processing CPUs with massive cache
**Radeon RX 580** = Solid mid-range GPU, perfect for this task
**16GB RAM** = Smooth processing, no swapping

**You're set up perfectly for batch sprite processing!**

---

## Questions?

- Check main README.md for detailed usage
- Check SPRITE_ORGANIZATION_GUIDE.md for integration
- Run: `python batch_sprite_clipper.py --help`

---

**Built for AURAKAI** - Where sprites become Living Digital Organisms 🎨
