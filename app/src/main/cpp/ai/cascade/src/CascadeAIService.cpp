#include "CascadeAIService.hpp"
#include <android/log.h>
#include <string>
#include <memory>
#include <jni.h>

#define LOG_TAG "CascadeAI-Native"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)


    namespace genesis::cascade {

        class CascadeAIService::Impl {
        public:
            Impl() = default;

            ~Impl() = default;

            bool initialize(JavaVM *vm, jobject context) {
                LOGI("Initializing Cascade AI Service");
                // Store the JavaVM for later use
                jvm_ = vm;

                // Get the JNI environment
                JNIEnv *env = nullptr;
                if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
                    LOGE("Failed to get JNI environment");
                    return false;
                }

                // Store global reference to the context
                if (context != nullptr) {
                    jclass contextClass = env->GetObjectClass(context);
                    if (contextClass == nullptr) {
                        LOGE("Failed to get context class");
                        return false;
                    }
                    context_ = env->NewGlobalRef(context);
                }

                LOGI("Cascade AI Service initialized successfully");
                return true;
            }

            void shutdown() {
                LOGI("Shutting down Cascade AI Service");

                // Release global references
                JNIEnv *env = nullptr;
                if (jvm_ != nullptr) {
                    jvm_->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6);
                    if (env != nullptr && context_ != nullptr) {
                        env->DeleteGlobalRef(context_);
                        context_ = nullptr;
                    }
                }
            }

            static jstring processRequest(JNIEnv *env, const std::string &request);

        private:
            JavaVM *jvm_ = nullptr;
            jobject context_ = nullptr;
        };

        jstring CascadeAIService::Impl::processRequest(JNIEnv *env, const std::string &request) {
            LOGI("Processing request: %s", request.c_str());

            // TODO: Implement actual request processing logic
            // For now, just return a simple response
            std::string response = R"({
            "status": "success",
            "agent": "Cascade",
            "version": "1.0.0",
            "response": "Request processed by Cascade AI agent"
        })";

            return env->NewStringUTF(response.c_str());
        }

// CascadeAIService implementation
        CascadeAIService::CascadeAIService() : pImpl_(std::make_unique<Impl>()) {}

        CascadeAIService::~CascadeAIService() = default;

        bool CascadeAIService::initialize(JavaVM *vm, jobject context) {
            if (pImpl_) {
                return pImpl_->initialize(vm, context);
            } else {
                return false;
            }
        }

        void CascadeAIService::shutdown() {
            if (pImpl_) {
                pImpl_->shutdown();
            }
        }

        jstring CascadeAIService::processRequest(JNIEnv *env, const std::string &request) {
            return pImpl_ ? pImpl_->processRequest(env, request) : nullptr;
        }

    } // namespace genesis::cascade


// JNI Implementation
namespace {
    std::unique_ptr<genesis::cascade::CascadeAIService> g_cascadeService;
    JavaVM *g_vm = nullptr;
} // anonymous namespace

// JNI Methods
extern "C" {

JNIEXPORT jboolean JNICALL
Java_dev_aurakai_auraframefx_ai_services_CascadeAIService_nativeInitialize(
        JNIEnv *env,
        jobject /* thiz */,
        jobject context
) {
    if (g_cascadeService) {
        LOGI("Cascade AI Service already initialized");
        return JNI_TRUE;
    }

    if (env->GetJavaVM(&g_vm) != JNI_OK) {
        LOGE("Failed to get JavaVM");
        return JNI_FALSE;
    }

    g_cascadeService = std::make_unique<genesis::cascade::CascadeAIService>();
    if (!g_cascadeService) {
        LOGE("Failed to create Cascade AI Service");
        return JNI_FALSE;
    }

    jobject globalContext = env->NewGlobalRef(context);
    if (!g_cascadeService->initialize(g_vm, globalContext)) {
        LOGE("Failed to initialize Cascade AI Service");
        g_cascadeService.reset();
        return JNI_FALSE;
    }

    LOGI("Cascade AI Service initialized successfully");
    return JNI_TRUE;
}

JNIEXPORT jstring JNICALL
Java_dev_aurakai_auraframefx_ai_services_CascadeAIService_nativeProcessRequest(
        JNIEnv *env,
        jobject /* thiz */,
        jstring request
) {
    if (!g_cascadeService) {
        LOGE("Cascade AI Service not initialized");
        return env->NewStringUTF(R"({"error":"Service not initialized"})");
    }

    const char *requestStr = env->GetStringUTFChars(request, nullptr);
    if (!requestStr) {
        LOGE("Failed to get request string");
        return env->NewStringUTF(R"({"error":"Invalid request"})");
    }

    std::string requestCpp(requestStr);
    env->ReleaseStringUTFChars(request, requestStr);

    return g_cascadeService->processRequest(env, requestCpp);
}

JNIEXPORT void JNICALL
Java_dev_aurakai_auraframefx_ai_services_CascadeAIService_nativeShutdown(
        JNIEnv * /* env */,
        jobject /* thiz */
) {
    if (g_cascadeService) {
        g_cascadeService->shutdown();
        g_cascadeService.reset();
    }

    if (g_vm) {
        g_vm = nullptr;
    }

    LOGI("Cascade AI Service shutdown complete");
}

} // extern "C"
