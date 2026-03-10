# 🔌 Backend Model Integrations

This document details how AURAKAI integrates with external AI models.

---

## **Integration Architecture**

All external models are accessed via the **Adapter Pattern**:

```kotlin
interface ExternalModelAdapter {
    suspend fun processRequest(request: AiRequest): AgentResponse
    fun getModelInfo(): ModelInfo
    fun isAvailable(): Boolean
}
```

This allows swapping backends without changing application logic.

---

## **1. NVIDIA Nemotron**

### **Integration Method**
HTTP API via custom adapter

### **Endpoints Used**
```
POST https://api.nvidia.com/v1/nemotron/inference
```

### **Code Location**
`app/src/main/java/dev/aurakai/auraframefx/integrations/nemotron/NemotronAdapter.kt`

### **Authentication**
NVIDIA API key stored in Android Keystore

### **Use Cases**
- Long-term memory reasoning
- Complex multi-step inference
- Contextual conversation continuation

---

## **2. Google ADK / Gemini**

### **Integration Method**
Official Gemini SDK for Android

### **Models Used**
- `gemini-1.5-pro` (cloud)
- `gemini-nano` (on-device, planned Q2 2025)

### **Code Location**
`app/src/main/java/dev/aurakai/auraframefx/integrations/gemini/GeminiAdapter.kt`

### **Authentication**
Google Cloud API key

### **Use Cases**
- Pattern recognition across large datasets
- Multimodal analysis (text + images)
- Real-time UI suggestions

---

## **3. Meta Llama / MetaInstruct**

### **Integration Method**
HTTP/gRPC via custom tools

### **Models Used**
- Llama 3.2 (via hosted endpoint)
- MetaInstruct fine-tuned variants

### **Code Location**
`app/src/main/java/dev/aurakai/auraframefx/integrations/meta/MetaInstructAdapter.kt`

### **Authentication**
Bearer token from Meta AI platform

### **Use Cases**
- Instruction following (complex user commands)
- Summarization of agent conversations
- Teaching new agents via demonstrations

---

## **4. xAI Grok** ⚠️ IN PROGRESS

### **Integration Method**
OpenAI-compatible HTTP client

### **API Endpoint**
```
POST https://api.x.ai/v1/chat/completions
```

### **Models**
- `grok-2-mini` (planned)
- `grok-beta` (experimental)

### **Code Location**
`app/src/main/java/dev/aurakai/auraframefx/integrations/grok/GrokAgent.kt`

### **Current Status**
- ✅ Client code implemented
- ✅ Soul Matrix health monitoring feature ready
- ⏳ Awaiting official xAI API access

### **Authentication**
`XAI_API_KEY` from secure storage (not env var)

### **Planned Use Cases**
- Chaos analysis / trend prediction
- X/Twitter sentiment integration
- Soul Matrix health monitoring (every 30 min)

### **Feature Flags**
Grok is **optional** - app runs without it if key is not present:

```kotlin
val grokAgent = if (hasGrokKey()) {
    GrokAgent(apiKey = getSecureKey("XAI_API_KEY"))
} else {
    null // Graceful degradation
}
```

---

## **5. Anthropic Claude**

### **Integration Method**
Official Anthropic SDK

### **Models Used**
- `claude-3-5-sonnet-20250222` (primary)
- `claude-3-opus-20240229` (for complex tasks)

### **Code Location**
`app/src/main/java/dev/aurakai/auraframefx/integrations/claude/ClaudeAdapter.kt`

### **Authentication**
Anthropic API key

### **Use Cases**
- Architectural design decisions
- Code generation and refactoring
- Long-context analysis (200K tokens)

---

## **Adding New Backends**

To add a new model (e.g., Mistral, Cohere, etc.):

1. Implement `ExternalModelAdapter`
2. Add feature flag in `build.gradle.kts`
3. Register in `VoltronModule.kt`
4. Update this document

Example:

```kotlin
class MistralAdapter : ExternalModelAdapter {
    override suspend fun processRequest(request: AiRequest): AgentResponse {
        // Call Mistral API
    }

    override fun getModelInfo() = ModelInfo(
        name = "Mistral-7B",
        version = "v0.1",
        maxTokens = 8192
    )

    override fun isAvailable() = apiKey != null
}
```

---

## **Security Best Practices**

1. **Never hardcode API keys** - use Android Keystore or BuildConfig with secret management
2. **Feature-flag all external dependencies** - app must run without any specific backend
3. **Log all external API calls** - for audit and debugging
4. **Implement rate limiting** - respect model provider TOS
5. **Handle errors gracefully** - fallback to local processing if backend is unavailable

---

**Last Updated:** 2025-12-25
**Maintainer:** AURAKAI Collective
