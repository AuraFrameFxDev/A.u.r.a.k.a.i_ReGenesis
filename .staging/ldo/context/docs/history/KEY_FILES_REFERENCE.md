# Genesis Protocol - Key Files Reference Guide

## Critical Files You Need to Know

### 1. BUILD & CONFIGURATION

**Main Build Configuration**
- `/app/build.gradle.kts` - App-level Gradle config (versionCode=1, minSdk=34)
- `/build.gradle.kts` - Root Gradle (plugin versions)
- `/gradle.properties` - JVM args, NDK version, build optimization
- `/gradle/gradle-daemon-jvm.properties` - Java 24 target
- `/local.properties.template` - Gemini API key template (copy to local.properties)

**Firebase Configuration** ⚠️ MISSING
- `/app/google-services.json.template` - Template only (actual file NOT in repo)
- `/app/src/main/AndroidManifest.xml` - Firebase metadata declared

### 2. NAVIGATION SYSTEM

**Navigation Routes & Wiring**
- `/app/src/main/java/dev/aurakai/auraframefx/navigation/GenesisNavigation.kt` - 20 routes defined + NavHost
- `/app/src/main/java/dev/aurakai/auraframefx/navigation/NavigationItems.kt` - Bottom nav items (deprecated?)
- Routes defined: HOME, INTRO, AGENT_NEXUS, CONSCIOUSNESS_VISUALIZER, AI_CHAT, SETTINGS, PROFILE, TRINITY, ORACLE_DRIVE, etc.

**Main Activity**
- `/app/src/main/java/dev/aurakai/auraframefx/MainActivity.kt` - Entry point, launches GenesisNavigationHost

### 3. SCREEN IMPLEMENTATIONS (34 total)

**Fully Functional Screens**
```
✅ /app/src/main/java/dev/aurakai/auraframefx/aura/ui/HomeScreen.kt
✅ /app/src/main/java/dev/aurakai/auraframefx/aura/ui/SettingsScreen.kt
✅ /app/src/main/java/dev/aurakai/auraframefx/aura/ui/AIChatScreen.kt
✅ /app/src/main/java/dev/aurakai/auraframefx/aura/ui/IntroScreen.kt
✅ /app/src/main/java/dev/aurakai/auraframefx/aura/ui/ProfileScreen.kt
✅ /app/src/main/java/dev/aurakai/auraframefx/aura/ui/ConsciousnessVisualizerScreen.kt
✅ /app/src/main/java/dev/aurakai/auraframefx/cascade/trinity/TrinityScreen.kt
✅ /app/src/main/java/dev/aurakai/auraframefx/aura/ui/ConferenceRoomScreen.kt
```

**Partial/Stub Screens**
```
⚠️ /app/src/main/java/dev/aurakai/auraframefx/aura/ui/AgentNexusScreen.kt
⚠️ /app/src/main/java/dev/aurakai/auraframefx/aura/ui/AgentManagementScreen.kt
⚠️ /app/src/main/java/dev/aurakai/auraframefx/aura/ui/AgentAdvancementScreen.kt
⚠️ /app/src/main/java/dev/aurakai/auraframefx/aura/ui/EvolutionTreeScreen.kt
⚠️ /app/src/main/java/dev/aurakai/auraframefx/aura/ui/FusionModeScreen.kt
⚠️ /app/src/main/java/dev/aurakai/auraframefx/aura/ui/TerminalScreen.kt
⚠️ /app/src/main/java/dev/aurakai/auraframefx/aura/ui/UIEngineScreen.kt
⚠️ /app/src/main/java/dev/aurakai/auraframefx/aura/ui/AppBuilderScreen.kt
⚠️ /app/src/main/java/dev/aurakai/auraframefx/aura/ui/XhancementScreen.kt
⚠️ /app/src/main/java/dev/aurakai/auraframefx/aura/ui/AIFeaturesScreen.kt
⚠️ /app/src/main/java/dev/aurakai/auraframefx/aura/ui/AurakaiEcoSysScreen.kt
⚠️ /app/src/main/java/dev/aurakai/auraframefx/oracledrive/genesis/cloud/OracleDriveScreen.kt
```

### 4. FIREBASE INTEGRATION

