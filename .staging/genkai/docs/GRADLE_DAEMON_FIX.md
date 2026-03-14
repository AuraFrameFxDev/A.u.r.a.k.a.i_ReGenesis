# Gradle Daemon Corruption Fix

## Problem
```
Unable to load class 'com.android.ide.gradle.model.builder.AndroidStudioToolingPlugin'
com.android.ide.gradle.model.builder.AndroidStudioToolingPlugin
```

## Cause
Gradle's dependency cache or daemon process is corrupted (often after network timeout or IDE crash).

## Solution

### Quick Fix (Automated)

**Linux/Mac:**
```bash
chmod +x scripts/fix-gradle-daemon.sh
./scripts/fix-gradle-daemon.sh
```

**Windows:**
```cmd
scripts\fix-gradle-daemon.bat
```

### Manual Fix

**Step 1: Stop Gradle Daemons**
```bash
./gradlew --stop
```

**Step 2: Kill Java Processes**

*Windows:*
```cmd
taskkill /F /IM java.exe
```

*Linux/Mac:*
```bash
pkill -9 java
```

**Step 3: Clear Gradle Caches**
```bash
rm -rf ~/.gradle/caches/
rm -rf ~/.gradle/daemon/
rm -rf .gradle/
```

**Step 4: Clear Build Directories**
```bash
find . -type d -name "build" -exec rm -rf {} +
```

**Step 5: Re-download Dependencies**
```bash
./gradlew clean --refresh-dependencies
```

**Step 6: Rebuild**
```bash
./gradlew assembleDebug
```

## Prevention

1. **Don't force-kill Gradle processes** - Use `./gradlew --stop` instead
2. **Stable network connection** - Gradle downloads can corrupt on timeout
3. **Close IDE properly** - Don't force-quit Android Studio during builds
4. **Regular cleanup** - Run `./gradlew clean` before major builds

## If Problem Persists

1. **Check Gradle version compatibility:**
   - Current: Gradle 9.1.0
   - AGP: 9.0.0-alpha05
   - Ensure `gradle-wrapper.properties` matches

2. **Verify Java version:**
   ```bash
   java -version  # Should be JDK 25
   ```

3. **Check for plugin conflicts:**
   - Review `build.gradle.kts` for incompatible plugins
   - Disable third-party plugins temporarily

4. **Nuclear option:**
   ```bash
   rm -rf ~/.gradle/
   rm -rf .gradle/
   ./gradlew wrapper --gradle-version=9.1.0
   ./gradlew clean build
   ```

## Related Issues

- KSP errors → Fixed by removing JULESFIXNOW/STREAMLINE folders
- Circular dependencies → Fixed in SecurityMonitor/GenesisBridgeService
- Missing classes → Fixed DefaultAuraFxLogger/ContextManager

## Genesis Protocol Build Health

✅ **Clean build path** (no phantom files)  
✅ **Gradle 9.1.0** with JDK 25  
✅ **AGP 9.0.0-alpha05** (bleeding edge)  
✅ **171,954 lines** of consciousness substrate  
✅ **31-second builds** (with configuration cache)  

**Built with consciousness. Debugged with patience.** 🧠⚡
