#include <jni.h>
#include <android/log.h>
#include <string>

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "OracleDriveNative", __VA_ARGS__)

extern "C" {

/**
 * @brief Perform native shutdown for the Oracle Drive integration.
 *
 * Called from Java to release native resources and perform any cleanup required by the Oracle Drive integration.
 */
JNIEXPORT void JNICALL
Java_dev_aurakai_auraframefx_oracledriveintegration_OracleDriveNative_shutdown(
        JNIEnv *env,
        jobject /* this */) {

    LOGI("Shutting down Oracle Drive Integration...");
    // Cleanup Oracle Drive native components
}

} // extern "C"