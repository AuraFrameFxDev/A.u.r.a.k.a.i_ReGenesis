# 🎨 Gate PNG Asset Guide

## Location
Place all PNG files in: `app/src/main/res/drawable/`

## File Naming Rules
- **MUST be lowercase** (Android requirement)
- Use underscores, not spaces
- No capital letters allowed

## Gate Assets Mapping

| Gate Title | Expected PNG File Name | Current Status |
|:-----------|:----------------------|:---------------|
| **Root Tools** | `gate_root_tools.png` | ✅ Fixed (was gate_romtools.png) |
| **System Monitor** | `gate_system_monitor.png` | ❓ Add if needed |
| **Sentinel's Fortress** | `gate_secure_comm.png` | ✅ Exists |
| **Firewall** | `gate_firewall.png` | ❓ Add if needed |
| **ChromaCore** | `gate_chromacore.png` | ✅ Exists |
| **CollabCanvas** | `gate_collab_canvas.png` | ✅ Exists |
| **Agent Hub** | `gate_agent_hub.png` | ✅ Exists |
| **Sphere Grid** | `gate_sphere_grid.png` | ❓ Add if needed |
| **Growth Metrics** | `gate_growth_metrics.png` | ❓ Add if needed |
| **Aura's Lab** | `gate_auras_lab.png` | ✅ Exists |

## Additional Assets Found
- `sentinel_gate.png` - ✅ Fixed (was Sentinelgate.png with capital S)
- `collabcanvasgate.png` - Consider renaming to match pattern
- `gateframe.png` - Frame asset (OK)
- `lsposedgate.png` - LSPosed related (OK)

## Quick Fix Applied
- Renamed `Sentinelgate.png` → `sentinel_gate.png` (lowercase)
- Renamed `gate_romtools.png` → `gate_root_tools.png` (correct name)

## Notes
- If a PNG is missing, the gate will still display but without the pixel art
- All files MUST be lowercase or the build will fail
- Use PNG format for best compatibility
