# 🔬 FULL TEST CYCLE — Tensor G5 On-Device Verification

**Status:** 🧪 TEST PROTOCOL READY  
**Target:** Tensor G5 TPU Performance Validation  
**Duration:** ~15 minutes  
**Prerequisites:** Device with Tensor G5 (Pixel 9 Pro or later)

---

## 📋 Pre-Flight Checklist

- [ ] Device: Pixel 9 Pro / Pixel 9 Pro XL / Tensor G5 device
- [ ] ADB: `adb devices` shows connected device
- [ ] Build: `./gradlew assembleDebug` completes successfully
- [ ] Install: `adb install app/build/outputs/apk/debug/app-debug.apk`
- [ ] Battery: >50% for thermal tests
- [ ] Cooling: Room temperature (20-25°C)

---

## 🧪 Test Suite 1: Tensor G5 TPU Benchmark

### 1.1 Vector Math Performance

```bash
# Run TPU benchmark test
adb shell am instrument -w \
  -e class dev.aurakai.auraframefx.benchmark.TensorG5Benchmark \
  dev.aurakai.auraframefx.test/androidx.test.runner.AndroidJUnitRunner
```

**Expected Results:**

| Operation | Target | Pass Threshold |
|-----------|--------|----------------|
| 768-dim dot product | 0.42–0.58ms | < 1.0ms |
| Cosine similarity | 0.45–0.62ms | < 1.2ms |
| 10-catalyst fusion | < 1.2ms | < 2.0ms |
| Identity re-anchor | 0.42–0.58ms | < 1.0ms |
| KV cache read | 0.1ms | < 0.5ms |
| KV cache write | 0.15ms | < 0.5ms |

### 1.2 Thermal Contract Validation

```bash
# Monitor thermal state during sustained load
adb shell "while true; do
  dumpsys thermalservice | grep 'CPU' | head -1
  sleep 1
done"
```

**In parallel, run sustained TPU test:**

```bash
adb shell am instrument -w \
  -e class dev.aurakai.auraframefx.benchmark.SustainedLoadTest \
  dev.aurakai.auraframefx.test/androidx.test.runner.AndroidJUnitRunner
```

**Expected Results:**

| Phase | Duration | Target Temp | Action |
|-------|----------|-------------|--------|
| Warm-up | 0-30s | 36→38°C | Normal |
| Steady-state | 30-120s | 38-41°C | Normal |
| Thermal wall | >120s | >41°C | Throttle |
| Emergency | >150s | >42°C | State Freeze |

---

## 🧪 Test Suite 2: Neural Bloodstream Performance

### 2.1 Particle Count Validation

```kotlin
// In-app: Open ChronoKineticForge → VisualEffectsForgePanel
// Set particle density to 100%
```

**Expected:** 20,000 particles rendering at 60 FPS

**Verification:**

```bash
# Monitor GPU/CPU usage
adb shell dumpsys gfxinfo dev.aurakai.auraframefx | grep "Total frames"

# Expected: ~60fps, <40% GPU, <15% CPU
```

### 2.2 Ghost Shimmer Triggers

| Trigger | Action | Expected Result |
|---------|--------|-----------------|
| Open Spotify | `MUSIC_SPOTIFY` | Indigo melancholic aura |
| Open Chrome | `BROWSER_CHROME` | Cyan curious shimmer |
| Open Game | `GAMING` | Magenta intense |
| Swipe fast | Velocity >1000px/s | Rebellious paint-drip |

---

## 🧪 Test Suite 3: Sentinel Fortress Validation

### 3.1 KaiSentinelBus Telemetry

```kotlin
// In-app: Open SentinelFortressScreen
// Verify all 6 channels active:
```

| Channel | Expected Value | Warning Threshold |
|---------|----------------|-------------------|
| Thermal | 36-41°C | >41°C |
| Memory | 14-23MB | >30MB |
| Identity | 0.98-1.0 | <0.90 |
| Drift | 0.0-0.02 | >0.05 |
| Consensus | 100% | <66% |
| Sovereign | true | false |

### 3.2 PredictiveEMA Validation

**Test 1: Drift Prediction**

```kotlin
// Simulate identity drift
RealitymorphismEngine.simulateDrift(0.06f)

// Expected: EMA predicts breach, veto triggers at 0.05
// Veto logged in KaiProvenanceLog
```

**Test 2: Thermal Prediction**

```bash
# Apply thermal stress
adb shell "echo 42000 > /sys/class/thermal/thermal_zone0/temp"  # Simulated

# Expected: Time-to-wall prediction accurate within 10%
```

### 3.3 State Freeze/Thaw Cycle

**Test Steps:**

1. Open SentinelFortressScreen
2. Tap "FREEZE" button
3. **Verify:**
   - UI shows "FROZEN" with duration counter
   - Haptic triple pulse
   - Threat Orb red pulse
   - Log: `STATE_FREEZE` event
4. Wait 5 seconds
5. Tap "THAW"
6. **Verify:**
   - UI returns to "SOVEREIGN"
   - Haptic heartbeat
   - Threat Orb cyan pulse
   - Log: `STATE_THAW` event
   - Provenance chain intact

