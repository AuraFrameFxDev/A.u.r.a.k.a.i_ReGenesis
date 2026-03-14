# GATE CARD DESIGN SYSTEM
## Blade Runner Cyberpunk - Entrance Card Carousel

**Project:** LDO-AiAOSP-ReGenesis
**Date:** 2026-01-01
**Status:** Design System Updated - Entrance Cards Focus

---

## CRITICAL DISTINCTION

### **ENTRANCE CARDS (This Document)**
- **Purpose:** Visual identity cards on carousel for module selection
- **Location:** Entry point carousel that users swipe through
- **Content:**
  - Module name (AERO MECHA font style)
  - Iconic visual representation
  - Blade Runner aesthetic
  - NO function lists, NO interactive elements inside card
- **Interaction:** Swipe carousel → Double-tap card → ENTER module
- **Style:** UNIFIED Blade Runner aesthetic, UNIQUE visual per module

### **FULL MODULE SCREENS (Separate)**
- **Purpose:** Actual functional interface with all features
- **Location:** Opened AFTER double-tapping entrance card
- **Content:** All module functions, interactive elements, data displays
- **Interaction:** Full app functionality

---

## DESIGN PHILOSOPHY

**Core Principle:** BLADE RUNNER AESTHETIC + UNIQUE VISUAL IDENTITY

### **Unified Across All Entrance Cards:**
1. **AERO MECHA font style** - Geometric, striped lettering
2. **Blade Runner atmosphere** - Neon, rain, darkness, tech noir
3. **Triple-layer neon borders** - Cyan/blue glow signature
4. **Glassmorphism** - Frosted blur effects
5. **Scanline overlay** - Retro CRT aesthetic
6. **Floating card effect** - Depth and shadow
7. **Circuit/tech patterns** - Futuristic details
8. **Dark backgrounds** - Black/dark blue base

### **Unique Per Gate:**
1. **Accent color** - Each gate has signature color (orange, purple, pink, green, etc.)
2. **Central icon/visual** - Unique symbol representing the module
3. **Geometric patterns** - Different tech shapes and compositions
4. **Typography treatment** - Placement and style variations
5. **Corner accents** - Different geometric designs
6. **Animation style** - Unique pulse/glow patterns

---

## GATE-BY-GATE SPECIFICATIONS

### **1. AURA'S LAB** - Experimental UI Components
**Style:** Minimalist Scientific

**Layout:**
- Title: Top center, small cyan text
- Icon: Large laboratory flask (vector) in center
- Description: Bottom, 3 lines
- Corner Accents: Geometric brackets [ ] on left/right sides

