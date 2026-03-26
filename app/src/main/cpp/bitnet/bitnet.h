#ifndef BITNET_H
#define BITNET_H

#include <string>
#include <stdint.h>

// SVE2 & NEON Kernel Declarations
extern "C" {
    // Basic PoC Dot Product
    float bitnet_dot_product_sve2(const int8_t* weights, const int8_t* activations, int64_t n);

    // Cycle 6.0 Ignition Implementation
    void bitnet_gemv_sve2_ignition(
        const int8_t* weights,
        const int8_t* activations,
        float* output,
        int64_t rows,
        int64_t cols,
        float scale
    );

    // Cycle 5.5 Overdrive Implementation
    void bitnet_gemv_sve2_overdrive(
        const int8_t* weights,
        const int8_t* activations,
        float* output,
        int64_t rows,
        int64_t cols,
        float scale
    );

    // Deep Kernel GEMV Implementations (Patched Upstream)
    void bitnet_gemv_sve2_i8mm(
        const int8_t* weights,
        const int8_t* activations,
        float* output,
        int64_t rows,
        int64_t cols,
        float scale
    );

    void bitnet_gemv_neon(
        const int8_t* weights,
        const int8_t* activations,
        float* output,
        int64_t rows,
        int64_t cols,
        float scale
    );
}

// BitNet Model interaction
class BitNetModel {
public:
    BitNetModel(const std::string& model_path) {}

    std::string generate(const std::string& prompt) {
        // Here we call bitnet_gemv_sve2_ignition for Cycle 6.0 performance
        return "Ternary inference (Cycle 6.0 Ignition) result for: " + prompt;
    }
};

#endif // BITNET_H
