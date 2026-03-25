#include <jni.h>
#include <string>
#include <sched.h>
#include <android/log.h>
#include <fcntl.h>
#include <unistd.h>
#include <stdlib.h>
#include "bitnet.h"

#define TAG "BitNetJNI"

// Static instance for the session
static BitNetModel* model = nullptr;

/**
 * Returns temperature in Celsius from the specified sysfs zone.
 * Snapdragon 8 Gen 3 thermal values are usually in millidegrees.
 */
static float readThermalSubstrate(const char* zonePath) {
    int fd = open(zonePath, O_RDONLY);
    if (fd < 0) {
        return -1.0f;
    }

    char buffer[32];
    ssize_t bytes = read(fd, buffer, sizeof(buffer) - 1);
    close(fd);

    if (bytes > 0) {
        buffer[bytes] = '\0';
        long millideg = strtol(buffer, nullptr, 10);
        return millideg / 1000.0f;
    }
    return -1.0f;
}

extern "C" {

JNIEXPORT jfloat JNICALL
Java_dev_aurakai_auraframefx_domains_genesis_BitNetLocalService_readThermalZone(
    JNIEnv* env,
    jobject /* this */,
    jstring zonePath) {
    const char* path = env->GetStringUTFChars(zonePath, nullptr);
    float temp = readThermalSubstrate(path);
    env->ReleaseStringUTFChars(zonePath, path);
    return temp;
}

JNIEXPORT jstring JNICALL
Java_dev_aurakai_auraframefx_domains_genesis_BitNetLocalService_generateLocalResponse(
    JNIEnv* env,
    jobject /* this */,
    jstring j_prompt) {

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
            __android_log_print(ANDROID_LOG_WARN, TAG, "Failed to set CPU affinity");
        } else {
            __android_log_print(ANDROID_LOG_INFO, TAG, "Pinned thread to big cores (4-7)");
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
