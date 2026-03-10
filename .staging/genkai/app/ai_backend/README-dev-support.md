Developer README — Genesis backend & local support testing

This document explains how to run the Genesis Flask development server and how to test the Android Live Support flow locally from the emulator.

Prereqs
- Python 3.8+ (with pip)
- Android emulator (AVD) or device connected to adb
- Java + Android SDK for building the app

Start the backend (PowerShell)
```powershell
cd app\ai_backend
python -m venv venv
.\venv\Scripts\Activate.ps1
pip install -r requirements.txt
# start the dev server
python genesis_api.py
```

Health & quick chat check (PowerShell)
```powershell
# health
Invoke-RestMethod -Uri http://localhost:5000/health -Method GET

# chat request
Invoke-RestMethod -Uri http://localhost:5000/genesis/chat -Method POST -Body (@{ message = "Hello"; user_id = "dev_user" } | ConvertTo-Json) -ContentType "application/json"
```

Android app notes
- The Retrofit base URL defaults to `http://10.0.2.2:5000` so an Android emulator can reach the host `localhost:5000`.
- The OkHttp interceptor attaches `Authorization: Bearer <key>` if a key is available from one of:
  - Android manifest meta-data `VERTEX_API_KEY`
  - Resource string `vertex_api_key` in `res/values/strings.xml`
  - `BuildConfig.VERTEX_API_KEY` (Gradle buildConfigField)
  - Environment variable `VERTEX_API_KEY`
  - System property `VERTEX_API_KEY`

Build & install app (PowerShell)
```powershell
# from repo root
.\gradlew assembleDebug
.\gradlew installDebug
```

Dev tips
- Use Android Studio App Inspection to view `support_db` and `support_messages` table to confirm messages and statuses.
- If you want to log requests/responses during dev, ensure BuildConfig.DEBUG is true (default for debug builds); the logging interceptor is enabled only in debug mode.

Integration test (Python)
- `test_integration.py` in this folder is a minimal test that calls `/genesis/chat` and checks for a JSON response containing `response` or `message`.
- To run:
```powershell
cd app\ai_backend
.\venv\Scripts\Activate.ps1
pip install -r requirements.txt  # if not installed
pytest -q test_integration.py
```

If anything fails, copy the server stdout/stderr and paste here — I will help fix it.

