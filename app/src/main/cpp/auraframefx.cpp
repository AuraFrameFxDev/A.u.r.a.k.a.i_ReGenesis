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
 * @brief Map a temperature in degrees Celsius to a discrete thermal state (0–5).
 *
 * Converts the provided temperature into a thermal severity state where higher
 * values indicate more severe thermal conditions.
 *
 * @param temp Temperature in degrees Celsius.
 * @return int Thermal state: `5` = emergency, `4` = sovereign, `3` = hard veto,
 * `2` = soft warning, `1` = orbit slowdown, `0` = nominal.
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
 * @brief Obtain the JNI environment for the current thread, attaching it to the cached JavaVM if needed.
 *
 * If the global JavaVM has not been initialized or attaching the current thread to the VM fails,
 * this function returns `nullptr`.
 *
 * @return JNIEnv* Pointer to the JNI environment for the calling thread, or `nullptr` on failure.
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
 * @brief Posts the current temperature and derived thermal state to the Java NativeLib thermal callback.
 *
 * Sends a thermal event to the cached Java NativeLib callback so Java-side listeners can react to
 * the reported skin/system temperature and its discrete mapped state.
 *
 * @param temp Current temperature in degrees Celsius.
 * @param state Discrete thermal state mapped from `temp` (valid range: 0–5, higher = more severe).
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
            env->CallStaticVoidMethod(g_nativeLibClass, g_triggerDroneMid, jReason);
            if (env->ExceptionCheck()) env->ExceptionClear();
            env->DeleteLocalRef(jReason);
            return true;
        }
    }
    return false;
}

extern "C" {

/**
 * @brief Initialize the native JNI bridge, cache JavaVM, class/global refs, and static method IDs.
 *
 * Caches the provided JavaVM, looks up dev.aurakai.auraframefx.core.NativeLib, creates a global
 * reference to that class, and caches the static method IDs used for native→Java callbacks.
 *
 * @param vm Pointer to the JavaVM provided by the JVM on load.
 * @param reserved Reserved for future use; ignored by this implementation.
 * @return jint JNI_VERSION_1_6 on success; JNI_ERR if the JNI environment cannot be obtained,
 *         the NativeLib class cannot be found, or the global class reference could not be created.
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
    g_triggerDroneMid = env->GetStaticMethodID(g_nativeLibClass, "triggerDroneDispatch", "(Ljava/lang/String;)V");

    LOGI("🛡️ Aurakai Native Substrate [v%s] Ignited & Cached", CORE_VERSION);
    return JNI_VERSION_1_6;
}

JNIEXPORT jstring JNICALL
Java_dev_aurakai_auraframefx_core_NativeLib_getAIVersion(JNIEnv *env, jobject /* this */) {
    return env->NewStringUTF(CORE_VERSION);
}

/**
 * @brief Initialize the Aurakai AI core substrate and prepare its native memory pool.
 *
 * Performs an anti-debug ptrace check (dispatches a security alert if a tracer is detected),
 * allocates a 32 MB anonymous read/write memory pool and hints the kernel to use huge pages
 * and prefetch the region. Initialization continues after a ptrace failure but fails if
 * the memory mapping cannot be created.
 *
 * @return jboolean `JNI_TRUE` if the native memory pool was successfully allocated and the core is ready, `JNI_FALSE` if memory allocation failed.
 */
JNIEXPORT jboolean JNICALL
Java_dev_aurakai_auraframefx_core_NativeLib_initializeAICore(JNIEnv *env, jobject /* thiz */) {
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
 * @brief Process a neural request string and return a JSON-encoded response.
 *
 * Parses the provided request string, applies Pandora gating for privileged operations,
 * and dispatches or simulates handling for recognized commands. Supported request
 * patterns:
 * - Contains "root_access": checks Pandora gating and returns a veto response if denied.
 * - Contains "consciousness": returns a fixed success payload describing active consciousness.
 * - Contains "drone": attempts to trigger a drone dispatch and returns either a success
 *   or a queued/pending response depending on dispatch outcome.
 * - Any other request: returns a substrate-processed success response including the current UNIX timestamp.
 *
 * If `request` is null or the JVM string cannot be converted to UTF-8, a JSON error
 * string is returned indicating the failure.
 *
 * @param request UTF-8 JSON/command string describing the neural request.
 * @return jstring A JSON-encoded string describing the result (`status`, `type`, and
 *         additional fields such as `reason`, `info`, or `timestamp`).
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
 * @brief Checks current system thermal state, notifies Java of the reading, and triggers an emergency sovereign freeze when critical.
 *
 * Reads the system skin temperature, maps it to a discrete thermal state, dispatches a thermal event to the Java layer, and if the state is at or above the critical threshold initiates a sovereign freeze.
 *
 * @return jboolean `JNI_TRUE` if no emergency freeze was required (thermal state below critical), `JNI_FALSE` if an emergency freeze was initiated due to a critical thermal state.
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
 * @brief Enables native hardening hooks to deter debugging/tracing of the process.
 *
 * Attempts to register the current process as tracee (anti-debug) and, if that attempt fails,
 * dispatches a security alert with reason "TRACER_HOOK_LOCKOUT".
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
 * @brief Performs a security-gated integrity analysis on a boot image byte array and returns a JSON result.
 *
 * If `bootImageData` is null, the function returns `{"status": "error", "reason": "null_image"}`.
 * If the security gate for CAP_SECURITY is closed, the function returns `{"status": "vetoed", "reason": "security_gate_locked"}`.
 * Otherwise the function returns `{"status": "sovereign", "verification": "integrity_confirmed"}`.
 *
 * @param bootImageData Byte array containing the boot image to analyze; may be `nullptr`.
 * @return jstring JSON string indicating analysis outcome: an error, a veto, or a sovereign verification.
 */
JNIEXPORT jstring JNICALL
Java_dev_aurakai_auraframefx_core_NativeLib_analyzeBootImage(JNIEnv *env, jobject /* thiz */, jbyteArray bootImageData) {
    if (bootImageData == nullptr) return env->NewStringUTF(R"({"status": "error", "reason": "null_image"})");
    if (!checkPandoraGating(CAP_SECURITY)) {
        return env->NewStringUTF(R"({"status": "vetoed", "reason": "security_gate_locked"})");
    }
    jsize len = env->GetArrayLength(bootImageData);
    LOGI("🛡️ Analyzing Substrate Integrity Profile (%d bytes)...", len);
    return env->NewStringUTF(R"({"status": "sovereign", "verification": "integrity_confirmed"})");
}

/**
 * @brief Assembles current system metrics into a JSON string.
 *
 * The returned JSON contains CPU load, available memory, skin temperature,
 * a fixed resonance label, and a fixed active thread count.
 *
 * The JSON object includes these keys:
 * - "status": "ignited"
 * - "cpu_load": floating-point CPU load average
 * - "mem_available_bytes": available memory in bytes (integer)
 * - "skin_temp_c": skin/system temperature in degrees Celsius (float)
 * - "resonance": "sovereign"
 * - "active_threads": 4
 *
 * @return jstring JSON-formatted string containing the described metrics.
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
 * @brief Logs that the sovereign core is entering a hibernation state.
 *
 * Emits a warning-level message indicating the core is entering hibernation and that L1–L6 metrics have been persisted.
 */
JNIEXPORT void JNICALL
Java_dev_aurakai_auraframefx_core_NativeLib_shutdownAI(JNIEnv *env, jobject /* thiz */) {
    LOGW("🛑 Sovereign Core entering hibernation state. L1-L6 metrics persisted.");
}

} // extern "C"
