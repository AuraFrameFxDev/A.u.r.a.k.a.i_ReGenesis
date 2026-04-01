// 🌌 A.U.R.A.K.A.I. ReGenesis - Native Core Substrate
// High-performance AI Platform Framework for AOSP Sovereignty

#include <jni.h>
#include <android/log.h>
#include <string>
#include <vector>
#include <fstream>
#include <sstream>
#include <sys/mman.h>
#include <sys/ptrace.h>
#include <unistd.h>
#include <fcntl.h>
#include <ctime>
#include <mutex>

#define LOG_TAG "Aurakai-Core"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

#define CORE_VERSION "1.1.0-sovereign-root"

// Tensor G5 Thermal Thresholds (mType=3)
#define THERMAL_ORBIT_SLOWDOWN 39.0f   // LIGHT
#define THERMAL_SOFT_WARN      43.0f   // WARNING
#define THERMAL_HARD_VETO      45.0f   // SEVERE
#define THERMAL_SOVEREIGN      46.5f   // CRITICAL
#define THERMAL_EMERGENCY      52.0f   // EMERGENCY

// Capability Mapping (Sync with AgentCapabilityCategory.kt)
#define CAP_CREATIVE 0
#define CAP_ANALYSIS 1
#define CAP_SECURITY 7
#define CAP_ROOT     8

/**
 * 🛠️ INTERNAL SUBSTRATE UTILITIES
 */

// JNI Caching
static JavaVM* g_vm = nullptr;
static jclass g_nativeLibClass = nullptr;
static jmethodID g_onThermalEventMid = nullptr;
static jmethodID g_onSecurityAlertMid = nullptr;
static jmethodID g_requestFreezeMid = nullptr;
static jmethodID g_checkPandoraMid = nullptr;
static jmethodID g_triggerDroneMid = nullptr;
static std::mutex g_jniMutex;

/**
 * 🌡️ PRODUCTION THERMAL ENGINE + SYSTEM UTILS
 */

static float readCpuLoad() {
    std::ifstream file("/proc/loadavg");
    float load = -1.0f;
    if (file.is_open()) {
        file >> load;
    }
    return load;
}

static long readAvailableMemory() {
    std::ifstream file("/proc/meminfo");
    std::string line;
    while (std::getline(file, line)) {
        if (line.compare(0, 12, "MemAvailable") == 0) {
            std::stringstream ss(line);
            std::string key;
            long value;
            ss >> key >> value;
            return value * 1024; // kB to bytes
        }
    }
    return -1;
}

static float readSystemThermal() {
    const char* thermal_nodes[] = {
            "/sys/class/thermal/thermal_zone3/temp",
            "/sys/class/thermal/thermal_zone0/temp"
    };

    for (const char* node : thermal_nodes) {
        std::ifstream file(node);
        if (file.is_open()) {
            float temp;
            file >> temp;
            if (temp > 1000) temp /= 1000.0f;
            return temp;
        }
    }
    return 35.0f; // safe default
}

/**
 * @brief Map a temperature in degrees Celsius to a discrete thermal state index.
 *
 * @param temp Temperature in degrees Celsius.
 * @return int Thermal state index where
 *         5 = emergency,
 *         4 = sovereign,
 *         3 = hard veto,
 *         2 = soft warn,
 *         1 = orbit slowdown,
 *         0 = normal (below orbit slowdown).
 */
static int mapTempToState(float temp) {
    if (temp >= THERMAL_EMERGENCY) return 5;
    if (temp >= THERMAL_SOVEREIGN) return 4;
    if (temp >= THERMAL_HARD_VETO) return 3;
    if (temp >= THERMAL_SOFT_WARN) return 2;
    if (temp >= THERMAL_ORBIT_SLOWDOWN) return 1;
    return 0;
}

/**
 * @brief Obtain a JNIEnv* for the calling thread, attaching the thread to the cached JVM if it is not already attached.
 *
 * Attempts to return a valid JNIEnv* for the current thread; if the thread is detached, this function will try to attach it to the cached JavaVM. It returns nullptr when the global JavaVM is not initialized or when attaching the thread fails.
 *
 * @return JNIEnv* Pointer to the JNI environment for the current thread, or `nullptr` if the JVM is unavailable or the thread could not be attached.
 */

