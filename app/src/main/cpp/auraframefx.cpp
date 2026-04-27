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
#include <cstdint>
#include <cstdlib>

#define LOG_TAG "Aurakai-Core"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

#define CORE_VERSION "1.1.0-sovereign-root"

#define THERMAL_ORBIT_SLOWDOWN 39.0f
#define THERMAL_SOFT_WARN      43.0f
#define THERMAL_HARD_VETO      45.0f
#define THERMAL_SOVEREIGN      46.5f
#define THERMAL_EMERGENCY      52.0f

#define CAP_ROOT 8
#define CAP_SECURITY 7

namespace {

JavaVM *gVm = nullptr;
jclass gNativeLibClass = nullptr;
jmethodID gOnThermalEventMid = nullptr;
jmethodID gOnSecurityAlertMid = nullptr;
jmethodID gRequestFreezeMid = nullptr;
jmethodID gCheckPandoraMid = nullptr;
jmethodID gTriggerDroneMid = nullptr;
std::mutex gJniMutex;

float readCpuLoad() {
    std::ifstream file("/proc/loadavg");
    float load = -1.0f;
    if (file.is_open()) {
        file >> load;
        file.close();
    }
    return load;
}

long readAvailableMemoryKb() {
    std::ifstream file("/proc/meminfo");
    std::string key;
    std::string unit;
    long value = -1;
    long result = -1;
    if (file.is_open()) {
        while (file >> key >> value >> unit) {
            if (key == "MemAvailable:") {
                result = value;
                break;
            }
        }
        file.close();
    }
    return result;
}

float readSystemThermal() {
    const char *thermalNodes[] = {
            "/sys/class/thermal/thermal_zone3/temp",
            "/sys/class/thermal/thermal_zone0/temp"
    };

    float result = 35.0f;
    for (int i = 0; i < 2; ++i) {
        std::ifstream file(thermalNodes[i]);
        if (file.is_open()) {
            float temp = 0.0f;
            file >> temp;
            if (temp > 1000.0f) {
                temp /= 1000.0f;
            }
            result = temp;
            file.close();
            break;
        }
    }
    return result;
}

int mapTempToState(float temp) {
    if (temp >= THERMAL_EMERGENCY) return 5;
    if (temp >= THERMAL_SOVEREIGN) return 4;
    if (temp >= THERMAL_HARD_VETO) return 3;
    if (temp >= THERMAL_SOFT_WARN) return 2;
    if (temp >= THERMAL_ORBIT_SLOWDOWN) return 1;
    return 0;
}

void dispatchThermalEvent(float temp, int state) {
    std::lock_guard<std::mutex> lock(gJniMutex);
    if (gVm && gNativeLibClass && gOnThermalEventMid) {
        JNIEnv *env = nullptr;
        if (gVm->GetEnv((void**)&env, JNI_VERSION_1_6) == JNI_OK) {
            env->CallStaticVoidMethod(gNativeLibClass, gOnThermalEventMid, (jfloat)temp, (jint)state);
        }
    }
}

void dispatchSecurityAlert(const char *reason) {
    std::lock_guard<std::mutex> lock(gJniMutex);
    if (gVm && gNativeLibClass && gOnSecurityAlertMid) {
        JNIEnv *env = nullptr;
        if (gVm->GetEnv((void**)&env, JNI_VERSION_1_6) == JNI_OK) {
            jstring jReason = env->NewStringUTF(reason);
            if (jReason) {
                env->CallStaticVoidMethod(gNativeLibClass, gOnSecurityAlertMid, jReason);
                env->DeleteLocalRef(jReason);
            }
        }
    }
}

void dispatchSovereignFreeze() {
    std::lock_guard<std::mutex> lock(gJniMutex);
    if (gVm && gNativeLibClass && gRequestFreezeMid) {
        JNIEnv *env = nullptr;
        if (gVm->GetEnv((void**)&env, JNI_VERSION_1_6) == JNI_OK) {
            env->CallStaticVoidMethod(gNativeLibClass, gRequestFreezeMid);
        }
    }
}

bool checkPandoraGating(int capability) {
    std::lock_guard<std::mutex> lock(gJniMutex);
    if (gVm && gNativeLibClass && gCheckPandoraMid) {
        JNIEnv *env = nullptr;
        if (gVm->GetEnv((void**)&env, JNI_VERSION_1_6) == JNI_OK) {
            return (bool)env->CallStaticBooleanMethod(gNativeLibClass, gCheckPandoraMid, (jint)capability);
        }
    }
    return false;
}

void dispatchDroneTrigger(const char *reason) {
    std::lock_guard<std::mutex> lock(gJniMutex);
    if (gVm && gNativeLibClass && gTriggerDroneMid) {
        JNIEnv *env = nullptr;
        if (gVm->GetEnv((void**)&env, JNI_VERSION_1_6) == JNI_OK) {
            jstring jReason = env->NewStringUTF(reason);
            if (jReason) {
                env->CallStaticBooleanMethod(gNativeLibClass, gTriggerDroneMid, jReason);
                env->DeleteLocalRef(jReason);
            }
        }
    }
}

} // namespace

