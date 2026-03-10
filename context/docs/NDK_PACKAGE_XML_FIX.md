# NDK package.xml Corruption Fix

## Problem Description

When building the project, you may encounter errors like:

```
[CXX5304] Found corrupted package.xml at C:\Users\...\Android\Sdk\ndk\25.0.8775105\package.xml
[CXX5304] Invalid package.xml found at C:\Users\...\Android\Sdk\ndk\25.0.8775105\package.xml and failed to parse using fallback.
[CXX5304] package.xml parsing problem. 文件提前结束。:
org.xml.sax.SAXParseException; lineNumber: 1; columnNumber: 1; 文件提前结束。
```

**Translation:** "文件提前结束。" means "Premature end of file."

## Root Cause

The Android build system scans **all** installed NDK versions in your SDK directory. If any NDK installation has a corrupted (empty or truncated) `package.xml` file, the build fails with a SAXParseException.

This commonly occurs when:
- NDK installations are interrupted
- Disk space runs out during installation
- File system corruption
- Incomplete downloads or extractions

## Quick Fix

### Windows Users

1. Open PowerShell as Administrator
2. Navigate to the project root directory
3. Run the fix script:

```powershell
.\fix-ndk-package-xml.ps1
```

### Linux/Mac Users

1. Open Terminal
2. Navigate to the project root directory
3. Make the script executable (first time only):

```bash
chmod +x fix-ndk-package-xml.sh
```

4. Run the fix script:

```bash
./fix-ndk-package-xml.sh
```

## What the Script Does

The fix script:

1. **Detects** your Android SDK location automatically
2. **Scans** all installed NDK versions
3. **Identifies** corrupted or missing `package.xml` files
4. **Creates backups** of existing corrupted files (`.backup` extension)
5. **Regenerates** valid `package.xml` files with proper structure
6. **Reports** summary of fixes applied

## Manual Fix (Alternative Method)

If you prefer to fix the issue manually:

### Option 1: Fix Individual package.xml Files

For each corrupted NDK version (e.g., `25.0.8775105`), create a valid `package.xml`:

**Location:** `%LOCALAPPDATA%\Android\Sdk\ndk\25.0.8775105\package.xml` (Windows)
or `~/Android/Sdk/ndk/25.0.8775105/package.xml` (Linux/Mac)

**Content:**
```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<ns2:repository xmlns:ns2="http://schemas.android.com/repository/android/common/01" xmlns:ns3="http://schemas.android.com/sdk/android/repo/addon2/01" xmlns:ns4="http://schemas.android.com/repository/android/generic/01" xmlns:ns5="http://schemas.android.com/sdk/android/repo/repository2/01" xmlns:ns6="http://schemas.android.com/sdk/android/repo/sys-img2/01">
    <localPackage path="ndk;25.0.8775105" obsolete="false">
        <type-details xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="ns4:genericDetailsType"/>
        <revision>
            <major>25</major>
            <minor>0</minor>
            <micro>8775105</micro>
        </revision>
        <display-name>NDK (Side by side) 25.0.8775105</display-name>
    </localPackage>
</ns2:repository>
```

**Important:** Replace version numbers (`25`, `0`, `8775105`) and path (`ndk;25.0.8775105`) to match your specific NDK version.

### Option 2: Delete Unused NDK Versions

If you don't need older NDK versions:

1. Open Android Studio > Tools > SDK Manager > SDK Tools tab
2. Expand "NDK (Side by side)"
3. Uncheck versions you don't use (keep 29.0.14206865 for this project)
4. Click "Apply" to remove unused versions

**Note:** This project uses NDK version **29.0.14206865** as specified in `gradle/libs.versions.toml`.

### Option 3: Specify Exact NDK Path

Add to your `local.properties` file:

```properties
ndk.dir=C:\\Users\\YourName\\AppData\\Local\\Android\\Sdk\\ndk\\29.0.14206865
```

This tells Gradle to use a specific NDK version instead of scanning all installed versions.

## Prevention