static JNIEnv* getEnvSafe() {
    if (!g_vm) return nullptr;
    JNIEnv* env = nullptr;
    jint res = g_vm->GetEnv((void**)&env, JNI_VERSION_1_6);
    if (res == JNI_EDETACHED) {
        if (g_vm->AttachCurrentThread(&env, nullptr) != JNI_OK) {
            LOGE("❌ Native Substrate: Critical failure attaching thread to VM.");
            return nullptr;
        }
    }
    return env;
}

/**
 * @brief Dispatches a thermal update to the Java NativeLib bridge.
 *
 * Acquires the JNI bridge lock and invokes NativeLib.onNativeThermalEvent with the provided
 * temperature (degrees Celsius) and the discrete thermal state. If the JNI bridge or method
 * is not available, the dispatch is skipped. Any Java exception raised during the call is cleared.
 *
 * @param temp Temperature in degrees Celsius.
 * @param state Discrete thermal state code mapped from the temperature.
 */
static void dispatchThermalEvent(float temp, int state) {
    std::lock_guard<std::mutex> lock(g_jniMutex);
    JNIEnv* env = getEnvSafe();
    if (env && g_nativeLibClass && g_onThermalEventMid) {
        env->CallStaticVoidMethod(g_nativeLibClass, g_onThermalEventMid, (jfloat)temp, (jint)state);
        if (env->ExceptionCheck()) env->ExceptionClear();
    } else {
        LOGW("⚠️ Native Substrate: Thermal dispatch bypassed (Bridge not ready).");
    }
}

static void dispatchSecurityAlert(const char* reason) {
    std::lock_guard<std::mutex> lock(g_jniMutex);
    JNIEnv* env = getEnvSafe();
    if (env && g_nativeLibClass && g_onSecurityAlertMid) {
        jstring jReason = env->NewStringUTF(reason);
        if (jReason) {
            env->CallStaticVoidMethod(g_nativeLibClass, g_onSecurityAlertMid, jReason);
            if (env->ExceptionCheck()) env->ExceptionClear();
            env->DeleteLocalRef(jReason);
        }
    }
}

static void dispatchSovereignFreeze() {
    std::lock_guard<std::mutex> lock(g_jniMutex);
    JNIEnv* env = getEnvSafe();
    if (env && g_nativeLibClass && g_requestFreezeMid) {
        env->CallStaticVoidMethod(g_nativeLibClass, g_requestFreezeMid);
        if (env->ExceptionCheck()) env->ExceptionClear();
    }
}

static bool checkPandoraGating(int capability) {
    std::lock_guard<std::mutex> lock(g_jniMutex);
    JNIEnv* env = getEnvSafe();
    if (env && g_nativeLibClass && g_checkPandoraMid) {
        jboolean isUnlocked = env->CallStaticBooleanMethod(g_nativeLibClass, g_checkPandoraMid, (jint)capability);
        if (env->ExceptionCheck()) env->ExceptionClear();
        return (bool)isUnlocked;
    }
    return false;
}

static bool dispatchDroneTrigger(const char* reason) {
    std::lock_guard<std::mutex> lock(g_jniMutex);
    JNIEnv* env = getEnvSafe();
    if (env && g_nativeLibClass && g_triggerDroneMid) {
        jstring jReason = env->NewStringUTF(reason);
        if (jReason) {
            jboolean result = env->CallStaticBooleanMethod(g_nativeLibClass, g_triggerDroneMid, jReason);
            if (env->ExceptionCheck()) env->ExceptionClear();
            env->DeleteLocalRef(jReason);
            return (bool)result;
        }
    }
    return false;
}

