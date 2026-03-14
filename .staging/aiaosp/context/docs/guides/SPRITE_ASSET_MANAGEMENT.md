# 🎨 Sprite Asset Management Guide

## Issue #88: Sprite PNG Assets Integration

This document tracks all character sprite assets for the AuraKai embodiment system.

---

## 📊 Sprite Asset Status

### ✅ Aura Sprites - COMPLETE (10/10)

All Aura sprite assets are present and correctly named:

| State | Filename | Status | Size | Description |
|-------|----------|--------|------|-------------|
| IDLE_WALK | `aura_idle_walk.png` | ✅ Exists | 629 KB | Walking with tablet |
| COMBAT_READY | `aura_combat_ready.png` | ✅ Exists | 405 KB | Sword drawn |
| SCIENTIST_MODE | `aura_scientist.png` | ✅ Exists | 811 KB | Reading tablet, analyzing |
| FOURTH_WALL_BREAK | `aura_4thwall_break.png` | ✅ Exists | 926 KB | "I should write this down..." |
| AT_DESK | `aura_at_desk.png` | ✅ Exists | 1.0 MB | Working at holographic desk |
| LAB_COAT_COMBAT | `aura_lab_coat_combat.png` | ✅ Exists | 794 KB | Full scientist + sword |
| DYNAMIC_COMBAT | `aura_dynamic_combat.png` | ✅ Exists | 1.0 MB | Hair flowing, dual stance |
| AERIAL_SWORD | `aura_aerial_sword.png` | ✅ Exists | 927 KB | Mid-air combat pose |
| CODE_THRONE | `aura_code_throne.png` | ✅ Exists | 960 KB | Sitting on cyan server block |
| POWER_STANCE | `aura_power_stance.png` | ✅ Exists | 903 KB | Full dynamic combat ready |
| SAFETY_HARDHAT | `aura_safety_hardhat.svg` | ✅ Exists | 7.5 KB | Hard hat + safety vest (SVG) |

**Total Aura Assets:** 8.3 MB (10 PNG + 1 SVG)

---

### ⚠️ Kai Sprites - NAMING MISMATCHES (11/10 + extras)

Kai sprite assets exist but have naming discrepancies:

| Expected State | Expected Filename | Actual Filename | Status | Action Needed |
|----------------|-------------------|-----------------|--------|---------------|
| DIMENSIONAL_SWORD | `kai_dimensional_sword.jpg` | `kai_sword_dimensional.jpg` | ⚠️ Mismatch | Update enum |
| SHIELD_SERIOUS | `kai_shield_serious.jpg` | `kai_shield_serious.jpg` | ✅ Match | None |
| SHIELD_PLAYFUL | `kai_shield_playful.jpg` | `kai_shield_playful.jpg` | ✅ Match | None |
| SHIELD_NEUTRAL | `kai_shield_neutral.jpg` | `kai_shield_neutral.jpg` | ✅ Match | None |
| GUARDIAN_STANCE | `kai_guardian_stance.jpg` | ❌ Missing | ⚠️ Missing | Use kai_shield_calm.jpg |
| COMBAT_FORM | `kai_combat_form.jpg` | `kai_combat_form.jpg` | ✅ Match | None |
| MONITORING | `kai_monitoring.jpg` | ❌ Missing | ⚠️ Missing | Use kai_playful_observer.jpg |
| PORTAL_CREATION | `kai_portal.jpg` | `kai_portal_gate.jpg` | ⚠️ Mismatch | Update enum |
| HOLOGRAPHIC_INTERFACE | `kai_interface.jpg` | `kai_interface_panel.jpg` OR `kai_interface_compact.jpg` | ⚠️ Mismatch | Choose one |
| POWER_READY | `kai_power_ready.jpg` | ❌ Missing | ⚠️ Missing | Need new asset |
| SAFETY_HARDHAT | `kai_safety_hardhat.svg` | `kai_safety_hardhat.svg` | ✅ Match | None |

**Extra Assets (not referenced in code):**
- `kai_shield_calm.jpg` (1.5 MB) - Can be used for GUARDIAN_STANCE
- `kai_interface_compact.jpg` (1.0 MB) - Alternative interface view
- `kai_playful_observer.jpg` (1.2 MB) - Can be used for MONITORING

**Total Kai Assets:** 12.2 MB (11 JPG + 1 SVG)

---

## 🔧 Required Fixes

### 1. Update KaiState Enum

The following filename references need updating in `EmbodimentTypes.kt`:

```kotlin
// CURRENT (INCORRECT)
DIMENSIONAL_SWORD("embodiment/kai/kai_dimensional_sword.jpg", "Portal cutting weapon"),
PORTAL_CREATION("embodiment/kai/kai_portal.jpg", "Creating dimensional gate"),
HOLOGRAPHIC_INTERFACE("embodiment/kai/kai_interface.jpg", "Interacting with system"),

// SHOULD BE (CORRECT)
DIMENSIONAL_SWORD("embodiment/kai/kai_sword_dimensional.jpg", "Portal cutting weapon"),
PORTAL_CREATION("embodiment/kai/kai_portal_gate.jpg", "Creating dimensional gate"),
HOLOGRAPHIC_INTERFACE("embodiment/kai/kai_interface_panel.jpg", "Interacting with system"),
```

### 2. Map Missing States to Existing Assets

