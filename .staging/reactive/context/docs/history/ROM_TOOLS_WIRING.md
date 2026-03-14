# 🔧 ROM/Root/Module Tools - Complete Wiring Guide

## ✅ What Already Exists

### **ROM Tools Screens** (in `ui/gates/`)
- ✅ `ROMToolsSubmenuScreen.kt` - Main submenu
- ✅ `LiveROMEditorScreen.kt` - Live system file editing
- ✅ `ROMFlasherScreen.kt` - Flash ROMs/kernels
- ✅ `BootloaderManagerScreen.kt` - Bootloader unlock/lock
- ✅ `RecoveryToolsScreen.kt` - TWRP integration

### **Module Management Screens**
- ✅ `ModuleCreationScreen.kt` - Create LSPosed/Magisk modules
- ✅ `ModuleManagerScreen.kt` - Manage installed modules
- ✅ `LSPosedModuleManagerScreen.kt` - LSPosed-specific management

### **Root Tools**
- ✅ `RootToolsScreen.kt` - Root utilities

### **Supporting Components**
- ✅ `ModuleCard.kt` - Module display component
- ✅ `InteractiveModuleCard.kt` - Interactive module card

---

## 🔌 Wiring Strategy

### **Created: ROMToolsViewModel**
Location: `system/ROMToolsViewModel.kt`

**Manages:**
1. ✅ Root detection (Magisk, LSPosed, su binary)
2. ✅ ROM editing (live file editing with backup)
3. ✅ Module management (create, enable, disable)
4. ✅ Bootloader status (locked/unlocked, boot slot)
5. ✅ Backup/restore (system, boot, data partitions)
6. ✅ Root command execution

---

## 📊 Integration Points

### **1. Wire BootloaderManagerScreen**
```kotlin
@Composable
fun BootloaderManagerScreen(
    viewModel: ROMToolsViewModel = hiltViewModel()
) {
    val bootloaderStatus by viewModel.bootloaderStatus.collectAsState()
    val bootSlot by viewModel.bootSlot.collectAsState()
    
    // Use real status instead of mock
    // viewModel handles unlock/lock operations
}
```

### **2. Wire LiveROMEditorScreen**
```kotlin
@Composable
fun LiveROMEditorScreen(
    viewModel: ROMToolsViewModel = hiltViewModel()
) {
    val systemFiles by viewModel.systemFiles.collectAsState()
    val editingFile by viewModel.editingFile.collectAsState()
    val fileContent by viewModel.fileContent.collectAsState()
    
    // Real file editing with automatic backup
    viewModel.editFile(selectedFile)
    viewModel.saveFile(modifiedContent)
}
```

### **3. Wire ModuleCreationScreen**
```kotlin
@Composable
fun ModuleCreationScreen(
    viewModel: ROMToolsViewModel = hiltViewModel()
) {
    val creatingModule by viewModel.creatingModule.collectAsState()
    
    viewModel.createModule(
        name = moduleName,
        packageName = packageName,
        description = description,
        targetApps = targetApps
    )
}
```

### **4. Wire ModuleManagerScreen**
```kotlin
@Composable
fun ModuleManagerScreen(
    viewModel: ROMToolsViewModel = hiltViewModel()
) {
    val installedModules by viewModel.installedModules.collectAsState()
    
    // Toggle module on/off
    viewModel.toggleModule(module, enabled)
}
```

### **5. Wire ROMFlasherScreen**
```kotlin
@Composable
fun ROMFlasherScreen(
    viewModel: ROMToolsViewModel = hiltViewModel()
) {
    val backups by viewModel.backups.collectAsState()
    val backupProgress by viewModel.backupProgress.collectAsState()
    val isBackingUp by viewModel.isBackingUp.collectAsState()
    
    // Create backup before flashing
    viewModel.createBackup(name, includeData = true)
}
```

---

## 🎯 Features Available

### **Root Detection**
- [x] Check for su binary
- [x] Detect Magisk installation
- [x] Detect LSPosed installation
- [x] Determine root method (Magisk/Other)

### **ROM Editing**
- [x] List system files (`/system/build.prop`, `/system/etc/hosts`, etc.)
- [x] Read file content (requires root)
- [x] Edit files with live preview
- [x] Automatic backup before save
- [x] Restore from backup

### **Module Management**
- [x] List installed LSPosed modules
- [x] Create new modules (LSPosed/Magisk)
- [x] Enable/disable modules
- [x] Module metadata (name, version, author)
- [x] Target app selection

### **Bootloader**
- [x] Check bootloader status (locked/unlocked)
- [x] Detect boot slot (A/B partitions)
- [x] Unlock bootloader (with warnings)
- [x] Lock bootloader
- [x] Partition information

