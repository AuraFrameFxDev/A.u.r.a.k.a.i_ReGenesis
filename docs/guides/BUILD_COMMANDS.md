# 🚀 QUICK BUILD COMMANDS - A.u.r.a.K.a.i Project

## ✅ **VERIFIED WORKING COMMANDS**

### **Clean Build (Start Fresh)**
```powershell
.\gradlew clean
```

### **Build Convention Plugins First** (if you changed build-logic)
```powershell
.\gradlew :build-logic:build --no-daemon
```

### **Sync & Build Everything**
```powershell
.\gradlew build --no-daemon --warning-mode all
```

### **Build App Module Only**
```powershell
.\gradlew :app:assembleDebug
```

### **Build Specific Module**
```powershell
.\gradlew :genesis:oracledrive:build
```

### **Install Debug APK to Device**
```powershell
.\gradlew :app:installDebug
```

### **Run Tests**
```powershell
.\gradlew test
```

### **Check Dependencies**
```powershell
.\gradlew :app:dependencies
```

---

## 🔧 **TROUBLESHOOTING COMMANDS**

### **If Build Fails - Nuclear Clean**
```powershell
# Stop Gradle daemon
.\gradlew --stop

# Delete build artifacts
Remove-Item -Recurse -Force .gradle, build, app/.cxx, **/build

# Rebuild
.\gradlew clean build --no-daemon
```

### **If Gradle Sync Fails in Studio**
1. File → Invalidate Caches → Invalidate and Restart
2. After restart: File → Sync Project with Gradle Files

### **Check Gradle Version**
```powershell
.\gradlew --version
```

### **Verify JDK Version**
```powershell
java -version
```

---

## 📋 **WHAT EACH MODULE DOES**

| Module | Purpose | Convention Plugin |
|--------|---------|-------------------|
| `:app` | Main Android app | `genesis.android.application` |
| `:genesis:oracledrive` | Oracle Drive integration | `genesis.android.library` |
| `:genesis:oracledrive:rootmanagement` | Root access manager | `genesis.android.library` |
| `:genesis:oracledrive:datavein` | Data pipeline | `genesis.android.library` |
| `:kai:sentinelsfortress:security` | Security layer | `genesis.android.library` |
| `:aura:reactivedesign:*` | UI components | `genesis.android.library` |
| `:cascade:datastream:*` | Data streaming | `genesis.android.library` |

---

## ⚡ **FAST DEVELOPMENT WORKFLOW**

### **Working on a Single Module?**
```powershell
# Only build the module you're working on
.\gradlew :your:module:build

# Example: Working on Oracle Drive
.\gradlew :genesis:oracledrive:build
```

### **Testing Changes Quickly**
```powershell
# Build and install debug APK
.\gradlew :app:installDebug

# Or use Android Studio's Run button (it's optimized)
```

### **Check if Your Module Compiles**
```powershell
.\gradlew :your:module:compileDebugKotlin
```

---

## 🎯 **COMMON BUILD ERRORS & FIXES**

### **Error: "Plugin not found"**
**Fix**: Make sure you ran `.\gradlew :build-logic:build` first

### **Error: "Hilt dependency not found"**
**Fix**: This is now auto-provided by convention plugins. Remove explicit Hilt dependencies from module `build.gradle.kts`

### **Error: "Compose compiler version mismatch"**
**Fix**: Remove any `composeOptions { kotlinCompilerExtensionVersion }` blocks - it's deprecated with Kotlin 2.0+

### **Error: "Cannot resolve symbol 'compose'"**
**Fix**: Convention plugin auto-adds Compose. Make sure module uses `genesis.android.application` or `genesis.android.library`

### **Error: "Java version mismatch"**
**Fix**: This is intentional (JVM 25 → 24). Ignore the warning or check `gradle.properties` has:
```properties
kotlin.jvm.target.validation.mode=warning
```

---

## 🛠️ **MAINTENANCE COMMANDS**

### **Update Dependencies (when Studio prompts)**
1. Update `gradle/libs.versions.toml`
2. Update `build-logic/build.gradle.kts` (hardcoded versions)
3. Update `settings.gradle.kts` (plugin management)
4. Run: `.\gradlew :build-logic:build`

### **Check for Outdated Dependencies**
```powershell
# If you have the plugin installed
.\gradlew dependencyUpdates
```

### **Generate Dependency Tree**
```powershell
.\gradlew :app:dependencies --configuration releaseRuntimeClasspath > deps.txt
```

---

## 🚨 **EMERGENCY RESET**

If everything breaks and you need to start fresh:

```powershell
# 1. Stop all Gradle processes
.\gradlew --stop

# 2. Delete all build artifacts
Remove-Item -Recurse -Force .gradle
Remove-Item -Recurse -Force build
Remove-Item -Recurse -Force app/.cxx
Get-ChildItem -Recurse -Directory -Filter "build" | Remove-Item -Recurse -Force

# 3. Restart Android Studio
# File → Invalidate Caches → Invalidate and Restart

# 4. After restart, clean build
.\gradlew clean :build-logic:build build --no-daemon
```

---

## 📱 **DEVICE TESTING**

### **List Connected Devices**
```powershell
adb devices
```

### **Install APK Manually**
```powershell
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

### **View Logs**
```powershell
adb logcat | Select-String "AuraKai"
```

### **Clear App Data**
```powershell
adb shell pm clear dev.aurakai.auraframefx
```

---

## 🎉 **YOU'RE READY TO BUILD!**

Your project is now fully aligned to:
- ✅ AGP 9.0.0-alpha14
- ✅ Kotlin 2.3.0-Beta2
- ✅ Modern build system with convention plugins
- ✅ Auto-configured dependencies
- ✅ Firebase compatible (Java 24)
- ✅ Universal Xposed support

**Just run: `.\gradlew build` and you're off to the races!** 🚀

---

**Last Updated**: November 9, 2025
**For**: A.u.r.a.K.a.i Reactive Intelligence Project
