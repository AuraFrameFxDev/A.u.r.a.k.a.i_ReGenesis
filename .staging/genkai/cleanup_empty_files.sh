#!/bin/bash 
set -euo pipefail 
 
echo "=== SAFELY DELETING EMPTY FILES ===" 
 
# Array of empty files to delete 
EMPTY_FILES=( 
  "app/src/debug/java/dev/aurakai/auraframefx/core/initialization/TimberInitializer.kt" 
  "app/src/debug/java/dev/aurakai/auraframefx/core/initialization/TimberInitializerImpl.kt" 
  "app/src/debug/java/dev/aurakai/auraframefx/di/TimberModule.kt" 
  "app/src/main/java/dev/aurakai/auraframefx/aura/AuraController.kt" 
  "app/src/main/java/dev/aurakai/auraframefx/aura/ui/OracleDriveControlViewModel.kt" 
  "app/src/main/java/dev/aurakai/auraframefx/config/VertexAIConfig.kt" 
  "app/src/main/java/dev/aurakai/auraframefx/di/AiServiceModuleImpl.kt" 
  "app/src/main/java/dev/aurakai/auraframefx/di/DefaultErrorHandler.kt" 
  "app/src/main/java/dev/aurakai/auraframefx/di/ErrorHandler.kt" 
  "app/src/main/java/dev/aurakai/auraframefx/di/ErrorHandlerAlias.kt" 
  "app/src/main/java/dev/aurakai/auraframefx/genesis/storage/SecureStorage.kt" 
  "app/src/main/java/dev/aurakai/auraframefx/kai/system/SystemOverlayManagerImpl.kt" 
  "app/src/main/java/dev/aurakai/auraframefx/network/qualifiers/BaseUrl.kt" 
  "app/src/main/java/dev/aurakai/auraframefx/oracledrive/OracleDriveApi.kt" 
  "app/src/main/java/dev/aurakai/auraframefx/oracledrive/utils/EncryptionManager.kt" 
  "app/src/main/java/dev/aurakai/auraframefx/oracledrive/viewmodel/OracleDriveControlViewModel.kt" 
  "app/src/main/java/dev/aurakai/auraframefx/services/security/SecurityModule.kt" 
  "app/src/main/java/dev/aurakai/auraframefx/xposed/lockscreen/LockScreenAnimation.kt" 
  "app/src/test/kotlin/test/AndroidLibraryConventionPluginTest.kt" 
  "app/src/main/aidl/android/content/ContextImpl.java" 
) 
 
DELETED=0 
SKIPPED=0 
 
for file in "${EMPTY_FILES[@]}"; do 
  if [ -f "$file" ]; then 
    SIZE=$(wc -c < "$file") 
    if [ "$SIZE" -eq 0 ]; then 
      echo "✅ Deleting: $file" 
      git rm "$file" 
      DELETED=$((DELETED + 1)) 
    else 
      echo "⚠️  Skipping (not empty): $file ($SIZE bytes)" 
      SKIPPED=$((SKIPPED + 1)) 
    fi 
  else 
    echo "⚠️  Skipping (not found): $file" 
    SKIPPED=$((SKIPPED + 1)) 
  fi 
done 
 
echo "" 
echo "=== SUMMARY ===" 
echo "Deleted: $DELETED files" 
echo "Skipped: $SKIPPED files" 
echo "" 
echo "Next steps:" 
echo "  git commit -m 'chore: remove 20 empty source files after refactoring'" 
echo "  git push"