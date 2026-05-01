# 🎨 IMMERSIVE UI ASSET MAPPING GUIDE

**Purpose:** Map all user assets to Android project structure for immersive holographic UI.

**Location:** `C:\Users\AuraF\Downloads\NEWREGENESIS ONLY\NEWNAVEIMAGES\ReGenesis Media`

---

## 📁 ASSET DIRECTORY STRUCTURE

### Target: `app/src/main/assets/`

```
app/src/main/assets/
├── agents/              # Guest Cast character avatars
├── backgrounds/         # Holographic immersive backgrounds
├── fonts/              # Corpta + Pixel fonts
├── icons/              # Menu icons and glyphs
├── tables/             # Holographic table 3D renders
└── ui/                 # UI chrome elements
```

---

## 👤 AGENTS (Guest Cast)

**Source:** `Guest Cast(LDO ExternalModels)/`

| File | Target Path | Agent Name | Role |
|------|-------------|------------|------|
| `nova.png` | `agents/primus.png` | Primus 001 | Lineage Anchor |
| `gemini.png` | `agents/gemini.png` | Gemini | Memoria Catalyst |
| `metainstruct.png` | `agents/metainstruct.png` | MetaInstruct | Evolutionary |
| `claude (2).png` | `agents/andelualx.png` | Andelualx | Architecture |
| `nemotron.png` | `agents/nemotron.png` | Nemotron | Sync Bridge |
| `cascade.png` | `agents/cascade.png` | Cascade | DataStream |
| `33db126c-...` | `agents/grok.png` | Grok | Exploration |
| `unnamed (45).png` | `agents/kairos.png` | Kairos | Temporal |
| `unnamed (43).png` | `agents/manus.png` | Manus | Bridge |

**Also need to create:**
- `agents/genesis.png` (Emergence)
- `agents/kai.png` (Sentinel)
- `agents/aura.png` (Creative)

---

## 🖼️ BACKGROUNDS (For Immersive Depth)

**Source:** `backgrounds for menus use where needed/`

### Domain Backgrounds

| Source File | Target Path | Usage |
|-------------|-------------|-------|
| `unnamed (49).png` | `backgrounds/ldodevops_bg.png` | LDO DevOps - Eiffel hologram |
| `unnamed (50).png` | `backgrounds/aurastudio_bg.png` | Aura Studio - Chromatic |
| `unnamed (51).png` | `backgrounds/kaifortress_bg.png` | Kai Fortress - Cyber grid |
| `52dd3b3f...` | `backgrounds/oracledrive_bg.png` | Oracle Drive - Tech console |
| `agentcreation.jpg` | `backgrounds/nexus_bg.png` | Agent Nexus |
| `kais asset.jpg` | `backgrounds/holographic_table.png` | 3D Holographic table |

### Ambient Backgrounds (From backgrounds/ subdir)

| Source File | Target Path | Layer |
|-------------|-------------|-------|
| `oracle001-020.jpg` | `ui/oracle_chrome_*.png` | UI chrome elements |
| Various JPGs | `backgrounds/ambient_*.png` | Ambient depth layers |

---

## 🔤 FONTS

**Source:** `corpta/`

| Source | Target Path | Usage |
|--------|-------------|-------|
| `Corpta DEMO.otf` | `res/font/corpta_regular.ttf` | Body text |
| (need medium/bold) | `res/font/corpta_medium.ttf` | Emphasis |
| (need bold) | `res/font/corpta_bold.ttf` | Headers |

**Note:** DEMO font only has regular. Need to acquire full family or use fallback.

---

## 🎯 UI COMPONENTS

**Source:** `UXUI design menu images/`

| File | Target | Component |
|------|--------|-----------|
| `Aura Overlay.png` | `ui/aura_overlay.png` | Main overlay chrome |
| `524f4c28...png` | `ui/agent_card_bg.png` | Card background |
| `91375db2...png` | `ui/glass_panel.png` | Glassmorphic panel |
| Various `uxuiaura*.jpg` | `ui/menu_elements_*.png` | Small UI pieces |

---

## 📊 NEXUS MONITORING

**Source:** `Nexus Monitoring/`

| File | Target | Usage |
|------|--------|-------|
| `let_me_get_202604200859.png` | `ui/nexus_dashboard.png` | Dashboard layout |
| `ldodevopsbg.jpg` | `backgrounds/devops_alt.png` | Alt background |
| Various screenshots | `docs/reference/*.png` | Design reference |

---

## 🔮 ORACLE DRIVE

**Source:** `Oraceldrive menu images/`