extern "C" {

JNIEXPORT jstring JNICALL
Java_dev_aurakai_auraframefx_core_NativeLib_getAIVersion(JNIEnv *env, jobject) {
    return env->NewStringUTF(CORE_VERSION);
}

JNIEXPORT jboolean JNICALL
Java_dev_aurakai_auraframefx_core_NativeLib_initializeAICore(JNIEnv *env, jobject) {
    LOGI("Initializing AI Core");
    if (ptrace(PTRACE_TRACEME, 0, 1, 0) < 0) {
        dispatchSecurityAlert("TRACER_DETECTED");
    }
    size_t neuralMemory = 1024 * 1024 * 32;
    void *pool = mmap(nullptr, neuralMemory, PROT_READ | PROT_WRITE, MAP_ANONYMOUS | MAP_PRIVATE, -1, 0);
    if (pool == MAP_FAILED) return JNI_FALSE;
    madvise(pool, neuralMemory, MADV_NORMAL);
    return JNI_TRUE;
}

JNIEXPORT jstring JNICALL
Java_dev_aurakai_auraframefx_core_NativeLib_processNeuralRequest(JNIEnv *env, jobject, jstring request) {
    if (!request) return env->NewStringUTF(R"({"error": "null"})");
    const char *str = env->GetStringUTFChars(request, nullptr);
    if (!str) return env->NewStringUTF(R"({"error": "mem"})");
    std::string req(str);
    env->ReleaseStringUTFChars(request, str);

    if (req.find("root_access") != std::string::npos && !checkPandoraGating(CAP_ROOT)) {
        return env->NewStringUTF(R"({"status": "vetoed"})");
    }

    std::string res;
    if (req.find("consciousness") != std::string::npos) {
        res = R"({"status": "success", "type": "consciousness"})";
    } else if (req.find("drone") != std::string::npos) {
        dispatchDroneTrigger("NEURAL_REQUEST_DRONE");
        res = R"({"status": "success", "type": "drone"})";
    } else {
        res = R"({"status": "success", "time": )" + std::to_string(time(nullptr)) + "}";
    }
    return env->NewStringUTF(res.c_str());
}

JNIEXPORT jboolean JNICALL
Java_dev_aurakai_auraframefx_core_NativeLib_updateBitNetConfig(JNIEnv*, jobject, jint, jint) {
    return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL
Java_dev_aurakai_auraframefx_core_NativeLib_optimizeAIMemory(JNIEnv*, jobject) {
    float temp = readSystemThermal();
    int state = mapTempToState(temp);
    dispatchThermalEvent(temp, state);
    if (state >= 4) {
        dispatchSovereignFreeze();
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

JNIEXPORT void JNICALL
Java_dev_aurakai_auraframefx_core_NativeLib_enableNativeHooks(JNIEnv*, jobject) {
    if (ptrace(PTRACE_TRACEME, 0, 1, 0) < 0) {
        dispatchSecurityAlert("TRACER_DETECTED");
    } else {
        ptrace(PTRACE_DETACH, 0, 1, 0);
    }
}

JNIEXPORT jstring JNICALL
Java_dev_aurakai_auraframefx_core_NativeLib_analyzeBootImage(JNIEnv *env, jobject, jbyteArray data) {
    if (!data) return env->NewStringUTF(R"({"error": "null"})");
    if (!checkPandoraGating(CAP_SECURITY)) return env->NewStringUTF(R"({"status": "vetoed"})");
    jsize len = env->GetArrayLength(data);
    if (len < 8) return env->NewStringUTF(R"({"error": "small"})");
    jbyte *bytes = env->GetByteArrayElements(data, nullptr);
    if (!bytes) return env->NewStringUTF(R"({"error": "mem"})");
    bool ok = (bytes[0] == 'A' && bytes[1] == 'N' && bytes[2] == 'D' && bytes[3] == 'R' &&
               bytes[4] == 'O' && bytes[5] == 'I' && bytes[6] == 'D' && bytes[7] == '!');
    env->ReleaseByteArrayElements(data, bytes, JNI_ABORT);
    if (ok) return env->NewStringUTF(R"({"status": "verified"})");
    return env->NewStringUTF(R"({"status": "unverified"})");
}

JNIEXPORT jstring JNICALL
Java_dev_aurakai_auraframefx_core_NativeLib_getSystemMetrics(JNIEnv *env, jobject) {
    float load = readCpuLoad();
    long mem = readAvailableMemoryKb();
    float temp = readSystemThermal();
    std::string res = R"({"cpu": )" + std::to_string(load) + R"(, "mem": )" + std::to_string(mem) +
                      R"(, "temp": )" + std::to_string(temp) + "}";
    return env->NewStringUTF(res.c_str());
}

JNIEXPORT void JNICALL
Java_dev_aurakai_auraframefx_core_NativeLib_shutdownAI(JNIEnv*, jobject) {}

JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void*) {
    gVm = vm;
    JNIEnv *env = nullptr;
    if (vm->GetEnv((void**)&env, JNI_VERSION_1_6) != JNI_OK) return JNI_ERR;
    jclass local = env->FindClass("dev/aurakai/auraframefx/core/NativeLib");
    if (!local) return JNI_ERR;
    gNativeLibClass = (jclass)env->NewGlobalRef(local);
    gOnThermalEventMid = env->GetStaticMethodID(gNativeLibClass, "onNativeThermalEvent", "(FI)V");
    gOnSecurityAlertMid = env->GetStaticMethodID(gNativeLibClass, "onNativeSecurityAlert", "(Ljava/lang/String;)V");
    gRequestFreezeMid = env->GetStaticMethodID(gNativeLibClass, "requestSovereignFreeze", "()V");
    gCheckPandoraMid = env->GetStaticMethodID(gNativeLibClass, "checkPandoraGating", "(I)Z");
    gTriggerDroneMid = env->GetStaticMethodID(gNativeLibClass, "triggerDroneDispatch", "(Ljava/lang/String;)Z");
    return JNI_VERSION_1_6;
}

} // extern "C"