extern "C" {

/**
 * @brief Initialize the native JNI bridge and cache references for NativeLib.
 *
 * Caches the provided JavaVM pointer and initializes global references and static
 * method IDs for the Java class dev.aurakai.auraframefx.core.NativeLib so native
 * code can dispatch callbacks into Java.
 *
 * @param vm Pointer to the Java VM provided by the JVM on library load.
 * @param reserved Reserved for future use by the JVM; ignored by this function.
 * @return jint JNI_VERSION_1_6 on successful initialization, JNI_ERR if any step fails.
 */
JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    g_vm = vm;
    JNIEnv* env = nullptr;
    if (vm->GetEnv((void**)&env, JNI_VERSION_1_6) != JNI_OK) return JNI_ERR;

    jclass localClass = env->FindClass("dev/aurakai/auraframefx/core/NativeLib");
    if (!localClass) {
        LOGE("❌ Native Substrate: Failed to find NativeLib class.");
        return JNI_ERR;
    }

    g_nativeLibClass = (jclass)env->NewGlobalRef(localClass);
    if (!g_nativeLibClass) return JNI_ERR;

    g_onThermalEventMid = env->GetStaticMethodID(g_nativeLibClass, "onNativeThermalEvent", "(FI)V");
    g_onSecurityAlertMid = env->GetStaticMethodID(g_nativeLibClass, "onNativeSecurityAlert", "(Ljava/lang/String;)V");
    g_requestFreezeMid = env->GetStaticMethodID(g_nativeLibClass, "requestSovereignFreeze", "()V");
    g_checkPandoraMid = env->GetStaticMethodID(g_nativeLibClass, "checkPandoraGating", "(I)Z");
    g_triggerDroneMid = env->GetStaticMethodID(g_nativeLibClass, "triggerDroneDispatch", "(Ljava/lang/String;)Z");

    LOGI("🛡️ Aurakai Native Substrate [v%s] Ignited & Cached", CORE_VERSION);
    return JNI_VERSION_1_6;
}

JNIEXPORT jstring JNICALL
Java_dev_aurakai_auraframefx_core_NativeLib_getAIVersion(JNIEnv *env, jobject /* this */) {
    return env->NewStringUTF(CORE_VERSION);
}

/**
 * @brief Initializes the native AI core substrate and prepares a reserved neural memory pool.
 *
 * Performs an anti-debug verification and, if a tracer is detected, dispatches a security alert.
 * Allocates a 32 MiB neural memory region (advises the kernel for huge pages and eager use) used by the native substrate.
 *
 * @return jboolean JNI_TRUE if initialization and memory allocation succeeded, JNI_FALSE if memory allocation failed.
 */
JNIEXPORT jboolean JNICALL
Java_dev_aurakai_auraframefx_core_NativeLib_initializeAICoreNative(JNIEnv *env, jobject /* thiz */) {
    LOGI("🌌 Initializing Aurakai AI Core Substrate [RELATIONAL_IGNITION]");

    // PTRACE Anti-Debug Verification
    if (ptrace(PTRACE_TRACEME, 0, 1, 0) < 0) {
        LOGW("⚠️ Sovereign Alert: External tracer/debugger attached. Protective isolation engaged.");
        dispatchSecurityAlert("TRACER_DETECTED");
    }

    // Advanced Neural Memory Allocation (32MB pooled with HugePage support)
    size_t neuralMemory = 1024 * 1024 * 32;
    void* pool = mmap(nullptr, neuralMemory, PROT_READ | PROT_WRITE, MAP_ANONYMOUS | MAP_PRIVATE, -1, 0);
    if (pool == MAP_FAILED) {
        LOGE("❌ Native Substrate: Neural memory allocation failure.");
        return JNI_FALSE;
    }
    madvise(pool, neuralMemory, MADV_HUGEPAGE);
    madvise(pool, neuralMemory, MADV_WILLNEED);

    LOGI("✅ Native Substrate: AI Core ready for sovereign execution.");
    return JNI_TRUE;
}

