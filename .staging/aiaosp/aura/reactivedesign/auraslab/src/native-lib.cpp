#include <jni.h>
#include <string>
#include <android/log.h>

#define LOG_TAG "native-lib"
#define ALOG(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

extern "C" [[maybe_unused]] JNIEXPORT jstring

JNICALL
Java_dev_aurakai_auraframefx_sandboxui_NativeBridge_stringFromNative(JNIEnv *env,
                                                                     jobject /* this */) {
    std::string hello = "Hello from native code";
    ALOG("%s", hello.c_str());
    return env->NewStringUTF(hello.c_str());
}

