#include <jni.h>
#include <string>
#include <sched.h>
#include <android/log.h>
#include <fcntl.h>
#include <unistd.h>
#include <cstdlib>
#include <cstring>
#include "bitnet.h"

#define LOG_TAG "ThermalSentinel"
#define JNI_TAG "BitNetJNI"

// Static instance for the session
static BitNetModel* model = nullptr;

/**
 * Returns temperature in Celsius from the specified sysfs zone.
 * Snapdragon 8 Gen 3 thermal values are usually in millidegrees.
 */
static float readThermalSubstrate(const char* zonePath) {
    if (zonePath == nullptr) return -1.0f;

    int fd = open(zonePath, O_RDONLY);
    if (fd < 0) {
        __android_log_print(ANDROID_LOG_WARN, LOG_TAG, "Failed to open %s", zonePath);
        return -1.0f;
    }

    char buffer[64];
    ssize_t bytes = read(fd, buffer, sizeof(buffer) - 1);
    close(fd);

    if (bytes > 0) {
        buffer[bytes] = '\0';
        char* end;
        long millideg = strtol(buffer, &end, 10);
        if (end != buffer) {
            return static_cast<float>(millideg) / 1000.0f;
        }
    }
    return -1.0f;
}

extern "C" {

JNIEXPORT jfloat JNICALL
Java_dev_aurakai_auraframefx_domains_genesis_BitNetLocalService_readThermalZone(
    JNIEnv* env,
    jobject /* thiz */,
    jstring zonePath) {
    if (zonePath == nullptr) return -1.0f;
    const char* path = env->GetStringUTFChars(zonePath, nullptr);
    float temp = readThermalSubstrate(path);
    env->ReleaseStringUTFChars(zonePath, path);
    return temp;
}

JNIEXPORT jstring JNICALL
Java_dev_aurakai_auraframefx_domains_genesis_BitNetLocalService_getThermalZoneType(
    JNIEnv* env,
    jobject /* thiz */,
    jint zoneId) {
    char path[128];
    snprintf(path, sizeof(path), "/sys/class/thermal/thermal_zone%d/type", zoneId);

    int fd = open(path, O_RDONLY);
    if (fd < 0) return nullptr;

    char buffer[128];
    ssize_t bytes = read(fd, buffer, sizeof(buffer) - 1);
    close(fd);

    if (bytes > 0) {
        while (bytes > 0 && (buffer[bytes - 1] == '\n' || buffer[bytes - 1] == '\r')) {
            bytes--;
        }
        buffer[bytes] = '\0';
        return env->NewStringUTF(buffer);
    }
    return nullptr;
}

JNIEXPORT jstring JNICALL
Java_dev_aurakai_auraframefx_domains_genesis_BitNetLocalService_generateLocalResponse(
    JNIEnv* env,
    jobject /* this */,
    jstring j_prompt,
    jint nThreads) {

    // Lazy initialization of the model
    if (!model) {
        // Path would be injected or configured in a real scenario
        model = new BitNetModel("/sdcard/models/bitnet-100b.gguf");

        // Thermal/Performance Optimization: Pin to Big Cores (e.g., cores 4-7 on Snapdragon)
        cpu_set_t set;
        CPU_ZERO(&set);
        for (int i = 4; i < 8; ++i) {
            CPU_SET(i, &set);
        }

        if (sched_setaffinity(0, sizeof(set), &set) < 0) {
            __android_log_print(ANDROID_LOG_WARN, JNI_TAG, "Failed to set CPU affinity");
        } else {
            __android_log_print(ANDROID_LOG_INFO, JNI_TAG, "Pinned thread to big cores (4-7) with target %d threads", nThreads);
        }
    }

    const char* prompt_cstr = env->GetStringUTFChars(j_prompt, nullptr);
    if (!prompt_cstr) {
        return nullptr;
    }

    std::string prompt(prompt_cstr);

    // Perform Inference
    std::string response = model->generate(prompt);

    env->ReleaseStringUTFChars(j_prompt, prompt_cstr);

    return env->NewStringUTF(response.c_str());
}
}