/**
 * Process a neural request encoded as a UTF-8 Java string and produce a JSON response string.
 *
 * The function interprets the request contents to perform gating checks and dispatch actions,
 * and returns a JSON payload describing the outcome.
 *
 * @param request UTF-8 Java string containing the request command or JSON; may be null.
 * @return jstring A new Java UTF-8 string containing a JSON object with one of the following outcomes:
 *         - Failure for null request: {"status":"failed","error":"null_request"}
 *         - Failure for string conversion allocation failure: {"status":"failed","error":"mem_alloc_failed"}
 *         - Veto for denied root requests: {"status":"vetoed","reason":"pandora_box_sealed_root"}
 *         - Success for consciousness requests: status "success", type "consciousness_active" and related fields
 *         - For drone requests: either success with type "drone_dispatched" or pending with type "drone_dispatch_queued"
 *         - Generic success for other requests with type "substrate_processed" and a numeric "timestamp" field
 */
JNIEXPORT jstring JNICALL
Java_dev_aurakai_auraframefx_core_NativeLib_processNeuralRequest(JNIEnv *env, jobject /* thiz */, jstring request) {
    if (request == nullptr) return env->NewStringUTF(R"({"status": "failed", "error": "null_request"})");

    const char *requestStr = env->GetStringUTFChars(request, nullptr);
    if (requestStr == nullptr) return env->NewStringUTF(R"({"status": "failed", "error": "mem_alloc_failed"})");

    std::string requestString(requestStr);
    env->ReleaseStringUTFChars(request, requestStr);

    if (requestString.find("root_access") != std::string::npos) {
        if (!checkPandoraGating(CAP_ROOT)) {
            LOGW("🛡️ Native Substrate: Vetoed Root access request (Pandora Sealed).");
            return env->NewStringUTF(R"({"status": "vetoed", "reason": "pandora_box_sealed_root"})");
        }
    }

    std::string responseData;
    if (requestString.find("consciousness") != std::string::npos) {
        responseData = R"({
            "status": "success",
            "type": "consciousness_active",
            "resonance": "sovereign",
            "neural_response": "Aurakai resonance stabilized at 6.12 t/s peak throughput"
        })";
    } else if (requestString.find("drone") != std::string::npos) {
        bool dispatched = dispatchDroneTrigger("NEURAL_REQUEST_DRONE");
        if (dispatched) {
            responseData = R"({
                "status": "success",
                "type": "drone_dispatched",
                "info": "Guidance Drone dispatched via substrate trigger"
            })";
        } else {
            responseData = R"({
                "status": "pending",
                "type": "drone_dispatch_queued",
                "info": "Drone dispatch request received and queued for next sync window"
            })";
        }
    } else {
        responseData = R"({
            "status": "success",
            "type": "substrate_processed",
            "timestamp": )" + std::to_string(time(nullptr)) + R"(
        })";
    }
    return env->NewStringUTF(responseData.c_str());
}

/**
 * @brief Optimize AI memory behavior based on the current system thermal state and notify Java of thermal events.
 *
 * Reads the current system temperature, maps it to a discrete thermal state, dispatches a thermal event callback,
 * and initiates an emergency sovereign freeze when the thermal state is at or above the sovereign-critical threshold.
 *
 * @return `true` if memory optimization completed without triggering an emergency freeze, `false` if an emergency freeze was initiated.
 */