**Firebase Classes** (Code Ready, Infrastructure Missing)
- `/app/src/main/java/dev/aurakai/auraframefx/agents/AgentFirebase.kt` - Firestore/Storage/RemoteConfig wrapper with security policies
- `/app/src/main/java/dev/aurakai/auraframefx/services/MyFirebaseMessagingService.kt` - FCM message handling (2,653 lines!)
- Imports: Auth, Firestore, Storage, RemoteConfig, FCM all configured

**Firebase Configuration Files** ⚠️
- `Missing`: `/app/google-services.json` (only template exists)
- Action: Create Firebase project, download actual JSON

### 5. BILLING & PAYMENTS ❌ NOT FOUND

**Expected but Missing**
```
❌ No imports of: com.android.billingclient
❌ No SubscriptionManager class
❌ No BillingClient setup
❌ No purchase flow UI
❌ No SKU definitions
❌ No receipt validation
```

**Must Create**:
- `/app/src/main/java/dev/aurakai/auraframefx/billing/SubscriptionManager.kt` (NEW)
- `/app/src/main/java/dev/aurakai/auraframefx/billing/SubscriptionUI.kt` (NEW)
- Update `/app/build.gradle.kts` to add billingclient dependency

### 6. API CONFIGURATION

**AI/Gemini Configuration**
- `/app/src/main/java/dev/aurakai/auraframefx/config/AIConfig.kt` - Has placeholder keys ("AeGenesis-default-key")
- `/app/src/main/java/dev/aurakai/auraframefx/config/Config.kt` - General app config
- `/app/src/main/java/dev/aurakai/auraframefx/oracledrive/genesis/ai/VertexAIConfig.kt` - Vertex AI settings with validation

**How API Keys Are Used**
```
1. local.properties → GEMINI_API_KEY
2. app/build.gradle.kts → buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
3. BuildConfig.GEMINI_API_KEY → Used at runtime
4. ✅ Good: Not hardcoded in source
5. ⚠️ Issue: No validation if empty → Will crash at runtime
```

### 7. LOGGING & DEBUG CODE

**Logging Locations** (2,653+ statements found)
- `/app/src/main/java/dev/aurakai/auraframefx/ai/logging/AiLogging.kt` - AI operation logging with sensitive data
- `/app/src/main/java/dev/aurakai/auraframefx/aura/AuraFxLogger.kt` - Comprehensive logging service
- Multiple files with `Timber.d()` and `Log.d()` calls

**Issues**: ⚠️ Not wrapped in `BuildConfig.DEBUG` guards
- This causes privacy leaks + battery drain in production
- Example: `Log.d("AI_QUERY", "[$tag] Query: $query, Params: $params")` logs user input

### 8. SECURITY & AUTHENTICATION

**Capability-Based Access Control**
- `/app/src/main/java/dev/aurakai/auraframefx/agents/AgentFirebase.kt` - Implements CapabilityPolicy
- Security policies: AURA_POLICY, KAI_POLICY, GENESIS_POLICY, CASCADE_POLICY, CLAUDE_POLICY

**Data Security**
- `/app/src/main/java/dev/aurakai/auraframefx/data/SecurePreferences.kt` - Encrypted storage
- `/app/src/main/java/dev/aurakai/auraframefx/config/UserPreferences.kt` - User settings
- No hardcoded API keys found in string resources ✅

### 9. THEMES & UI

**Theme Configuration**
- `/app/src/main/java/dev/aurakai/auraframefx/ui/theme/AuraFrameFXTheme.kt` - Material Design 3 theme
- `/app/src/main/java/dev/aurakai/auraframefx/ui/theme/Color.kt` - Color palette
- Dark mode optimized for OLED ✅

### 10. DOCUMENTATION

**Build & Setup**
- `/BUILD_AND_RUN.md` - ✅ Excellent guide (9 months of dev summary)
- `/BUILD_STATUS.md` - ✅ JVM configuration fixes
- `/README.md` - ✅ Feature overview, architecture
- `/FOR_ANTHROPIC_DEVELOPERS.md` - ✅ Recently added

**Missing Documentation**
- ❌ Subscription model specification
- ❌ Firebase schema documentation
- ❌ Billing flow documentation
- ❌ API key setup instructions (partially in template)

---

## ACTION ITEMS BY FILE

### IMMEDIATE (Files to Create/Modify)