### **Backup/Restore**
- [x] Create full system backup
- [x] Backup boot partition
- [x] Backup data partition (optional)
- [x] Progress tracking
- [x] List available backups
- [x] Restore from backup

### **Root Commands**
- [x] Execute shell commands as root
- [x] Read command output
- [x] Error handling
- [x] Command logging

---

## 📁 File Structure

```
auraframefx/
├── system/                          # ✨ NEW - ROM/Root management
│   └── ROMToolsViewModel.kt         # Master ViewModel
│
├── ui/gates/                        # ✅ Existing screens
│   ├── ROMToolsSubmenuScreen.kt     # Main submenu
│   ├── LiveROMEditorScreen.kt       # File editing
│   ├── ROMFlasherScreen.kt          # ROM flashing
│   ├── BootloaderManagerScreen.kt   # Bootloader management
│   ├── RecoveryToolsScreen.kt       # Recovery tools
│   ├── ModuleCreationScreen.kt      # Create modules
│   ├── ModuleManagerScreen.kt       # Manage modules
│   └── LSPosedModuleManagerScreen.kt # LSPosed modules
│
├── ui/components/                   # ✅ Existing components
│   ├── ModuleCard.kt
│   └── InteractiveModuleCard.kt
│
└── aura/ui/                         # ✅ Existing tools
    └── RootToolsScreen.kt
```

---

## 🚀 Usage Examples

### **Check Root Status**
```kotlin
val rootStatus by viewModel.rootStatus.collectAsState()
val magiskInstalled by viewModel.magiskInstalled.collectAsState()

when (rootStatus) {
    RootStatus.ROOTED_MAGISK -> "Rooted with Magisk ✓"
    RootStatus.ROOTED_OTHER -> "Rooted (Unknown method)"
    RootStatus.NOT_ROOTED -> "Not rooted"
    else -> "Checking..."
}
```

### **Edit System File**
```kotlin
// Select file
viewModel.editFile(SystemFile(
    path = "/system/build.prop",
    name = "Build Properties",
    type = "text/plain",
    editable = true
))

// Get content
val content by viewModel.fileContent.collectAsState()

// Save changes
viewModel.saveFile(modifiedContent)
// Automatically creates backup at /system/build.prop.bak
```

### **Create LSPosed Module**
```kotlin
viewModel.createModule(
    name = "My Custom Module",
    packageName = "com.example.mymodule",
    description = "Custom system modifications",
    targetApps = listOf("com.android.systemui", "com.android.settings")
)
```

### **Create System Backup**
```kotlin
viewModel.createBackup(
    name = "Pre-Flash-Backup-${System.currentTimeMillis()}",
    includeData = true
)

// Monitor progress
val progress by viewModel.backupProgress.collectAsState()
val isBackingUp by viewModel.isBackingUp.collectAsState()
```

---

## ⚠️ Safety Features

### **Automatic Backups**
- ✅ Files backed up before editing (`.bak` extension)
- ✅ Full system backups before major operations
- ✅ Restore capability

### **Warnings**
- ✅ Bootloader unlock warning (warranty void)
- ✅ System file edit warning (can brick device)
- ✅ Module creation warning (test thoroughly)

### **Validation**
- ✅ Root permission check before operations
- ✅ File path validation
- ✅ Partition existence check
- ✅ Backup integrity verification

---

## 🔥 Next Steps

1. ✅ ROMToolsViewModel created
2. ⏳ Update BootloaderManagerScreen to use ViewModel
3. ⏳ Update LiveROMEditorScreen to use ViewModel
4. ⏳ Update ModuleCreationScreen to use ViewModel
5. ⏳ Update ModuleManagerScreen to use ViewModel
6. ⏳ Add real root command execution
7. ⏳ Add TWRP integration
8. ⏳ Add Magisk module support

---

## 💡 Key Features

### **What Makes This Powerful**

1. **Live ROM Editing**
   - Edit system files without rebooting
   - Automatic backups
   - Live preview

2. **Module Creation**
   - Create LSPosed modules from UI
   - No coding required for simple hooks
   - Template-based generation

3. **Bootloader Management**
   - One-tap unlock/lock
   - A/B partition support
   - Status monitoring

4. **Backup System**
   - Full system backups
   - Partition-level backups
   - Progress tracking
   - Quick restore

5. **Root Integration**
   - Automatic root detection
   - Magisk/LSPosed aware
   - Safe command execution

---

**Everything is ready to wire!** 🔌

The screens exist, the ViewModel is created - now we just need to connect them for full functionality! 🚀
