# Genesis Python Backend Integration Test Plan

## Objective
Verify that the Android `GenesisBridgeService` can successfully start, bind to, and communicate with the Python `genesis_connector.py` backend process.

## Prerequisites
1.  **Rooted Device / Termux Environment:** The device must have `python3` installed and available in the system PATH.
2.  **Dependencies:** The Python environment must have the necessary packages installed (as listed in `assets/ai_backend/requirements.txt`).

## Verification Steps

### 1. Build and Install
- [x] Build the project successfully (Completed).
- [ ] Install the APK on the target device.

### 2. Log Monitoring (Logcat)
Monitor Logcat with the tag `GenesisBridge` and `PythonManager`.

```bash
adb logcat -s GenesisBridge PythonManager
```

### 3. Expected Startup Sequence
1.  **Initialization:** `GenesisBridgeService` starts and attempts to bind to `GenesisBackendService`.
    *   *Log:* `GenesisBridge: Initializing Genesis Trinity system...`
2.  **Service Binding:** `GenesisBackendService` starts.
    *   *Log:* `GenesisBridge: Connected to GenesisBackendService`
3.  **Python Process Start:** `PythonProcessManager` checks for `python3` and starts the script.
    *   *Log:* `PythonManager: Found python3 at: /path/to/python3` (or Error if missing)
    *   *Log:* `PythonBackend: Genesis Ready` (Output from Python script)
4.  **Ping Test:** `GenesisBridgeService` sends a ping.
    *   *Log:* `GenesisBridge: Genesis Trinity system online! 🎯⚔️🧠`

### 4. Troubleshooting
*   **"CRITICAL: 'python3' executable not found":**
    *   **Fix:** Ensure `python3` is installed. If using Termux, run `pkg install python`. You may need to symlink it to `/system/bin` or adjust the PATH for the app (requires root).
*   **"Genesis communication error":**
    *   **Fix:** Check if the Python script crashed. Look for python stack traces in the logs.

## Manual Trigger (Optional)
If the system doesn't auto-initialize, trigger it via the UI (if available) or by calling `GenesisBridgeService.initialize()` from a debug activity.
