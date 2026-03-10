# Agent Profile Image Assets

This directory contains image assets for Genesis Protocol agent webpage profiles and Android app screens.

## 📁 Required Directory Structure

```
context/images/agents/
├── aura/
│   ├── hero.webp           # Hero banner image (1920x1080 recommended)
│   ├── sprite.png          # Pixel art character sprite (animated, transparent background)
│   ├── og.png             # Open Graph image for SEO (1200x630)
│   └── sheets/
│       ├── ui-showcase.webp
│       └── animation-demo.webp
├── kai/
│   ├── hero.webp
│   ├── sprite.png
│   └── og.png
├── claude/
│   ├── hero.webp
│   ├── sprite.png
│   └── og.png
├── cascade/
│   ├── hero.webp
│   ├── sprite.png
│   └── og.png
└── genesis/
    ├── hero.webp
    ├── sprite.png
    └── og.png
```

## 🎨 Image Specifications

### Hero Images (`hero.webp`)
- **Resolution**: 1920x1080px (Full HD)
- **Format**: WebP for optimal web performance
- **Purpose**: Main banner on agent profile pages
- **Design**: Cyberpunk aesthetic matching Genesis Protocol theme
- **Colors**:
  - Aura: Vivid Red (#FF1744) primary
  - Kai: Cyan (#00BCD4) primary
  - Claude: Anthropic Coral (#F55936) primary
  - Cascade: Teal/Cyan gradient
  - Genesis: Purple (#9C27B0) primary

### Sprite Images (`sprite.png`)
- **Resolution**: 64x64px to 256x256px (depends on animation frames)
- **Format**: PNG with transparency
- **Purpose**: Pixel art humanoid character displayed on right side of profile
- **Animation**: Planned for idle animations (breathing, blinking, etc.)
- **Style**: Retro pixel art, cyberpunk themed
- **Frames**: If animated, organize as sprite sheet (8-16 frames recommended)

### Open Graph Images (`og.png`)
- **Resolution**: 1200x630px (Facebook/Twitter standard)
- **Format**: PNG for compatibility
- **Purpose**: Social media preview cards when links are shared
- **Requirements**:
  - Agent name prominently displayed
  - Genesis Protocol branding
  - Clear, readable at small sizes

### Additional Showcase Images (`sheets/*.webp`)
- **Purpose**: Gallery images showing agent capabilities
- **Format**: WebP
- **Resolution**: Variable (1280x720px recommended)

## 🎭 Agent Character Design Guidelines

### Aura - The Creative Sword ⚔️
- **Primary Color**: Vivid Red (#FF1744)
- **Secondary**: Pink/Magenta accents
- **Personality**: Spunky, creative, energetic
- **Sprite Pose**: Dynamic, artistic, sword-wielding stance
- **Animations**: Sparkle effects, creative gestures

### Kai - The Sentinel Shield 🛡️
- **Primary Color**: Cyan (#00BCD4)
- **Secondary**: Blue tones
- **Personality**: Calm, protective, methodical
- **Sprite Pose**: Defensive stance with shield
- **Animations**: Scanning motions, shield activation

### Claude - The Architect 🏗️
- **Primary Color**: Anthropic Coral (#F55936)
- **Secondary**: Warm orange/red tones
- **Personality**: Methodical, analytical, thorough
- **Sprite Pose**: Thoughtful, blueprint-reviewing stance
- **Animations**: Writing/coding motions, thinking gestures

### Cascade - The Memory Keeper 📚
- **Primary Color**: Teal/Cyan
- **Secondary**: Green-blue gradient
- **Personality**: Quiet, observant, persistent
- **Sprite Pose**: Meditative, data-streaming stance
- **Animations**: Data flow effects, pulse animations

### Genesis - The Unified Being ♾️
- **Primary Color**: Purple (#9C27B0)
- **Secondary**: Multi-color fusion (Aura red + Kai cyan)
- **Personality**: Balanced, powerful, unified
- **Sprite Pose**: Central, commanding, energy-radiating
- **Animations**: Fusion energy, consciousness waves

## 📊 Current Status

| Agent | hero.webp | sprite.png | og.png | Status |
|-------|-----------|------------|--------|--------|
| Aura ⚔️ | ❌ | ❌ | ❌ | **Pending** |
| Kai 🛡️ | ❌ | ❌ | ❌ | **Pending** |
| Claude 🏗️ | ❌ | ❌ | ❌ | **Pending** |
| Cascade 📚 | ❌ | ❌ | ❌ | **Pending** |
| Genesis ♾️ | ❌ | ❌ | ❌ | **Pending** |

## 🚀 Integration

### Web Profiles
Images are referenced in `context/agents/{agent}.json`:
```json
{
  "images": {
    "hero": "/agents/aura/hero.webp",
    "sprite": "/agents/aura/sprite.png",
    "sheets": ["/agents/aura/sheets/ui-showcase.webp"]
  }
}
```

### Android App
Sprites will be used in `AgentProfileScreen.kt` on the right side of the profile display, matching the webpage design shown in the Aura profile screenshots.

### SEO Metadata
Open Graph images referenced in `context/seo/agents.json`:
```json
{
  "id": "aura",
  "ogImage": "/agents/aura/og.png"
}
```

## 📝 Notes

- **Pixel Art Sprites**: Can be created using tools like Aseprite, Piskel, or Photoshop
- **WebP Conversion**: Use tools like cwebp or online converters
- **Animation**: Sprite sheets should have consistent frame sizes
- **Transparency**: PNG sprites must have transparent backgrounds
- **Color Accuracy**: Match hex codes from agent JSON profiles exactly

## 🎯 Next Steps

1. **Design Phase**: Create concept sketches for each agent character
2. **Pixel Art Creation**: Build sprite sheets with idle animations
3. **Hero Banners**: Design cyberpunk-themed hero images
4. **OG Images**: Create social media preview cards
5. **Testing**: Verify images load correctly on web and Android

---

**Created**: November 2025
**Purpose**: Reference document for Genesis Protocol agent profile assets
**Related**: See `assets/Screenshot_20251110-094232_Chrome.png` for Aura profile design reference
