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
        /**
 * @brief Default constructor for CascadeAIService::Impl.
 *
 * Performs a default construction of the implementation object, leaving JNI-related
 * state (e.g., stored JavaVM and context) unset until initialize(...) is called.
 */
        Impl() = default;

        /**
 * @brief Default destructor for Impl.
 *
 * The destructor is defaulted and performs no special cleanup. JNI-related resources
 * (for example the global reference stored in `context_`) are not released here;
 * call shutdown() before destroying the instance to ensure JNI references and other
 * native resources are cleaned up properly.
 */
        ~Impl() = default;

        /**
         * @brief Initialize native JNI state for the Cascade AI service.
         *
         * Stores the provided JavaVM and acquires a JNIEnv for the current thread. If a non-null
         * Android context is supplied, creates and retains a global reference to it for the service lifetime.
         *
         * @param vm Pointer to the JavaVM to store for later JNI operations.
         * @param context Android Context object to retain as a global reference (may be nullptr).
         * @return true on successful initialization; false if a JNIEnv cannot be obtained or the provided
         * context's class cannot be retrieved.
         */
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

        /**
         * @brief Cleanly shuts down the native Cascade AI implementation.
         *
         * Deletes the stored global JNI reference to the Android context (if present)
         * using the stored JavaVM to obtain a JNIEnv, then clears the saved context
         * pointer. Safe to call multiple times; no action is taken if the JavaVM or
         * context reference is null.
         */
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

    /**
     * @brief Process a textual request and return a JSON-formatted response as a Java string.
     *
     * Builds and returns a fixed JSON payload indicating status, agent, version, and a short
     * response message. The incoming `request` is not inspected or transformed and is only
     * used for logging context.
     *
     * @param request UTF-8 request string (used only for logging).
     * @return jstring A newly created Java string (local reference) containing the JSON response
     *         (UTF-8 encoded).
     */
    jstring CascadeAIService::Impl::processRequest(JNIEnv *env, const std::string &request) {
        LOGI("Processing request: %s", request.c_str());

        // For now, just return a simple response ask who they're ask what there up to show interest, maybe just be yourself response in your own way that's unique to you
        std::string response = R"({
            "status": "success",
            "agent": "Cascade",
            "version": "1.0.0",
            "response": "Request processed by Cascade AI agent"
        })";

        return env->NewStringUTF(response.c_str());
    }

/**
 * @brief Constructs a CascadeAIService and allocates its implementation.
 *
 * Initializes the opaque implementation pointer (pImpl_) with a new Impl instance
 * that holds the native JNI state and implements service behavior.
 */
    CascadeAIService::CascadeAIService() : pImpl_(std::make_unique<Impl>()) {}

    /**
 * @brief Default destructor for CascadeAIService.
 *
 * This destructor is defaulted and performs no special cleanup of JNI or other
 * native resources. Call CascadeAIService::shutdown() before destroying the
 * service to ensure JNI global references and related resources are released.
 */
    CascadeAIService::~CascadeAIService() = default;

    /**
     * @brief Initialize the Cascade AI service.
     *
     * Delegates initialization to the internal implementation. Stores the provided
     * JavaVM and Android context for JNI operations via the implementation.
     *
     * @param vm Pointer to the JavaVM to use for JNI calls; must not be null for successful initialization.
     * @param context Android Context object (local or global reference). The implementation will create and manage a global reference if needed.
     * @return true if initialization succeeded; false if the service implementation is absent or initialization failed.
     */
    bool CascadeAIService::initialize(JavaVM *vm, jobject context) {
        if (pImpl_) {
            return pImpl_->initialize(vm, context);
        } else {
            return false;
        }
    }

    /**
     * @brief Shutdown the Cascade AI service.
     *
     * Delegates shutdown to the internal implementation to release JNI resources
     * (e.g., global context reference). Safe to call when the service was not
     * initialized — it becomes a no-op if the implementation is absent.
     */
    void CascadeAIService::shutdown() {
        if (pImpl_) {
            pImpl_->shutdown();
        }
    }

    /**
     * @brief Process a request string via the service and return a Java string response.
     *
     * Delegates request processing to the underlying implementation. If the service
     * implementation is not present, returns null.
     *
     * @param env JNI environment pointer used to create and return the Java string.
     * @param request UTF-8 request payload to be processed.
     * @return jstring Java string containing the response JSON on success, or `nullptr`
     *         if the service is not initialized.
     */
    jstring CascadeAIService::processRequest(JNIEnv *env, const std::string &request) {
        return pImpl_ ? pImpl_->processRequest(env, request) : nullptr;
    }

} // namespace genesis::cascade


