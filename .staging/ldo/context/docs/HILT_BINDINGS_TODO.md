# Missing Hilt Bindings - Action Plan

**Generated:** December 5, 2025
**Status:** KSP Processing Errors - Missing Dependency Injection Configuration

---

## Summary

The build system is configured correctly (Kotlin 2.3.0-RC2, Gradle 9.4, AGP 9.0-beta02), but the application has **14+ missing Hilt dependency bindings** that prevent KSP from processing the DI graph.

---

## Missing Types by Category

### 1. Room Generated DAOs
**Status:** ❌ Room not generating

| Type | Used In | Issue |
|------|---------|-------|
| `AgentMemoryDao` | `DatabaseModule.provideAgentMemoryDao()` | Room should generate from `AppDatabase` but isn't |
| `TaskHistoryDao` | `DatabaseModule.provideTaskHistoryDao()` | Room should generate from `AppDatabase` but isn't |

**Root Cause:** There may be Kotlin compilation errors preventing Room annotation processor from running.

**Fix:**
1. Check for Kotlin compilation errors: `./gradlew :app:compileDebugKotlin --console=verbose`
2. Ensure `AppDatabase.kt` compiles without errors
3. Verify all `@Entity` classes compile successfully

---

### 2. Third-Party Library Types
**Status:** ❌ Missing @Provides methods

| Type | Used In | Package |
|------|---------|---------|
| `YukiHookModulePrefs` | `HomeScreenTransitionManager` | `com.highcapable.yukihookapi.hook.xposed.prefs` |
| `VertexAIClient` | `AuraAgent`, `KaiAgent` | `com.google.cloud.vertexai` (likely) |

**Fix:** Add to Hilt module:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object ThirdPartyModule {

    @Provides
    @Singleton
    fun provideYukiHookPrefs(): YukiHookModulePrefs {
        return YukiHookModulePrefs.instance
    }

    @Provides
    @Singleton
    fun provideVertexAIClient(): VertexAIClient {
        return VertexAI.builder()
            .setProjectId("your-project-id")
            .build()
            .client
    }
}
```

---

### 3. Cloud/Storage Services
**Status:** ❌ Missing implementations or @Binds

| Type | Used In | Location |
|------|---------|----------|
| `CloudStatusMonitor` | `DiagnosticsViewModel`, `KaiAIService`, `AuraAIServiceImpl`, `ClaudeAIService` | Likely in `app/` module |
| `CloudStorageProvider` | `OracleDriveManager` | Likely interface |
| `OracleDriveService` | `OracleDriveIntegration` | `app/src/.../oracledrive/OracleDriveService.kt` |
| `OracleDriveApi` | `OracleDriveManager`, `OracleDriveServiceImpl` | `app/src/.../oracledrive/genesis/` |

**Fix Options:**

**Option A:** Create implementations if they don't exist
**Option B:** If they're interfaces, add @Binds in a module:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class OracleDriveBindings {

    @Binds
    abstract fun bindOracleDriveService(
        impl: OracleDriveServiceImpl
    ): OracleDriveService

    @Binds
    abstract fun bindOracleDriveApi(
        impl: OracleDriveApiImpl  // Create this class
    ): OracleDriveApi

    @Binds
    abstract fun bindCloudStorageProvider(
        impl: CloudStorageProviderImpl  // Create this class
    ): CloudStorageProvider
}
```

---

### 4. AI Service Interfaces
**Status:** ❌ Missing @Binds binding implementations to interfaces

| Type | Used In | Location |
|------|---------|----------|
| `AuraAIService` | `TrinityCoordinatorService` | Interface at `app/src/.../ai/services/AuraAIService.kt` |
| `ClaudeAIService` | `ConferenceRoomViewModel` | `app/src/.../oracledrive/genesis/ai/ClaudeAIService.kt` |

**Issue:** These are likely interfaces with implementations (`AuraAIServiceImpl`, etc) but missing Hilt bindings.

**Fix:**

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class AIServiceModule {

    @Binds
    abstract fun bindAuraAIService(
        impl: AuraAIServiceImpl
    ): AuraAIService

    // ClaudeAIService is already a class with @Inject constructor
    // So it should work - verify it's not an interface
}
```

---

### 5. Utility/Helper Classes
**Status:** ❌ Missing or not injectable

| Type | Used In | Notes |
|------|---------|-------|
| `GreetingProvider` | `HiltTestActivity.greetingProvider` | Test-only class - may not exist |

**Fix:** Either:
1. Remove the `@Inject` field from test activity
2. Create `GreetingProvider` class with `@Inject` constructor
3. Add `@Provides` method in test module

---

## Recommended Action Plan

### Phase 1: Quick Win - Comment Out Problematic Injections (30 min)

Temporarily comment out `@Inject` constructors/fields for missing dependencies to get build working:

```kotlin
// Before
@Inject
constructor(
    cloudMonitor: CloudStatusMonitor,  // Missing!
    dataManager: OfflineDataManager
)