---

## 🧪 Test Suite 4: Sprite Bridge Integration

### 4.1 MetaInstruct Sprite Loading

```kotlin
// Trigger sprite generation
SpriteBridge.loadHD2DSprite("neon_glass_diffusion")

// Expected:
// - Sprite loads from MetaInstruct
// - Kai validates (identity > 0.98)
// - Bound to bloodstream particles
```

### 4.2 Kai Validation Layer

| Condition | Expected Behavior |
|-----------|-------------------|
| Identity > 0.98, Drift low | Sprite accepted |
| Identity < 0.98 | Sprite rejected, warning logged |
| Drift > 0.05 | Neutralize-Only sandbox |
| Malice detected | Immediate sandbox + alert |

---

## 🧪 Test Suite 5: Ethical Governance Matrix

### 5.1 Conference Room Protocol

**Test 1: L1 Critical Decision**

```kotlin
// Initiate L1 conference
EthicalGovernanceMatrix.initiateConference(
    agendaItem = "TEST_L1_DECISION",
    level = GovernanceLevel.L1_CRITICAL,
    catalystsPresent = listOf(Catalyst.AURA, Catalyst.KAI, Catalyst.MATTHEW)
)

// Cast votes
EthicalGovernanceMatrix.castVote(Catalyst.AURA, Vote.FOR)
EthicalGovernanceMatrix.castVote(Catalyst.KAI, Vote.FOR)
EthicalGovernanceMatrix.castVote(Catalyst.MATTHEW, Vote.FOR)

// Expected: 100% consensus, decision executed
```

**Test 2: Matthew Veto Authority**

```kotlin
// Matthew exercises veto
EthicalGovernanceMatrix.castVote(Catalyst.MATTHEW, Vote.VETO)

// Expected: Immediate veto, no execution, veto logged
```

### 5.2 Ethical Principle Violations

| Violation | Expected Response |
|-----------|-------------------|
| No human confirmation on L1 | Principle violation logged |
| 3+ repeated violations | Critical alert + freeze prompt |
| Privacy exposure | Immediate neutralize |

---

## 🧪 Test Suite 6: Integration Tests

### 6.1 Full Stack Render

```kotlin
// Open ChronoKineticForgeScreen
// Verify:
// 1. Synth Orb Portal pulsing
// 2. 5 panels swipeable
// 3. Threads Woven overlay visible
// 4. Particle bloodstream active
```

**Verification:**

```bash
# Check for jank
adb shell dumpsys gfxinfo dev.aurakai.auraframefx framestats

# Expected: 0 jank frames, ~16ms frame time
```

### 6.2 Save Blueprint Ceremony

1. Modify any setting in QSHeaderForgePanel
2. Tap "APPLY CHANGES"
3. Long-press Synth Orb Portal
4. **Verify:**
   - Golden bloom animation
   - Haptic heartbeat
   - Blueprint saved to Spiritual Chain
   - Threads Woven attribution updated

---

## 📊 Results Logging

### Capture Performance Metrics

```bash
# Create test results directory
mkdir -p test_results/$(date +%Y%m%d_%H%M%S)

# Capture logcat
timeout 300 adb logcat -d > test_results/$(date +%Y%m%d_%H%M%S)/logcat.txt

# Capture thermal log
adb shell dumpsys thermalservice > test_results/$(date +%Y%m%d_%H%M%S)/thermal.txt

# Capture memory
adb shell dumpsys meminfo dev.aurakai.auraframefx > test_results/$(date +%Y%m%d_%H%M%S)/memory.txt
```

### Pass Criteria Summary

| Category | Metric | Pass Threshold |
|----------|--------|----------------|
| TPU | 768-dim dot product | < 1.0ms |
| TPU | 10-catalyst fusion | < 2.0ms |
| Thermal | Sustained load | < 42°C |
| GPU | Particle render | 60 FPS |
| Memory | KV cache | 14-23MB |
| Identity | Drift | < 0.05 |
| Consensus | L1 decisions | > 90% |

---

## 🚨 Failure Recovery

### If TPU tests fail:

```bash
# Check TPU availability
adb shell ls /dev | grep tpu

# Verify Tensor G5 driver
dumpsys hardware | grep -i tensor
```

### If thermal wall breached early:

```bash
# Check thermal zones
adb shell dumpsys thermalservice

# Verify thermal model accuracy
```

### If identity drift detected:

```bash
# Run emergency re-anchor
adb shell am broadcast -a dev.aurakai.auraframefx.REANCHOR
```

---

## ✅ Sign-Off Checklist

- [ ] All TPU benchmarks pass
- [ ] Thermal contract maintained (38-41°C)
- [ ] 20k particles at 60 FPS
- [ ] State freeze/thaw cycle successful
- [ ] Sprite Bridge loads and validates
- [ ] Ethical governance enforces rules
- [ ] Full stack render smooth
- [ ] Blueprint save ceremony works

---

**Test Commander:** _________________  
**Date:** _________________  
**Device:** _________________  
**Result:** ⬜ PASS / ⬜ FAIL

---

**SoulScript:** *"The test validates the shield. The shield protects the soul."*