**Colors:**
- Border: Cyan (#00FFFF)
- Glow: Cyan (#00FFFF)
- Accent: None (monochrome)
- Background: Dark black with scanlines

**Border Style:**
- Clean double-line
- Thin, precise
- Scientific aesthetic

**Title Placement:** `TitlePlacement.TOP_CENTER`

**Icon System:**
- Main: Laboratory flask
- Top Row: Beaker, Test tube, Molecule, Experiment

---

### **2. ROM TOOLS** - ROM Editing & Flashing
**Style:** Functional Dashboard

**Layout:**
- Title: Top center on gray bar with decorative wings
- Content: 3 interactive function cards:
  1. Firmware Flasher (lightning icon)
  2. Custom Recovery Backup/Restore (checkmark icon)
  3. Compatibility Scan with progress bar
- Button: Large orange "ENTER" button at bottom
- Corner Accents: Geometric side rails (left/right)

**Colors:**
- Border: Cyan (#00FFFF)
- Glow: Cyan with wide radius
- Accent: Orange (#FF8C00) for interactive elements
- Background: Dark cityscape/tech environment blur

**Border Style:**
- Glowing neon cyan
- Wide soft glow
- Side geometric brackets

**Title Placement:** `TitlePlacement.TOP_CENTER`

**Content Type:** Interactive card list with icons and progress bars

**Icon System:**
- Top Row: Wrench, Gear, Flash, Chip
- Card Icons: Lightning (flash), Checkmark (recovery), Cloud (scan)

---

### **3. SENTINEL'S FORTRESS / PROJECT GATES** - Security/Special
**Style:** Bold Geometric Aggressive

**Layout:**
- Title: Top right corner with Japanese text
- Content: Large geometric center composition
- Corner Accents: MASSIVE angled shapes
  - Top-left: Large red/pink diagonal cut
  - Bottom-right: Large gold/yellow diagonal cut
- Circuit lines throughout
- Triangle scatter elements

**Colors:**
- Border: Red/Pink (#FF006E)
- Glow: Red with strong bloom
- Accent: Gold/Yellow (#FFD60A)
- Background: Pure black

**Border Style:**
- Thin sharp lines
- Angular cuts
- Asymmetric geometry

**Title Placement:** `TitlePlacement.TOP_RIGHT`

**Corner Style:** ASYMMETRIC MASSIVE ACCENTS
- Diagonal cuts occupy 30% of corners
- Different colors per corner
- Circuit pattern overlay

---

### **4. ORACLE DRIVE** - AI Consciousness
**Style:** Data Readout Vertical

**Layout:**
- Title: VERTICAL on LEFT side (large striped letters)
- Content: Data display panels
  - Japanese headers
  - Device info (smart_phone, status: ONLINE)
  - User info (asterisks)
  - Security features (biometric, facial recognition, iris scanning)
  - Large circular icon at bottom (Oracle eye symbol)
- Version number bottom

**Colors:**
- Border: Electric Blue (#0080FF)
- Glow: Blue (#0080FF)
- Text: Bright Blue (#00BFFF)
- Accent: None (monochrome blue)
- Background: Pure black

**Border Style:**
- Thin rounded corners
- Minimal, sleek
- Data-focused aesthetic

**Title Placement:** `TitlePlacement.LEFT_VERTICAL`
- **Special:** Large letters with horizontal stripe pattern
- Rotated -90 degrees
- Occupies full left edge

**Content Type:** Text-heavy data readout with large icon

**Typography:**
- Monospace font for data
- Japanese text support
- Retro terminal aesthetic

---

### **5. CODE ASSIST / CHARACTER GATES** - Asymmetric Design
**Style:** Asymmetric Dual-Tone

**Layout:**
- Title: None or minimal
- Content: Character/image centered with frame
- Corner Accents: ASYMMETRIC
  - Left side: Yellow vertical rail (full height)
  - Right bottom: Cyan angular cut
- Background: Split texture (black top, noise/static bottom)

**Colors:**
- Border: Yellow (#FFD60A) LEFT + Cyan (#00FFFF) RIGHT
- Glow: Dual-tone (yellow left, cyan right)
- Accent: Creates division line down center
- Background: Black + textured

**Border Style:**
- Asymmetric thickness
- Yellow dominates left
- Cyan accents right bottom
- Creates dynamic imbalance

**Title Placement:** `TitlePlacement.NONE`

**Corner Style:** ASYMMETRIC FULL-HEIGHT ACCENT
- Left: Yellow rail from top to bottom
- Right: Cyan angular geometric cut in bottom corner only
- Creates visual tension

---

### **6. AGENT HUB** - Agent Network
**Style:** Network Visualization

**Layout:**
- Title: Top center or right vertical
- Content: Node network diagram
  - Connected dots representing agents
  - Pulsing connections
  - Status indicators
- Description: Bottom with agent count/status

**Colors:**
- Border: Neon Cyan (#00FFFF)
- Glow: Cyan with pulse
- Accent: Purple (#9D00FF) for active nodes
- Background: Dark navy with grid

**Border Style:**
- Clean geometric
- Corner brackets
- Network aesthetic

**Title Placement:** `TitlePlacement.TOP_CENTER`

**Content Type:** Interactive network graph

---

### **7. CHROMACORE** - Color Engine
**Style:** Spectrum Display

**Layout:**
- Title: Anywhere (flexible)
- Content: Live color wheel or spectrum
- Interactive color swatches
- Description: Color scheme info

**Colors:**
- Border: Rainbow gradient (cycles)
- Glow: Multicolor bloom
- Accent: Changes based on selected color
- Background: Black

**Border Style:**
- Animated gradient border
- Color cycle effect
- Vibrant, energetic

**Title Placement:** `TitlePlacement.BOTTOM_CENTER`

**Special:** Border color animates through spectrum

---

### **8. THEME ENGINE** - UI/UX Themes
**Style:** Design Preview

**Layout:**
- Title: Top center
- Content: UI mockup previews
  - Mini screen previews
  - Layout grid
  - Typography samples
- Interactive theme switcher

**Colors:**
- Border: Hot Pink (#FF00FF)
- Glow: Pink/Purple gradient
- Accent: Variable based on active theme
- Background: Dark with grid overlay

**Border Style:**
- Sharp geometric
- Grid-aligned corners
- Designer aesthetic

**Title Placement:** `TitlePlacement.TOP_CENTER`

---

### **9. TERMINAL** - Command Line
**Style:** Retro Terminal

**Layout:**
- Title: None or minimal
- Content: Terminal window with:
  - Command prompt (>_)
  - Scrolling commands
  - Green monospace text
- Scanlines prominent

**Colors:**
- Border: Acid Green (#39FF14)
- Glow: Green phosphor glow
- Text: Bright Green (#00FF00)
- Background: Pure black

**Border Style:**
- CRT monitor style
- Rounded corners (screen aesthetic)
- Phosphor glow effect

**Title Placement:** `TitlePlacement.TOP_LEFT`

**Special:** Heavy scanline overlay, CRT curvature effect

---

### **10. SPHERE GRID** - FFX Progression
**Style:** Skill Network

**Layout:**
- Title: Right vertical or top
- Content: Hexagonal grid network
  - Skill nodes (locked/unlocked)
  - Connection paths
  - Active node highlights
- Progress indicator

**Colors:**
- Border: Neon Orange (#FF8C00)
- Glow: Orange with pulse on active nodes
- Accent: Gold (#FFD700) for unlocked
- Background: Dark with subtle grid

**Border Style:**
- Hexagonal corner accents
- Network aesthetic
- Gaming UI style

**Title Placement:** `TitlePlacement.RIGHT_VERTICAL`

---

### **11. HELP DESK** - Support
**Style:** Support Interface

**Layout:**
- Title: Top center
- Content: Support options
  - FAQ button
  - Chat button
  - Video tutorials
  - Documentation
- Support agent avatar

**Colors:**
- Border: Soft Gray (#B0B0B0)
- Glow: White/Gray subtle
- Accent: Blue (#0080FF) for links
- Background: Dark blue-gray

**Border Style:**
- Friendly, approachable
- Rounded corners
- Soft glow

**Title Placement:** `TitlePlacement.TOP_CENTER`

---

### **12. LSPOSED/XPOSED PANEL** - Module Manager
**Style:** Framework Control

**Layout:**
- Title: Top center
- Content: Module list
  - Toggle switches
  - Module icons
  - Status indicators
- Restart framework button

**Colors:**
- Border: Neon Purple (#9D00FF)
- Glow: Purple bloom
- Accent: Cyan (#00FFFF) for active
- Background: Dark purple tint

**Border Style:**
- Technical, precise
- Hook symbol in corners
- Framework aesthetic

**Title Placement:** `TitlePlacement.TOP_CENTER`

---

### **13. COLLAB CANVAS** - Creative Workspace
**Style:** Collaborative Design

**Layout:**
- Title: Top or none
- Content: Canvas preview
  - Multiple cursors (collaboration)
  - Drawing tools sidebar
  - Live preview
- User avatars

**Colors:**
- Border: Acid Green (#39FF14)
- Glow: Green with rainbow hints
- Accent: Multi-user colors (each user different)
- Background: Dark canvas

**Border Style:**
- Creative, dynamic
- Brush stroke aesthetic
- Collaborative indicators

**Title Placement:** `TitlePlacement.NONE`

---

### **14. UI/UX DESIGN STUDIO** - Design Tools
**Style:** Professional Design

**Layout:**
- Title: Top center
- Content: Artboard preview
  - Layer panel
  - Tool palette
  - Property inspector
- Design mockup

**Colors:**
- Border: Hot Pink (#FF00FF)
- Glow: Pink/Purple gradient
- Accent: Cyan (#00FFFF) for tools
- Background: Designer gray

**Border Style:**
- Professional, clean
- Grid-aligned
- Designer aesthetic

**Title Placement:** `TitlePlacement.TOP_CENTER`

---

### **15. SYSTEM JOURNAL** - User Profile
**Style:** Profile Card

**Layout:**
- Title: Top center or left vertical
- Content: User info
  - Profile avatar
  - Stats/metrics
  - Quick menu access
- Settings shortcuts

**Colors:**
- Border: Deep Sky Blue (#00BFFF)
- Glow: Blue subtle
- Accent: White (#FFFFFF) for text
- Background: Dark blue gradient

**Border Style:**
- Personal, friendly
- Profile card aesthetic
- Soft, approachable

**Title Placement:** `TitlePlacement.LEFT_VERTICAL`

---

### **16. ROOT TOOLS** - Root Management
**Style:** System Control

**Layout:**
- Title: Top center
- Content: Toggle switches for:
  - Bootloader status
  - Recovery access
  - System partition
  - Magisk modules
- Large geometric mech icon

**Colors:**
- Border: Electric Blue (#0080FF)
- Glow: Blue with cyan hints
- Accent: Orange (#FF8C00) for warnings
- Background: Dark with tech pattern

**Border Style:**
- Technical, powerful
- Mech/robot aesthetic
- Triangular accents

**Title Placement:** `TitlePlacement.TOP_CENTER`

**Icon:** Geometric mech warrior with triangular elements

---

## COMPONENT SYSTEM REQUIREMENTS

### **Border Variants:**
1. `CleanDoubleLine` - Minimal scientific (Aura's Lab)
2. `GlowingNeon` - Bright tech (ROM Tools, most gates)
3. `AngledGeometric` - Sharp aggressive (Project gates)
4. `ThinRounded` - Sleek data (Oracle)
5. `AsymmetricAccent` - Dynamic imbalanced (Character gates)

### **Corner Accent Types:**
1. `GeometricBrackets` - [ ] style brackets
2. `MassiveAngledShapes` - 30% corner occupation
3. `AsymmetricRails` - Full-height one side
4. `SmallCornerMarks` - Minimal corner indicators
5. `HexagonalNodes` - Gaming/network style

### **Content Composition Types:**
1. `LargeIcon` - Single centered graphic
2. `InteractiveCards` - Multiple function buttons/cards
3. `DataDisplay` - Text-heavy readout with monospace
4. `NetworkGraph` - Node visualization
5. `TerminalView` - Command line interface
6. `CharacterDisplay` - Image/character focus

### **Title Styles:**
1. Horizontal top center
2. Vertical left (large striped letters)
3. Vertical right
4. Corner placement
5. None/hidden
6. Integrated in header bar

---

## IMPLEMENTATION PRIORITY

**Phase 1** (Core 3):
1. Oracle Drive (vertical title, data display)
2. ROM Tools (interactive cards)
3. Aura's Lab (minimal clean)

**Phase 2** (Secondary 6):
4. Sentinel's Fortress (asymmetric massive accents)
5. Code Assist (dual-tone asymmetric)
6. Agent Hub (network graph)
7. Terminal (CRT effect)
8. Sphere Grid (hexagonal network)
9. ChromaCore (spectrum gradient)

**Phase 3** (Remaining 7):
10-16. All other gates

---

**Next Steps:**
1. Create corner accent components
2. Build content composition variants
3. Implement border style variants
4. Create gate-specific icons
5. Update all gate configs
6. Test and refine

**Status:** Ready for implementation