// After - temporary fix
constructor(
    // cloudMonitor: CloudStatusMonitor,  // TODO: Add Hilt binding
    dataManager: OfflineDataManager
) {
    // val cloudMonitor = CloudStatusMonitorStub()  // Temporary
}
```

Files to modify:
- `HomeScreenTransitionManager.kt`
- `AuraAgent.kt`
- `KaiAgent.kt`
- `DiagnosticsViewModel.kt`
- `KaiAIService.kt`
- `AuraAIServiceImpl.kt`
- `ClaudeAIService.kt`
- `OracleDriveIntegration.kt`
- `OracleDriveApi.kt`
- `OracleDriveServiceImpl.kt`
- `TrinityCoordinatorService.kt`
- `HiltTestActivity.kt` (test file)

### Phase 2: Fix Room Generation (1 hour)

1. Run verbose Kotlin compilation:
   ```bash
   ./gradlew :app:compileDebugKotlin --console=verbose 2>&1 | tee kotlin-compile.log
   ```

2. Look for errors in:
   - `AgentMemoryEntity.kt`
   - `TaskHistoryEntity.kt`
   - `AgentMemoryDao.kt`
   - `TaskHistoryDao.kt`
   - `AppDatabase.kt`

3. Fix any Kotlin compilation errors found

4. Verify Room generates:
   ```bash
   ./gradlew :app:kspDebugKotlin --rerun-tasks
   ```

5. Check generated files in:
   ```
   app/build/generated/ksp/debug/kotlin/
   ```

### Phase 3: Create Missing Hilt Modules (2-3 hours)

Create proper Hilt modules with @Provides/@Binds for all missing types.

#### 3.1 Create `ThirdPartyModule.kt`
```kotlin
package dev.aurakai.auraframefx.di

import com.highcapable.yukihookapi.hook.xposed.prefs.YukiHookModulePrefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ThirdPartyModule {

    @Provides
    @Singleton
    fun provideYukiHookPrefs(): YukiHookModulePrefs =
        YukiHookModulePrefs.instance
}
```

#### 3.2 Create `CloudModule.kt`
```kotlin
package dev.aurakai.auraframefx.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CloudModule {

    @Provides
    @Singleton
    fun provideCloudStatusMonitor(): CloudStatusMonitor {
        return CloudStatusMonitorImpl()  // Create this class
    }

    @Provides
    @Singleton
    fun provideCloudStorageProvider(): CloudStorageProvider {
        return CloudStorageProviderImpl()  // Create this class
    }
}
```

#### 3.3 Create missing implementations

Create stub implementations for:
- `CloudStatusMonitorImpl`
- `CloudStorageProviderImpl`
- `OracleDriveApiImpl` (if `OracleDriveApi` is an interface)
- `VertexAIClientWrapper` (for Vertex AI)

### Phase 4: Verify Build (30 min)

```bash
./gradlew clean
./gradlew :app:kspDebugKotlin
./gradlew :app:assembleDebug
```

---

## Quick Reference: Common Hilt Patterns

### Pattern 1: Interface + Implementation

```kotlin
// Interface
interface MyService {
    fun doSomething()
}

// Implementation
@Singleton
class MyServiceImpl @Inject constructor(
    private val dependency: SomethingElse
) : MyService {
    override fun doSomething() { }
}

// Module
@Module
@InstallIn(SingletonComponent::class)
abstract class MyServiceModule {
    @Binds
    abstract fun bindMyService(impl: MyServiceImpl): MyService
}
```

### Pattern 2: Third-Party Library

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object ExternalLibModule {

    @Provides
    @Singleton
    fun provideExternalLib(): ExternalLibClass {
        return ExternalLibClass.Builder()
            .configure()
            .build()
    }
}
```

### Pattern 3: Generated Code (Room, Retrofit)

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "db")
            .build()
    }

    @Provides
    fun provideDao(database: AppDatabase): MyDao {
        return database.myDao()  // Room generates this
    }
}
```

---

## Files to Check/Create

### Existing Files to Verify
- [ ] `app/src/main/java/dev/aurakai/auraframefx/di/DatabaseModule.kt` - Check DAOs return type
- [ ] `app/src/main/java/dev/aurakai/auraframefx/data/room/AppDatabase.kt` - Verify compiles
- [ ] All `@Entity` classes compile without errors

### New Files to Create
- [ ] `app/src/main/java/dev/aurakai/auraframefx/di/ThirdPartyModule.kt`
- [ ] `app/src/main/java/dev/aurakai/auraframefx/di/CloudModule.kt`
- [ ] `app/src/main/java/dev/aurakai/auraframefx/di/AIServiceModule.kt`
- [ ] `app/src/main/java/dev/aurakai/auraframefx/cloud/CloudStatusMonitorImpl.kt` (if doesn't exist)
- [ ] `app/src/main/java/dev/aurakai/auraframefx/cloud/CloudStorageProviderImpl.kt` (if doesn't exist)

---

## Next Steps

**Immediate (Choose One):**

**Option A - Quick Fix (Get building ASAP):**
1. Comment out all @Inject constructors with missing deps
2. Add stub implementations inline
3. Get build working
4. Gradually uncomment and fix properly

**Option B - Proper Fix (Do it right):**
1. Run `./gradlew :app:compileDebugKotlin --console=verbose`
2. Fix all Kotlin compilation errors
3. Create missing Hilt modules
4. Create missing implementation classes
5. Build and test

**Recommended:** Start with Option A to unblock,then migrate to Option B.

---

## Useful Commands

```bash
# Check Kotlin compilation only
./gradlew :app:compileDebugKotlin --console=verbose

# Check KSP generation only
./gradlew :app:kspDebugKotlin --rerun-tasks

# See generated files
ls app/build/generated/ksp/debug/kotlin/

# Full clean build
./gradlew clean build --no-build-cache

# Run with stack traces
./gradlew :app:kspDebugKotlin --stacktrace
```

---

## Status Tracking

- [ ] Phase 1: Comment out problematic injections
- [ ] Phase 2: Fix Room DAO generation
- [ ] Phase 3: Create Hilt modules
- [ ] Phase 4: Verify build succeeds
- [ ] Phase 5: Uncomment and wire properly

**Estimated Time:** 4-6 hours total

---

**Last Updated:** December 5, 2025
**Build Status:** ❌ Failing - 14+ missing Hilt bindings
**Next Action:** Choose Option A or B above
