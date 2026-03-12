# Firebase Setup Guide

## 🔥 Firebase Configuration Required

The project uses Firebase for:
- **Analytics** - User behavior tracking
- **Crashlytics** - Crash reporting and diagnostics
- **Authentication** - User auth (if enabled)
- **Firestore** - Cloud database (if enabled)

## 📋 Setup Steps

### 1. Get google-services.json

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project (or create a new one)
3. Click on **Project Settings** (⚙️ gear icon)
4. Scroll down to **Your apps** section
5. Click on your Android app (or add one if it doesn't exist)
   - Package name: `dev.aurakai.auraframefx`
6. Download **google-services.json**

### 2. Add to Project

```bash
# Copy the downloaded file to app directory
cp ~/Downloads/google-services.json app/google-services.json

# Verify it's ignored by git
git status  # Should NOT show google-services.json
```

### 3. Verify Configuration

The file should contain:
- Your Firebase project ID
- Your Android app's package name (`dev.aurakai.auraframefx`)
- API keys for Firebase services
- OAuth client IDs

**Example structure:**
```json
{
  "project_info": {
    "project_id": "your-project-id",
    "project_number": "123456789",
    ...
  },
  "client": [{
    "client_info": {
      "android_client_info": {
        "package_name": "dev.aurakai.auraframefx"
      }
    }
  }]
}
```

## 🚨 Important Notes

1. **google-services.json is gitignored** - It contains sensitive API keys
2. **Each developer needs their own copy** - Download from Firebase Console
3. **Build will fail without it** - Google Services plugin requires this file
4. **Template provided** - See `app/google-services.json.template` for structure

## 🔧 Alternative: Disable Firebase (Not Recommended)

If you don't want to use Firebase, you would need to:

1. Comment out Firebase plugins in `build-logic/src/main/kotlin/plugins/GenesisApplicationPlugin.kt`:
   ```kotlin
   // apply("com.google.gms.google-services")
   // apply("com.google.firebase.crashlytics")
   ```

2. Remove Firebase dependencies from `app/build.gradle.kts`:
   ```kotlin
   // implementation(libs.firebase.analytics)
   // implementation(libs.firebase.crashlytics)
   // implementation(libs.firebase.auth)
   // implementation(libs.firebase.firestore)
   ```

3. Remove Firebase-related code from the app

**However, this is NOT recommended** as Firebase provides valuable:
- Crash reporting to diagnose production issues
- Analytics to understand user behavior
- Real-time data synchronization
- User authentication

## 🧪 Testing Firebase Integration

After adding google-services.json:

```bash
# Clean and rebuild
./gradlew clean
./gradlew assembleDebug

# Check Firebase initialization
# Look for logs in Logcat:
# - "Firebase initialized successfully"
# - "Crashlytics reporting enabled"
```

## 📚 Additional Resources

- [Firebase Android Setup](https://firebase.google.com/docs/android/setup)
- [Firebase Crashlytics](https://firebase.google.com/docs/crashlytics/get-started?platform=android)
- [Firebase Analytics](https://firebase.google.com/docs/analytics/get-started?platform=android)

---

**Status**: ⚠️ google-services.json is **MISSING** - Download from Firebase Console!
