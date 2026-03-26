#ifndef BITNET_H
#define BITNET_H

#include <string>
#include <stdint.h>

// SVE2 & NEON Kernel Declarations
extern "C" {
    // Basic PoC Dot Product
    float bitnet_dot_product_sve2(const int8_t* weights, const int8_t* activations, int64_t n);

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
        // Here we would call bitnet_gemv_sve2_i8mm for matrix multiplication
        return "Ternary inference (SVE2 + I8MM Optimized) result for: " + prompt;
    }
};

#endif // BITNET_H
