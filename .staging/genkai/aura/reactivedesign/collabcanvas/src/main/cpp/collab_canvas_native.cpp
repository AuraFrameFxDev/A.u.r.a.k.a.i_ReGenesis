#include <jni.h>
#include <android/log.h>

#define LOG_TAG "CollabCanvas-Native"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

extern "C" {

/**
 * @brief Return the Collab Canvas native library version.
 *
 * Creates and returns a new Java UTF string containing the library version
 * identifier "1.0.0-genesis-canvas".
 *
 * @return jstring Java string with the native library version.
 */
JNIEXPORT jstring
JNICALL
Java_dev_aurakai_auraframefx_canvas_CollabCanvasNative_getVersion(JNIEnv *env, jobject /* this */) {
    LOGI("Collab Canvas Native Library initialized");
    return env->NewStringUTF("1.0.0-genesis-canvas");
}

/**
 * @brief Initialize the collaborative canvas subsystem.
 *
 * Performs any native setup required for the collaborative canvas and reports success.
 * Currently the initialization logic is a placeholder and always returns success.
 *
 * @return jboolean JNI_TRUE if initialization succeeded (currently always JNI_TRUE).
 */
JNIEXPORT jboolean
JNICALL
Java_dev_aurakai_auraframefx_canvas_CollabCanvasNative_initializeCanvas(JNIEnv *env,
                                                                        jobject /* this */) {
    LOGI("Initializing collaborative canvas");
    // TODO: Implement canvas initialization
    return JNI_TRUE;
}

/**
 * @brief Processes a collaboration payload received from Java.
 *
 * Converts the provided Java string to a UTF-8 C string, releases the UTF-8 memory,
 * and (planned) performs collaborative-processing logic on the payload.
 *
 * @param data UTF-8 encoded collaboration payload from the Java caller.
 * @return jboolean JNI_TRUE on success (currently always returns success).
 */
JNIEXPORT jboolean
JNICALL
Java_dev_aurakai_auraframefx_canvas_CollabCanvasNative_processCollaboration(JNIEnv *env,
                                                                            jobject /* this */,
                                                                            jstring data) {
    const char *collabData = env->GetStringUTFChars(data, 0);
    LOGI("Processing collaboration data: %s", collabData);
    env->ReleaseStringUTFChars(data, collabData);

    // TODO: Implement collaboration processing
    return JNI_TRUE;
}

}
