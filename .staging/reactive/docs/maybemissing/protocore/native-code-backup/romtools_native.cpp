#include <jni.h>
#include <android/log.h>

#define LOG_TAG "ROMTools-Native"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

extern "C" {

/**
 * @brief Return the native library version string.
 *
 * Returns a new Java UTF string containing the native ROM Tools version literal "1.0.0-genesis".
 * Also emits an info-level initialization message to the Android log.
 *
 * @return jstring New Java string with the version ("1.0.0-genesis").
 */
JNIEXPORT jstring
JNICALL
Java_dev_aurakai_auraframefx_romtools_ROMToolsNative_getVersion(JNIEnv *env, jobject /* this */) {
    LOGI("ROM Tools Native Library initialized");
    return env->NewStringUTF("1.0.0-genesis");
}

/**
 * @brief Placeholder native entry that "analyzes" a boot image at the given path.
 *
 * Converts the provided Java string `path` to a C string, logs the path, releases
 * the converted string, and returns success. This is a stub implementation; real
 * analysis is not performed.
 *
 * @param path Java string containing the filesystem path to the boot image.
 * @return jboolean Always returns JNI_TRUE to indicate success in this placeholder.
 */
JNIEXPORT jboolean
JNICALL
Java_dev_aurakai_auraframefx_romtools_ROMToolsNative_analyzeBootImage(JNIEnv *env,
                                                                      jobject /* this */,
                                                                      jstring path) {
    const char *bootPath = env->GetStringUTFChars(path, 0);
    LOGI("Analyzing boot image: %s", bootPath);
    env->ReleaseStringUTFChars(path, bootPath);

    // TODO: Implement actual boot image analysis
    return JNI_TRUE;
}

/**
 * @brief Placeholder native method that "mounts" a partition by name.
 *
 * Converts the provided Java string to a UTF-8 C string, logs the partition name,
 * releases the UTF-8 chars, and returns success. This function is a stub and
 * does not perform any real mounting; it always returns JNI_TRUE.
 *
 * @param partition Java string containing the partition name or path.
 * @return jboolean Always returns JNI_TRUE (placeholder success).
 */
JNIEXPORT jboolean
JNICALL
Java_dev_aurakai_auraframefx_romtools_ROMToolsNative_mountPartition(JNIEnv *env, jobject /* this */,
                                                                    jstring partition) {
    const char *partName = env->GetStringUTFChars(partition, 0);
    LOGI("Mounting partition: %s", partName);
    env->ReleaseStringUTFChars(partition, partName);

    // TODO: Implement actual partition mounting
    return JNI_TRUE;
}

}
