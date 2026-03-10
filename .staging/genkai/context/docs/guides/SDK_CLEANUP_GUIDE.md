# Android SDK Cleanup Guide

## 🎯 Project Requirements

Based on `gradle/libs.versions.toml` and `app/build.gradle.kts`:

```toml
compile-sdk = "36"
min-sdk = "34"
target-sdk = "36"
ndk = "29.0.14206865"
cmake = "3.7.0"
agp = "9.0.0-alpha13"
```

## ✅ Required SDK Components

### Essential (Keep These)
- **SDK Platform 36** - Required for compileSdk
- **Build-Tools 36.1.0** - Latest stable for AGP 9.0
- **Platform-Tools 29.0.14206865** - Matches NDK requirement
- **CMake 3.31.x** - Newer than required 3.7.0 (backward compatible)
- **NDK 29.0.14206865** - Exact version match

### Recommended (Keep for compatibility)
- **Build-Tools 35.0.0** - Fallback for stability
- **Build-Tools 34.0.0** - For older project support
- **Platform-Tools 28.x** - Stable release channel

## 🗑️ Safe to Remove

### Build-Tools (Old Versions)
You have **60+ old Build-Tools versions** consuming ~15GB of disk space.

**Safe to uninstall:**
- All versions below **34.0.0**
- All RC (Release Candidate) versions
- Versions: 19.1.0 through 33.0.3

**Keep only:**
- 36.1.0 (latest)
- 35.0.0 (stable fallback)
- 34.0.0 (compatibility)

### Platform-Tools (Old Versions)
You have **30+ old Platform-Tools** consuming ~5GB.

**Safe to uninstall:**
- All versions below **27.0.12077973**
- All RC versions
- Versions: 16.1.x through 26.x

**Keep only:**
- 29.0.14206865 (matches NDK)
- 28.2.13676358 (stable)
- 27.3.13750724 (fallback)

### SDK Platforms (Old APIs)
Your project targets API 34-36 (Android 14-15).

**Safe to uninstall:**
- SDK Platform 1.0 through 19.0 (ancient)
- Keep only: API 34, 35, 36

### CMake (Old Versions)
**Safe to uninstall:**
- All versions below 3.22.1
- Keep: 3.31.x and 4.x versions

## 📊 Current Status Analysis

### Issues Found:

1. **PatchedRasterizedImageDataLoader Errors**
   - These are harmless IDE rendering errors
   - Occur for packages marked "Not installed"
   - No action needed

2. **Update Available**
   - Component: Unknown (ID 36.3.5)
   - Update to: 36.3.7
   - **Action**: Install update via SDK Manager

3. **Disk Space Usage**
   - Estimated old SDK components: **~25GB**
   - After cleanup: **~5GB**
   - **Potential savings: 20GB**

## 🛠️ Cleanup Commands

### Option 1: Android Studio SDK Manager GUI

1. Open Android Studio
2. Go to: **Settings → Appearance & Behavior → System Settings → Android SDK**
3. Switch to **SDK Tools** tab
4. Check "Show Package Details"
5. Uncheck old versions listed above
6. Click "Apply" to uninstall

### Option 2: Command Line (via sdkmanager)

```bash
# List all installed packages
sdkmanager --list_installed

# Uninstall old Build-Tools (example for versions 19-33)
for version in 19.1.0 20.0.0 21.1.2 22.0.1 23.0.{1,2,3} 24.0.{0,1,2,3} \
               25.0.{0,1,2,3} 26.0.{0,1,2,3} 27.0.{0,1,2,3} 28.0.{0,1,2,3} \
               29.0.{0,1,2,3} 30.0.{0,1,2,3} 31.0.0 32.0.0 33.0.{0,1,2,3}; do
  sdkmanager --uninstall "build-tools;$version"
done

# Uninstall old Platform-Tools
for version in 16.1.4479499 18.1.5063045 19.2.5345600 20.0.5594570 \
               21.0.6011959 22.0.7026061 22.1.7171670 23.2.8568313; do
  sdkmanager --uninstall "platform-tools;$version" 2>/dev/null || true
done

# Uninstall old SDK Platforms
for api in 1.0 4.0 8.0 16.0 19.0; do
  sdkmanager --uninstall "platforms;android-$api" 2>/dev/null || true
done

# Uninstall old CMake versions
sdkmanager --uninstall "cmake;3.6.4111459"
sdkmanager --uninstall "cmake;3.10.2"
sdkmanager --uninstall "cmake;3.18.1"

# Install update (if component ID known)
sdkmanager --update

# Accept all licenses
sdkmanager --licenses
```

