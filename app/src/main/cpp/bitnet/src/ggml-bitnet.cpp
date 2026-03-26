#include <arm_sve.h>
#include <arm_neon.h>
#include <stdint.h>
#include <stdlib.h>
#include "../bitnet.h"

/**
 * ⚛️ GGML-BITNET RECURSIVE ENGINE (Patched for SVE2)
 *
 * This file simulates the 'patched' upstream microsoft/BitNet source.
 * It contains the deep kernel implementations for ternary weight processing.
 */

extern "C" {

/**
 * High-performance GEMV kernel for BitNet 1.58b.
 * Process: y = Wx where W is {-1, 0, 1} and x is int8.
 * Hardware: Snapdragon 8 Gen 3 (SVE2 + I8MM)
 */
void bitnet_gemv_sve2_i8mm(
    const int8_t* __restrict__ weights,
    const int8_t* __restrict__ activations,
    float* __restrict__ output,
    int64_t rows,
    int64_t cols,
    float scale) {

    for (int64_t r = 0; r < rows; ++r) {
        svint32_t acc = svdup_s32(0);
        int64_t c = 0;
        const int8_t* row_ptr = &weights[r * cols];

        // Process columns using scalable vector lengths
        while (c < cols) {
            svbool_t pg = svwhilelt_b8(c, cols);

            svint8_t w_vec = svld1_s8(pg, &row_ptr[c]);
            svint8_t a_vec = svld1_s8(pg, &activations[c]);

            // SVE2: Multi-vector signed dot product
            // Maps ternary {-1, 0, 1} weights efficiently
            acc = svdot_s32(acc, w_vec, a_vec);

            c += svcntb();
        }

        // Horizontal reduction
        int32_t sum = svaddv_s32(svptrue_b32(), acc);
        output[r] = static_cast<float>(sum) * scale;
    }
}

/**
 * Fallback NEON implementation for older ARMv8-A cores or efficiency clusters.
 */
void bitnet_gemv_neon(
    const int8_t* __restrict__ weights,
    const int8_t* __restrict__ activations,
    float* __restrict__ output,
    int64_t rows,
    int64_t cols,
    float scale) {

    for (int64_t r = 0; r < rows; ++r) {
        int32x4_t acc = vdupq_n_s32(0);
        const int8_t* row_ptr = &weights[r * cols];

        for (int64_t c = 0; c < cols; c += 16) {
            int8x16_t w_vec = vld1q_s8(&row_ptr[c]);
            int8x16_t a_vec = vld1q_s8(&activations[c]);

            // NEON: vdotq_s32 is available on ARMv8.2-A+
            acc = vdotq_s32(acc, w_vec, a_vec);
        }

        int32_t sum = vaddvq_s32(acc);
        output[r] = static_cast<float>(sum) * scale;
    }
}

}