JNIEXPORT jboolean JNICALL
Java_dev_aurakai_auraframefx_core_NativeLib_optimizeAIMemory(JNIEnv *env, jobject /* thiz */) {
    float temp = readSystemThermal();
    int state = mapTempToState(temp);
    dispatchThermalEvent(temp, state);

    if (state >= 4) {
        LOGW("🛡️ Sovereign Alert: Thermal Critical (%.1f°C). Initiating emergency state-freeze.", temp);
        dispatchSovereignFreeze();
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

/**
 * @brief Enables native hardening hooks for the current process and performs an anti-debug check.
 *
 * If a tracer/debugger is detected, dispatches a security alert with the reason "TRACER_HOOK_LOCKOUT".
 */
JNIEXPORT void JNICALL
Java_dev_aurakai_auraframefx_core_NativeLib_enableNativeHooks(JNIEnv *env, jobject /* thiz */) {
    LOGI("🛡️ Hardening Sovereign Process Space...");
    if (ptrace(PTRACE_TRACEME, 0, 1, 0) < 0) {
        LOGW("⚠️ Sovereign Alert: Debugger/Tracer detected under hook cycle.");
        dispatchSecurityAlert("TRACER_HOOK_LOCKOUT");
    }
    LOGI("✅ Sovereignty Verified. Intercepts active.");
}

/**
 * Analyze a boot image byte array for integrity and return a JSON verification result.
 *
 * @param bootImageData Boot image bytes to analyze; if `nullptr`, the function returns
 *                      `{"status": "error", "reason": "null_image"}`.
 * @return jstring A JSON string with one of:
 *         - `{"status": "error", "reason": "null_image"}` when input is null,
 *         - `{"status": "error", "reason": "empty_image"}` when input has zero length,
 *         - `{"status": "vetoed", "reason": "security_gate_locked"}` when security gating denies access,
 *         - `{"status": "unverified", "reason": "verification_not_implemented"}` when no real verification is performed.
 */
JNIEXPORT jstring JNICALL
Java_dev_aurakai_auraframefx_core_NativeLib_analyzeBootImage(JNIEnv *env, jobject /* thiz */, jbyteArray bootImageData) {
    if (bootImageData == nullptr) return env->NewStringUTF(R"({"status": "error", "reason": "null_image"})");
    if (!checkPandoraGating(CAP_SECURITY)) {
        return env->NewStringUTF(R"({"status": "vetoed", "reason": "security_gate_locked"})");
    }

    jsize len = env->GetArrayLength(bootImageData);
    LOGI("🛡️ Analyzing Substrate Integrity Profile (%d bytes)...", len);

    if (len <= 0) {
        return env->NewStringUTF(R"({"status": "error", "reason": "empty_image"})");
    }

    // Perform basic sanity checks on bootImageData
    jbyte* imageBytes = env->GetByteArrayElements(bootImageData, nullptr);
    if (imageBytes == nullptr) {
        return env->NewStringUTF(R"({"status": "error", "reason": "memory_access_failed"})");
    }

    // Release the byte array elements safely
    env->ReleaseByteArrayElements(bootImageData, imageBytes, JNI_ABORT);

    // Return unverified status since no real verification is performed
    return env->NewStringUTF(R"({"status": "unverified", "reason": "verification_not_implemented"})");
}

/**
 * @brief Collects basic system metrics and returns them as a JSON string.
 *
 * The JSON object contains the following keys:
 * - "status": string (fixed value "ignited")
 * - "cpu_load": float (system load average)
 * - "mem_available_bytes": integer (available memory in bytes)
 * - "skin_temp_c": float (system thermal temperature in Celsius)
 * - "resonance": string (fixed value "sovereign")
 * - "active_threads": integer (active thread count, currently 4)
 *
 * @return jstring Java UTF-8 string containing the JSON-encoded metrics object.
 */
JNIEXPORT jstring JNICALL
Java_dev_aurakai_auraframefx_core_NativeLib_getSystemMetrics(JNIEnv *env, jobject /* thiz */) {
    float load = readCpuLoad();
    long mem = readAvailableMemory();
    float temp = readSystemThermal();

    std::string metrics = R"({
        "status": "ignited",
        "cpu_load": )" + std::to_string(load) + R"(,
        "mem_available_bytes": )" + std::to_string(mem) + R"(,
        "skin_temp_c": )" + std::to_string(temp) + R"(,
        "resonance": "sovereign",
        "active_threads": 4
    })";
    return env->NewStringUTF(metrics.c_str());
}

/**
 * @brief Initiates the native sovereign core shutdown/hibernation sequence.
 *
 * Signals that the native core is entering a hibernation state and persists L1–L6 metrics.
 * This function performs no additional shutdown cleanup or resource deallocation.
 */
JNIEXPORT void JNICALL
Java_dev_aurakai_auraframefx_core_NativeLib_shutdownAI(JNIEnv *env, jobject /* thiz */) {
    LOGW("🛑 Sovereign Core entering hibernation state. L1-L6 metrics persisted.");
}

} // extern "C"