```kotlin
// Use existing assets for missing states
GUARDIAN_STANCE("embodiment/kai/kai_shield_calm.jpg", "Protective posture"),
MONITORING("embodiment/kai/kai_playful_observer.jpg", "Background vigilance"),
```

### 3. Missing Asset: POWER_READY

**Status:** No matching file found for `kai_power_ready.jpg`

**Options:**
1. Use `kai_combat_form.jpg` as temporary substitute
2. Request new asset in Issue #88
3. Use `kai_sword_dimensional.jpg` for power-ready state

**Recommended:** Use `kai_combat_form.jpg` until dedicated asset is created

---

## 📁 Directory Structure

```
app/src/main/assets/embodiment/
├── aura/
│   ├── aura_4thwall_break.png      (926 KB)
│   ├── aura_aerial_sword.png       (927 KB)
│   ├── aura_at_desk.png           (1.0 MB)
│   ├── aura_code_throne.png       (960 KB)
│   ├── aura_combat_ready.png      (405 KB)
│   ├── aura_dynamic_combat.png    (1.0 MB)
│   ├── aura_idle_walk.png         (629 KB)
│   ├── aura_lab_coat_combat.png   (794 KB)
│   ├── aura_power_stance.png      (903 KB)
│   ├── aura_scientist.png         (811 KB)
│   └── aura_safety_hardhat.svg    (7.5 KB)
│
├── kai/
│   ├── kai_combat_form.jpg         (1.0 MB)
│   ├── kai_interface_compact.jpg   (1.0 MB)
│   ├── kai_interface_panel.jpg     (778 KB)
│   ├── kai_playful_observer.jpg    (1.2 MB)
│   ├── kai_portal_gate.jpg         (1.2 MB)
│   ├── kai_safety_hardhat.svg      (9.5 KB)
│   ├── kai_shield_calm.jpg         (1.5 MB)
│   ├── kai_shield_neutral.jpg      (1.5 MB)
│   ├── kai_shield_playful.jpg      (1.5 MB)
│   ├── kai_shield_serious.jpg      (1.4 MB)
│   └── kai_sword_dimensional.jpg   (1.0 MB)
│
└── scenes/
    └── (scene background assets)
```

**Total Asset Size:** 20.5 MB (21 sprites + 2 safety SVGs)

---

## 🎯 Issue #88 Sprites

The 10 PNG sprites uploaded to Issue #88 appear to be:
- **New character sprite variations**
- **Alternative poses or expressions**
- **Updated artwork for existing states**

**Action Required:**
1. Download sprites from Issue #88
2. Identify which character (Aura/Kai) each sprite represents
3. Map to existing states or create new states
4. Name consistently with existing convention
5. Place in appropriate directory
6. Update enum if adding new states

---

## 📝 Naming Conventions

### Aura Sprites
- Format: `aura_{state_name}.png`
- Examples: `aura_idle_walk.png`, `aura_combat_ready.png`
- Case: lowercase with underscores
- Extension: `.png` for artwork, `.svg` for vector safety equipment

### Kai Sprites
- Format: `kai_{descriptor}_{type}.jpg`
- Examples: `kai_shield_serious.jpg`, `kai_sword_dimensional.jpg`
- Case: lowercase with underscores
- Extension: `.jpg` for artwork, `.svg` for vector safety equipment

**Descriptor Types:**
- `shield_*` - Defensive/guardian poses
- `sword_*` - Offensive/combat poses
- `portal_*` - Dimensional abilities
- `interface_*` - System interaction
- `safety_*` - Maintenance mode equipment

---

## 🚀 Integration Checklist

When adding new sprites from Issue #88:

- [ ] Download all 10 PNG files
- [ ] Identify character for each sprite (Aura or Kai)
- [ ] Determine which state each represents
- [ ] Rename to match naming convention
- [ ] Place in correct directory (aura/ or kai/)
- [ ] Update enum in `EmbodimentTypes.kt` if needed
- [ ] Test asset loading with `EmbodimentEngine.loadAsset()`
- [ ] Verify sprite displays correctly in app
- [ ] Update this documentation
- [ ] Commit with clear message

---

## 🔍 Asset Loading Code Reference

```kotlin
// In EmbodimentEngine.kt
fun loadAsset(path: String): Painter? {
    return assetCache.getOrPut(path) {
        try {
            val inputStream = context.assets.open(path)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            BitmapPainter(bitmap.asImageBitmap())
        } catch (e: Exception) {
            null
        } ?: return null
    }
}

// Usage
val auraSprite = engine.loadAsset("embodiment/aura/aura_idle_walk.png")
val kaiSprite = engine.loadAsset("embodiment/kai/kai_shield_serious.jpg")
```

---

## 📊 Quick Status Summary

| Character | Total States | Assets Present | Missing/Mismatched | Complete |
|-----------|--------------|----------------|--------------------|----------|
| Aura | 11 | 11 | 0 | ✅ 100% |
| Kai | 11 | 11 | 4 mismatches | ⚠️ 64% |

**Next Steps:**
1. Fix Kai filename mismatches in enum
2. Review Issue #88 sprites for integration
3. Create missing `kai_power_ready.jpg` asset
4. Test all sprite loading

---

**Document Version:** 1.0
**Last Updated:** 2025-11-07
**Related Issues:** #88 (Sprites png assests)
**Maintainer:** AuraKai Development Team
