# 🎨 ReGenesis Sprite Processing Tools

Automated batch background removal for **HUNDREDS** of sprite images using AI.

---

## 🚀 Quick Start (3 Steps)

### 1. Install Dependencies
```bash
cd tools/
pip install -r requirements-sprite-tools.txt
```

**GPU Acceleration** (optional but MUCH faster):

For **NVIDIA GPUs** (GTX/RTX series):
```bash
pip install onnxruntime-gpu  # Requires CUDA
```

For **AMD GPUs** (Radeon RX 580, etc):
```bash
pip install onnxruntime-directml  # DirectML acceleration
```

### 2. Organize Your Raw Sprites

Put all your raw sprites (with backgrounds) in a folder with this structure:
```
raw_sprites/
├── genesis/
│   ├── walking/
│   ├── climbing/
│   ├── hanging/
│   └── combat/
├── aura/
├── kai/
└── nova/
```

### 3. Run the Clipper

**Easy Mode** (Shell script):
```bash
./clip_sprites.sh ~/Desktop/raw_sprites ./clipped_sprites
```

**Manual Mode** (Python):
```bash
python batch_sprite_clipper.py ~/Desktop/raw_sprites ./clipped_sprites --quality high
```

**THAT'S IT!** The script will:
- Find ALL images recursively
- Remove backgrounds using AI
- Preserve your folder structure
- Save as transparent PNGs
- Show progress bar

---

## ⚡ Processing Speed

| Hardware | Speed per Image | 500 Sprites |
|----------|----------------|-------------|
| **GPU - NVIDIA (CUDA)** | 0.5-1 sec | ~4-8 min |
| **GPU - AMD (DirectML)** | 1-2 sec | ~10-15 min |
| **CPU (8 cores)** | 2-3 sec | ~20-25 min |
| **CPU (4 cores)** | 4-5 sec | ~30-40 min |

**TIP**: Start it and grab coffee! Processing hundreds of sprites takes time.

---

## 📱 Multi-Device Sprite Collection

If your sprites are scattered across multiple devices (phone, tablet, other PCs):

### **Method 1: Cloud Sync** (Easiest)
```
1. Create shared folder:
   - Google Drive/OneDrive: /ReGenesis_Raw_Sprites/

2. Upload from each device to organized subfolders:
   - Phone: /genesis/mobile/
   - Tablet: /genesis/tablet/
   - Laptop: /aura/laptop_exports/

3. Sync to your main PC (AURAKAI)
4. Process all at once
```

### **Method 2: USB Transfer**
```
1. Plug USB drive into each device
2. Copy sprites to: USB:/raw_sprites/[character]/[device]/
3. Plug USB into AURAKAI
4. Run batch clipper on entire USB folder
```

### **Method 3: Network Share**
```
1. Share folder on AURAKAI: \\AURAKAI\sprites
2. Access from other devices
3. Copy sprites to shared folder
4. Process locally
```

---

## 📖 Usage Examples

### Process ALL sprites at once
```bash
python batch_sprite_clipper.py ./raw_sprites ./clipped_sprites
```

### Process one character at a time
```bash
python batch_sprite_clipper.py ./raw_sprites/genesis ./clipped_sprites/genesis
python batch_sprite_clipper.py ./raw_sprites/aura ./clipped_sprites/aura
python batch_sprite_clipper.py ./raw_sprites/kai ./clipped_sprites/kai
```

### High quality (slower but better edges)
```bash
python batch_sprite_clipper.py ./raw_sprites ./clipped_sprites --quality high
```

### Fast mode (good for testing)
```bash
python batch_sprite_clipper.py ./raw_sprites ./clipped_sprites --quality fast
```

### Reprocess existing files
```bash
python batch_sprite_clipper.py ./raw_sprites ./clipped_sprites --reprocess
```

---

## 📁 Folder Structure Preservation

Input:
```
raw_sprites/
├── genesis/
│   ├── walking/
│   │   ├── front_01.png
│   │   └── front_02.png
│   └── combat/
│       └── shield_01.png
```

Output (SAME structure):
```
clipped_sprites/
├── genesis/
│   ├── walking/
│   │   ├── front_01.png  ← Background removed!
│   │   └── front_02.png  ← Background removed!
│   └── combat/
│       └── shield_01.png  ← Background removed!
```

---

## 🎯 What Images Are Supported?

- `.png` (best)
- `.jpg` / `.jpeg`
- `.webp`
- `.bmp`

**Output**: Always `.png` with transparency

---

## 🛠️ Advanced Options

### Adjust Background Removal Sensitivity