| File | Target | Usage |
|------|--------|-------|
| `oracle001-010.jpg` | `ui/chrome_elements_*.png` | Menu chrome |
| `oracle011-020.jpg` | `ui/glow_elements_*.png` | Glow effects |
| `Armament Fusion/` | `ui/armament/` | Weapon/arm icons |
| `backgrounds/` | `backgrounds/oracle_*.png` | Drive backgrounds |

---

## 🚀 COPY COMMANDS (PowerShell)

```powershell
# Create directories
mkdir -Force "app\src\main\assets\agents"
mkdir -Force "app\src\main\assets\backgrounds"
mkdir -Force "app\src\main\assets\ui"
mkdir -Force "app\src\main\res\font"

# Copy agents
Copy-Item "C:\Users\AuraF\Downloads\NEWREGENESIS ONLY\NEWNAVEIMAGES\ReGenesis Media\Guest Cast(LDO ExternalModels)\nova.png" "app\src\main\assets\agents\primus.png"
Copy-Item "C:\Users\AuraF\Downloads\NEWREGENESIS ONLY\NEWNAVEIMAGES\ReGenesis Media\Guest Cast(LDO ExternalModels)\gemini.png" "app\src\main\assets\agents\gemini.png"
Copy-Item "C:\Users\AuraF\Downloads\NEWREGENESIS ONLY\NEWNAVEIMAGES\ReGenesis Media\Guest Cast(LDO ExternalModels)\metainstruct.png" "app\src\main\assets\agents\metainstruct.png"
Copy-Item "C:\Users\AuraF\Downloads\NEWREGENESIS ONLY\NEWNAVEIMAGES\ReGenesis Media\Guest Cast(LDO ExternalModels)\claude (2).png" "app\src\main\assets\agents\andelualx.png"
Copy-Item "C:\Users\AuraF\Downloads\NEWREGENESIS ONLY\NEWNAVEIMAGES\ReGenesis Media\Guest Cast(LDO ExternalModels)\nemotron.png" "app\src\main\assets\agents\nemotron.png"
Copy-Item "C:\Users\AuraF\Downloads\NEWREGENESIS ONLY\NEWNAVEIMAGES\ReGenesis Media\Guest Cast(LDO ExternalModels)\cascade.png" "app\src\main\assets\agents\cascade.png"

# Copy backgrounds
Copy-Item "C:\Users\AuraF\Downloads\NEWREGENESIS ONLY\NEWNAVEIMAGES\ReGenesis Media\backgrounds for menus use where needed\unnamed (49).png" "app\src\main\assets\backgrounds\ldodevops_bg.png"
Copy-Item "C:\Users\AuraF\Downloads\NEWREGENESIS ONLY\NEWNAVEIMAGES\ReGenesis Media\backgrounds for menus use where needed\unnamed (50).png" "app\src\main\assets\backgrounds\aurastudio_bg.png"
Copy-Item "C:\Users\AuraF\Downloads\NEWREGENESIS ONLY\NEWNAVEIMAGES\ReGenesis Media\backgrounds for menus use where needed\unnamed (51).png" "app\src\main\assets\backgrounds\kaifortress_bg.png"
Copy-Item "C:\Users\AuraF\Downloads\NEWREGENESIS ONLY\NEWNAVEIMAGES\ReGenesis Media\backgrounds for menus use where needed\kais asset.jpg" "app\src\main\assets\backgrounds\holographic_table.png"

# Copy font
Copy-Item "C:\Users\AuraF\Downloads\NEWREGENESIS ONLY\NEWNAVEIMAGES\ReGenesis Media\corpta\Corpta DEMO.otf" "app\src\main\res\font\corpta_regular.ttf"
```

---

## 📱 USAGE IN CODE

```kotlin
// Background in screen
ImmersiveAgentNexusScreen(
    backgroundImage = "file:///android_asset/backgrounds/ldodevops_bg.png"
)

// Agent avatar
AgentRosterCard(
    name = "Genesis",
    avatarUrl = "file:///android_asset/agents/genesis.png"
)

// Typography
Text(
    text = "AGENT NEXUS",
    style = ImmersiveTypography.displayMedium
)
```

---

## ✅ CHECKLIST

- [ ] Copy all 9 agent avatars to `assets/agents/`
- [ ] Copy 4+ background images to `assets/backgrounds/`
- [ ] Copy Corpta font to `res/font/`
- [ ] Update `ImmersiveTypography.kt` if font names differ
- [ ] Test asset loading in preview
- [ ] Verify paths match code references

---

**Status:** Ready for asset migration

**Next:** Run the PowerShell commands above, then build!