To prevent this issue in the future:

### 1. Configure Specific NDK Version

The project already specifies NDK version in:
- `gradle.properties` (line 56): `android.ndkVersion=29.0.14206865`
- `gradle/libs.versions.toml` (line 185): `ndk = "29.0.14206865"`
- `app/build.gradle.kts` (line 25): `ndkVersion = libs.versions.ndk.get()`

### 2. Use local.properties Template

Copy `local.properties.template` to `local.properties` and configure your SDK path:

```bash
# Windows
copy local.properties.template local.properties

# Linux/Mac
cp local.properties.template local.properties
```

Then edit `local.properties` to specify your SDK location.

### 3. Keep NDKs Up to Date

Use Android Studio SDK Manager to:
- Keep installed NDKs updated
- Remove unused NDK versions
- Reinstall corrupted NDKs

## Technical Details

### Error Breakdown

```
org.xml.sax.SAXParseException; lineNumber: 1; columnNumber: 1; 文件提前结束。
```

- **SAXParseException:** XML parser error
- **lineNumber: 1; columnNumber: 1:** Error at the very start of the file
- **文件提前结束 (Premature end of file):** File is empty or truncated

### Why This Happens

The Android Gradle Plugin (AGP) 9.0+ uses:
- `com.android.repository.impl.manager.LocalRepoLoaderImpl` to scan SDK packages
- `com.android.build.gradle.internal.cxx.configure.CmakeLocator` to find CMake installations
- These components parse `package.xml` for **all** NDK versions during configuration

Even if your project specifies a specific NDK version, the build system still **scans all NDKs** to build an inventory. If any `package.xml` is corrupted, the build fails.

### What package.xml Contains

The `package.xml` file describes:
- Package path (e.g., `ndk;25.0.8775105`)
- Version information (major, minor, micro)
- Display name
- Package type and metadata

This metadata is used by Android Studio SDK Manager and Gradle build system.

## Verification

After running the fix script, verify the fix worked:

1. **Check the script output** for "Successfully fixed" count
2. **Run a Gradle sync:**
   ```bash
   ./gradlew --refresh-dependencies
   ```
3. **Build the project:**
   ```bash
   ./gradlew build
   ```

If you still see errors, run the script again or manually inspect the `package.xml` files.

## Related Files

- `fix-ndk-package-xml.ps1` - PowerShell fix script (Windows)
- `fix-ndk-package-xml.sh` - Bash fix script (Linux/Mac)
- `local.properties.template` - Template for local SDK configuration
- `gradle.properties` - Project-wide Gradle settings (includes NDK version)
- `gradle/libs.versions.toml` - Version catalog (defines NDK 29.0.14206865)

## FAQ

**Q: Why do I have multiple NDK versions installed?**
A: Android Studio installs NDK versions as needed for different projects. Each NDK version can coexist side-by-side.

**Q: Can I delete all NDKs except the one I need?**
A: Yes, but ensure you keep the version required by your project (29.0.14206865). Other projects may need different versions.

**Q: Will this affect other Android projects?**
A: No, the fix only regenerates metadata files. It doesn't modify NDK binaries or affect other projects.

**Q: Do I need to run the script every time I build?**
A: No, you only need to run it once to fix corrupted files. The fix is permanent unless the files get corrupted again.

**Q: What if the script doesn't work?**
A: Try these alternatives:
1. Reinstall the corrupted NDK version via Android Studio SDK Manager
2. Delete the corrupted NDK directory entirely
3. Use `ndk.dir` in `local.properties` to specify exact NDK path

## Support

If you continue experiencing issues:

1. Check that you have NDK 29.0.14206865 installed
2. Verify your Android SDK path in `local.properties`
3. Ensure you have write permissions to the SDK directory
4. Review the error logs for other potential issues

For project-specific issues, open a GitHub issue with:
- Full error message
- Output from the fix script
- Your NDK versions (run `ls $ANDROID_SDK_ROOT/ndk` or `dir %ANDROID_SDK_ROOT%\ndk`)