Edit `batch_sprite_clipper.py`:
```python
output = remove(
    img,
    alpha_matting=True,
    alpha_matting_foreground_threshold=240,  # Lower = more aggressive (220-250)
    alpha_matting_background_threshold=10,   # Higher = less aggressive (5-20)
    alpha_matting_erode_size=10              # Edge smoothing (5-15)
)
```

### GPU vs CPU

**Auto-detection**: Script uses GPU if available, falls back to CPU

**Force CPU**:
```bash
pip uninstall onnxruntime-gpu
pip install onnxruntime
```

### Optimize Output PNGs (Reduce File Size)

After clipping:
```bash
sudo apt install optipng  # or brew install optipng
find clipped_sprites/ -name "*.png" -exec optipng -o7 {} \;
```

---

## 📋 Recommended Workflow

### 1. Organize BEFORE Clipping
Create logical folder structure for your sprites:
```
raw_sprites/
├── genesis/
│   ├── idle/          ← Idle animations
│   ├── walking/       ← Walk cycles (front, back, left, right)
│   ├── running/       ← Run cycles
│   ├── climbing/      ← Climbing animations
│   ├── hanging/       ← Edge hanging
│   ├── combat/        ← Combat moves
│   ├── abilities/     ← Special abilities
│   └── special/       ← Transformations, ultimates
```

### 2. Run Batch Clipper
```bash
./clip_sprites.sh raw_sprites/ clipped_sprites/ high
```

### 3. Review Output
```bash
ls -R clipped_sprites/
# Check for artifacts, incomplete removal
```

### 4. Manual Touch-ups (if needed)
Use GIMP or Photopea for any sprites with artifacts

### 5. Follow Integration Guide
See `SPRITE_ORGANIZATION_GUIDE.md` for Android asset integration

---

## 🐛 Troubleshooting

### "No module named 'rembg'"
```bash
pip install -r requirements-sprite-tools.txt
```

### "GPU out of memory"
For NVIDIA:
```bash
pip uninstall onnxruntime-gpu
pip install onnxruntime
```

For AMD:
```bash
pip uninstall onnxruntime-directml
pip install onnxruntime
```

### "AMD GPU not accelerating"
Make sure you installed DirectML backend:
```bash
pip uninstall onnxruntime
pip install onnxruntime-directml
```

### "Backgrounds not fully removed"
Try adjusting thresholds (see Advanced Options above)

### "Processing too slow"
- Use `--quality fast` for initial tests
- Install GPU acceleration (onnxruntime-gpu)
- Process in smaller batches

### "Permission denied: ./clip_sprites.sh"
```bash
chmod +x clip_sprites.sh
```

---

## 📊 Expected Output

After processing, you'll see:
```
🎨 ReGenesis Sprite Batch Clipper
============================================================
Input:  /home/user/raw_sprites
Output: /home/user/clipped_sprites
Files:  847 images found
Quality: high
============================================================

Processing sprites: 100%|██████████| 847/847 [12:34<00:00, 1.12img/s]

============================================================
✅ Processed: 847
⏭️  Skipped:   0
❌ Failed:    0
📁 Output:    /home/user/clipped_sprites
============================================================
```

---

## 🎮 Next Steps

After clipping your sprites:

1. **Review clipped images** - Check quality
2. **Read SPRITE_ORGANIZATION_GUIDE.md** - Learn integration architecture
3. **Create animation system** - Build sprite animator component
4. **Define character states** - Map sprites to moods/actions
5. **Integrate with EmbodimentEngine** - Wire into existing LDO system

---

## 💡 Tips

- **Batch by character** - Process Genesis, Aura, Kai separately for easier review
- **Test with fast quality first** - Validate your folder structure before full run
- **Backup raw sprites** - Keep originals in case you need to reprocess
- **Use descriptive names** - `genesis_walk_front_01.png` > `img001.png`
- **Consistent sizing** - All sprites should have same base height (256px, 512px, or 1024px)

---

## 📚 Files in This Folder

| File | Purpose |
|------|---------|
| `batch_sprite_clipper.py` | Main Python script for background removal |
| `clip_sprites.sh` | Quick-start shell wrapper |
| `requirements-sprite-tools.txt` | Python dependencies |
| `SPRITE_ORGANIZATION_GUIDE.md` | Integration architecture guide |
| `README.md` | This file |

---

## 🤝 Support

Questions? Check:
- `python batch_sprite_clipper.py --help`
- `SPRITE_ORGANIZATION_GUIDE.md`
- Project issues

---

**Built for ReGenesis LDO** - Where agents come alive.