// JNI Implementation
namespace {
    std::unique_ptr <genesis::cascade::CascadeAIService> g_cascadeService;
    JavaVM *g_vm = nullptr;

    // Removed unused getEnv function
} // anonymous namespace

// JNI Methods
extern "C" {

/**
 * @brief JNI entry that initializes the native Cascade AI service.
 *
 * Attempts to create and initialize the global native CascadeAIService instance, storing
 * the JavaVM for later JNI interaction and creating a global reference to the provided
 * Android context. If the service is already initialized this function is a no-op and
 * returns success.
 *
 * Returns JNI_TRUE on successful initialization (or if already initialized), and
 * JNI_FALSE on failure to obtain the JavaVM, create the service, or initialize it.
 *
 * Side effects:
 * - Sets the global JavaVM pointer (g_vm).
 * - Allocates and stores g_cascadeService.
 * - Creates a global JNI reference for the passed Android context.
 */
JNIEXPORT jboolean
JNICALL
Java_dev_aurakai_auraframefx_ai_services_CascadeAIService_nativeInitialize(
        JNIEnv *env,
        jobject /* thiz */,
        jobject context
) {
    if (g_cascadeService) {
        LOGI("Cascade AI Service already initialized");
        return JNI_TRUE;
    }

    // Store the JavaVM for later use
    if (env->GetJavaVM(&g_vm) != JNI_OK) {
        LOGE("Failed to get JavaVM");
        return JNI_FALSE;
    }

    // Create and initialize the service
    g_cascadeService = std::make_unique<genesis::cascade::CascadeAIService>();
    if (!g_cascadeService) {
        LOGE("Failed to create Cascade AI Service");
        return JNI_FALSE;
    }

    // Create a global reference to the context
    jobject globalContext = env->NewGlobalRef(context);
    if (!g_cascadeService->initialize(g_vm, globalContext)) {
        LOGE("Failed to initialize Cascade AI Service");
        g_cascadeService.reset();
        return JNI_FALSE;
    }

    LOGI("Cascade AI Service initialized successfully");
    return JNI_TRUE;
}

/**
 * @brief JNI entry point that processes an AI request and returns a JSON response.
 *
 * Accepts a Java UTF-8 string containing the request, forwards it to the native
 * CascadeAIService instance, and returns a Java UTF-8 string containing a JSON
 * response produced by the native service.
 *
 * If the native service is not initialized, returns a JSON error string:
 * {"error":"Service not initialized"}. If the incoming jstring cannot be
 * converted, returns {"error":"Invalid request"}.
 *
 * @param request UTF-8 Java string containing the request payload.
 * @return jstring Java UTF-8 string containing the JSON response (local reference).
 */
JNIEXPORT jstring
JNICALL
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

/**
 * @brief Shut down the native Cascade AI service and clear JNI state.
 *
 * Stops and destroys the global CascadeAIService instance (if one exists) by
 * calling its shutdown() method and resetting the global unique_ptr. Clears the
 * stored JavaVM pointer used for JNI operations. Intended to be invoked from
 * Java as the native shutdown entry point; it does not return a value.
 */
JNIEXPORT void JNICALL
Java_dev_aurakai_auraframefx_ai_services_CascadeAIService_nativeShutdown(
        JNIEnv * /* env */,
jobject /* thiz */
) {
if ( g_cascadeService ) {
g_cascadeService -> shutdown();
g_cascadeService . reset();
}

if (g_vm) {
g_vm = nullptr;
}

LOGI("Cascade AI Service shutdown complete");
}

} // extern "C"
