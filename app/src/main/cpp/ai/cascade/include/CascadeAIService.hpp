#pragma once

#include <jni.h>
#include <string>
#include <memory>

namespace genesis {
    namespace cascade {

/**
 * @brief Cascade AI Service native implementation
 * 
 * This class provides the native implementation of the Cascade AI agent,
 * which coordinates between Aura and Kai AI services for state management
 * and context processing.
 */
        class CascadeAIService {
        public:
            CascadeAIService();

            ~CascadeAIService();

            // Prevent copying
            CascadeAIService(const CascadeAIService &) = delete;

            CascadeAIService &operator=(const CascadeAIService &) = delete;

            /**
             * @brief Process an AI request through the Cascade agent
             *
             * @param env JNI environment pointer
             * @param request The AI request to process
             * @return jstring JSON-encoded response from the AI agent
             */
            jstring processRequest(JNIEnv *env, const std::string &request);

            /**
             * @brief Initialize the Cascade AI service
             *
             * @param vm JavaVM pointer for JNI operations
             * @param context Android context
             * @return true if initialization was successful
             */
            bool initialize(JavaVM *vm, jobject context);

            /**
             * @brief Shut down the Cascade AI service
             */
            void shutdown();

        private:
            class Impl;

            std::unique_ptr<Impl> pImpl_;
        };

    } // namespace cascade
} // namespace genesis

// JNI function declarations
extern "C" {
JNIEXPORT jboolean JNICALL
Java_dev_aurakai_auraframefx_ai_services_CascadeAIService_nativeInitialize(
        JNIEnv *env,
        jobject thiz,
        jobject context
);

JNIEXPORT jstring JNICALL
Java_dev_aurakai_auraframefx_ai_services_CascadeAIService_nativeProcessRequest(
        JNIEnv *env,
        jobject thiz,
        jstring request
);

JNIEXPORT void JNICALL
Java_dev_aurakai_auraframefx_ai_services_CascadeAIService_nativeShutdown(
        JNIEnv *env,
        jobject thiz
);
}