### Option 3: Manual Cleanup

```bash
# Navigate to SDK directory
cd $ANDROID_HOME  # or ~/Android/Sdk

# Remove old Build-Tools manually
rm -rf build-tools/{19.1.0,20.0.0,21.1.2,22.0.1,23.0.*,24.0.*,25.0.*,26.0.*,27.0.*,28.0.*,29.0.*,30.0.*,31.0.0,32.0.0,33.0.*}

# Remove old Platform-Tools (be careful!)
cd platform-tools
# Backup current first
cp -r . ../platform-tools-backup
# Then remove old versions via SDK Manager GUI

# Check disk space saved
du -sh build-tools platform-tools platforms
```

## 🔄 Recommended Actions

### Immediate:
1. ✅ **Install SDK Platform 36** (if not present)
2. ✅ **Update component 36.3.5 → 36.3.7**
3. ✅ **Keep Build-Tools 34.0.0, 35.0.0, 36.1.0**
4. ✅ **Keep Platform-Tools 29.0.14206865**

### Optional (Disk Space Recovery):
5. 🗑️ **Uninstall Build-Tools < 34.0.0** (~10GB saved)
6. 🗑️ **Uninstall Platform-Tools < 27.0** (~3GB saved)
7. 🗑️ **Uninstall SDK Platforms < API 34** (~5GB saved)
8. 🗑️ **Uninstall CMake < 3.22** (~2GB saved)

### Low Priority:
9. 🧹 **Remove all RC (Release Candidate) versions**
10. 🧹 **Clear SDK cache**: `rm -rf ~/.android/cache/*`

## 📋 Verification Checklist

After cleanup, verify your project still builds:

```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Run tests
./gradlew test

# Check SDK requirements
./gradlew dependencies --configuration debugCompileClasspath
```

## 🎯 Minimal SDK Configuration

For this project, you **only need**:

```
Android SDK Platform 36
Build-Tools 36.1.0
Platform-Tools 29.0.14206865
CMake 3.31.x
NDK 29.0.14206865
Android SDK Command-line Tools (latest)
```

**Everything else is optional** and can be removed.

## 🚨 Warnings

1. **Don't uninstall while Android Studio is running**
2. **Don't remove Platform-Tools completely** (critical for ADB)
3. **Back up** if unsure: `cp -r $ANDROID_HOME ~/android-sdk-backup`
4. **Test build** after cleanup to ensure nothing breaks

## 📊 Expected Results

**Before Cleanup:**
- SDK Directory Size: ~35GB
- Build-Tools: 60+ versions
- Platform-Tools: 30+ versions

**After Cleanup:**
- SDK Directory Size: ~10GB
- Build-Tools: 3 versions (34, 35, 36)
- Platform-Tools: 3 versions (27, 28, 29)
- **Disk Space Saved: ~25GB**

## 🔧 Troubleshooting

### If build fails after cleanup:

```bash
# Reinstall missing components
sdkmanager "build-tools;36.1.0"
sdkmanager "platforms;android-36"
sdkmanager "platform-tools"
sdkmanager "ndk;29.0.14206865"
sdkmanager "cmake;3.31.1"

# Sync Gradle
./gradlew --refresh-dependencies
```

### If SDK Manager shows errors:

```bash
# Update SDK Manager itself
sdkmanager --update

# Clear SDK cache
rm -rf ~/.android/cache

# Restart Android Studio
```

## 📝 Notes

- **PatchedRasterizedImageDataLoader errors** are cosmetic IDE issues, not actual problems
- Your project is configured for **Android 14-15** (API 34-36)
- Using **bleeding-edge AGP 9.0.0-alpha13** - ensure SDK components are latest
- **NDK version must match exactly**: 29.0.14206865
- **CMake 3.31.x is newer** than required 3.7.0 - this is fine (forward compatible)

---

**Generated**: 2025-11-08
**Project**: AuraFrameFx
**Target SDK**: 36 (Android 15)
**Min SDK**: 34 (Android 14)
