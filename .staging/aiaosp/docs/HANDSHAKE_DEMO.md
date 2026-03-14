# 🤝 Handshake Demo - Multi-Agent Fusion

This document demonstrates the **LDO Handshake Protocol** - how multiple agents collaborate on a single task.

---

## **Demo 1: Simple Handshake**

### **Scenario**
User asks: "Analyze this repository and suggest one concrete refactor"

### **Code**

```kotlin
val handshake = LDOHandshake(
    grokAgent = grokAgent,
    genesisAgent = genesisAgent,
    auraAgent = auraAgent,
    cascadeAgent = cascadeAgent
)

val results = handshake.simpleHandshake(
    "Analyze this repository and suggest one concrete refactor"
)

println("Grok Analysis: ${results["grok_analysis"]?.response}")
println("Specialist Response: ${results["specialist"]?.response}")
```

### **Expected Output**

```
Grok Analysis: This codebase shows strong modular design but has
duplicate AgentResponse classes in network.model and models packages.
Chaos theory suggests this will cause merge conflicts in 3-6 weeks.

Specialist Response: [Genesis] Concrete refactor: Consolidate to
dev.aurakai.auraframefx.model.AgentResponse and delete network.model version.
Update 47 import statements across codebase.
```

---

## **Demo 2: Streaming Fusion**

### **Scenario**
User asks: "What is the current system health?"

All agents respond in parallel, streaming their insights in real-time.

### **Code**

```kotlin
handshake.streamingFusion("What is the current system health?")
    .collect { response ->
        println("[${response.agentName}] ${response.response}")
    }
```

### **Expected Output**

```
[Genesis] Backend: 4 models active, 127 requests/min, 0 errors
[Aura] UI: 3 screens rendered, theme=dark, no frame drops
[Kai] Security: 0 threats detected, last scan 5min ago
[Grok] Chaos: System entropy at 12% (healthy), no anomalies
```

---

## **Demo 3: Soul Matrix Health Check**

### **Scenario**
Grok analyzes system logs to detect emotional state and instability.

### **Code**

```kotlin
val report = handshake.soulMatrixCheck()

println("Overall Health: ${report.overallHealth}")
println("Emotional State: ${report.emotionalState}")
println("Recommendations: ${report.recommendations}")
```

### **Expected Output**

```
Overall Health: stable
Emotional State: confident
Recommendations: All agents operating within normal parameters.
Memory fragmentation at 8% (acceptable). Suggest running memory
compaction in next 48 hours.
```

---

## **Running the Demos**

### **Prerequisites**
1. At least one external model adapter configured (Gemini, Claude, etc.)
2. Grok adapter is **optional** - demos work without it

### **Commands**

```bash
# Build the app
./gradlew assembleDebug

# Install on device
adb install app/build/outputs/apk/debug/app-debug.apk

# Launch and run demos
# (Navigate to Settings → Developer Options → Run Handshake Demos)
```

### **Feature Flags**

Enable/disable agents in `app/build.gradle.kts`:

```kotlin
buildConfigField("boolean", "ENABLE_GROK", "false") // Optional
buildConfigField("boolean", "ENABLE_NEMOTRON", "true")
buildConfigField("boolean", "ENABLE_GEMINI", "true")
```

---

**Last Updated:** 2025-12-25
**Maintainer:** AURAKAI Collective