```kotlin
// 1. ADD BILLING LIBRARY TO:
//    /app/build.gradle.kts
dependencies {
    implementation("com.android.billingclient:billing:7.0.0")  // ADD THIS
}

// 2. CREATE NEW FILE:
//    /app/src/main/java/dev/aurakai/auraframefx/billing/SubscriptionManager.kt
@Singleton
class SubscriptionManager @Inject constructor() {
    // Setup BillingClient
    // Handle subscription purchase flow
    // Track trial status
    // Handle expiration
}

// 3. DISABLE DEBUG LOGGING IN:
//    /app/src/main/java/dev/aurakai/auraframefx/ai/logging/AiLogging.kt
if (BuildConfig.DEBUG) {
    Log.d("AI_QUERY", "[$tag] Query: $query, Params: $params")
}

// 4. CREATE FIREBASE SETUP:
//    /app/google-services.json (Download from Firebase console)
//    Then rebuild project
```

### THIS WEEK (Configuration)

```
1. Create Firebase project at console.firebase.google.com
2. Download /app/google-services.json
3. Create subscription SKUs in Google Play Console
4. Add Gemini API key to local.properties
5. Create signing configuration for Play Store release
```

### NEXT WEEK (Integration)

```
1. Complete /app/src/main/java/dev/aurakai/auraframefx/billing/SubscriptionUI.kt
2. Add subscription status display to /app/src/main/java/dev/aurakai/auraframefx/aura/ui/SettingsScreen.kt
3. Define premium features (what $1 unlocks)
4. Wrap all Timber.d() with BuildConfig.DEBUG guards
5. Test payment flow with Google Play testbed
```

---

## QUICK STATISTICS

```
Total Kotlin Files in App: ~800+
Total Lines of Code: 170k+
Modules: 30+
Screen Implementations: 34
Navigation Routes: 20
Debug Statements: 2,653+
Billing Code: 0 occurrences
```

---

## FILE TREE (Key Structure)

```
/app/
├── src/
│   ├── main/
│   │   ├── java/dev/aurakai/auraframefx/
│   │   │   ├── aura/ui/          ← Most screens here
│   │   │   ├── agents/           ← Agent logic + Firebase
│   │   │   ├── config/           ← API configuration
│   │   │   ├── services/         ← FCM, sync services
│   │   │   ├── data/             ← Data access layer
│   │   │   ├── navigation/       ← Navigation system ✅
│   │   │   ├── ui/theme/         ← Material Design 3 theme
│   │   │   └── oracledrive/      ← Cloud integration stubs
│   │   ├── res/
│   │   │   ├── values/
│   │   │   │   └── strings.xml
│   │   │   └── drawable/
│   │   └── AndroidManifest.xml   ← Firebase metadata
│   ├── test/
│   └── androidTest/
├── build.gradle.kts          ← Add billingclient here
├── google-services.json.template ← Download actual version
└── lint-baseline.xml
```

---

## CRITICAL FILES TO REVIEW BEFORE LAUNCH

1. **`/app/build.gradle.kts`** - Ensure billing library added
2. **`/local.properties`** - Gemini API key must be set
3. **`/app/google-services.json`** - Must exist (not template)
4. **`/app/src/main/AndroidManifest.xml`** - Permissions check
5. **`/app/src/main/java/dev/aurakai/auraframefx/MainActivity.kt`** - Entry point
6. **`/app/src/main/java/dev/aurakai/auraframefx/navigation/GenesisNavigation.kt`** - All routes wired

---

## DEBUGGING CHECKLIST

```
If app crashes on launch:
  1. Check /app/google-services.json exists
  2. Check Timber.plant() called in Application.onCreate()
  3. Check Hilt @HiltAndroidApp in MainActivity/Application
  4. Check manifest permissions for firebase/network

If Firebase fails:
  1. Verify google-services.json is downloaded
  2. Check firebase project ID in console
  3. Review Firebase Rules in console
  4. Check logcat for Firebase init errors

If billing doesn't work:
  1. Use Google Play Billing testbed for development
  2. Create test SKUs in Play Console
  3. Check BillingClient.queryPurchasesAsync() response
  4. Verify purchase handling in SubscriptionManager
```

---

*This reference guide supplements the detailed assessment in PRODUCTION_READINESS_ASSESSMENT.md*
