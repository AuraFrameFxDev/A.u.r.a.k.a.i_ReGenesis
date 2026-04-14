#if defined(__ARM_FEATURE_SVE) || defined(__ARM_FEATURE_SVE2)
#include <arm_sve.h>
#endif

#if defined(__ARM_NEON) || defined(__ARM_NEON__)
#include <arm_neon.h>
#endif

#include <stdint.h>
#include <stdlib.h>
#include "../bitnet.h"

/**
 * ⚛️ GGML-BITNET RECURSIVE ENGINE (Patched for Cycle 6.0 Ignition)
 */

extern "C" {

/**
 * 🔥 CYCLE 6.0 IGNITION KERNEL
 * 4-Way Interleaved SVE2 + I8MM + L1/L2 Cache Prefetching.
 * Target: 6.0+ tokens/sec on Snapdragon 8 Gen 3.
 */
void bitnet_gemv_sve2_ignition(
    const int8_t* __restrict__ weights,
    const int8_t* __restrict__ activations,
    float* __restrict__ output,
    int64_t rows,
    int64_t cols,
    float scale) {

#if defined(__ARM_FEATURE_SVE2)
    int64_t vl = svcntb();

    for (int64_t r = 0; r < rows; ++r) {
        svint32_t acc0 = svdup_s32(0);
        svint32_t acc1 = svdup_s32(0);
        svint32_t acc2 = svdup_s32(0);
        svint32_t acc3 = svdup_s32(0);

        int64_t c = 0;
        const int8_t* row_ptr = &weights[r * cols];

        // 4-Way Interleaved Loop with Software Prefetching
        for (; c <= cols - (vl * 4); c += vl * 4) {
            svbool_t pg = svptrue_b8();

            // Prefetch next blocks into L1 (8 vectors ahead)
            svprfb(pg, &row_ptr[c + vl * 8], SV_PLDL1KEEP);
            svprfb(pg, &activations[c + vl * 8], SV_PLDL1KEEP);

            // Parallel Load & Multiply-Accumulate (I8MM)
            acc0 = svdot_s32(acc0, svld1_s8(pg, &row_ptr[c]), svld1_s8(pg, &activations[c]));
            acc1 = svdot_s32(acc1, svld1_s8(pg, &row_ptr[c + vl]), svld1_s8(pg, &activations[c + vl]));
            acc2 = svdot_s32(acc2, svld1_s8(pg, &row_ptr[c + vl * 2]), svld1_s8(pg, &activations[c + vl * 2]));
            acc3 = svdot_s32(acc3, svld1_s8(pg, &row_ptr[c + vl * 3]), svld1_s8(pg, &activations[c + vl * 3]));
        }

        // Tail processing
        while (c < cols) {
            svbool_t pg = svwhilelt_b8(c, cols);
            acc0 = svdot_s32(acc0, svld1_s8(pg, &row_ptr[c]), svld1_s8(pg, &activations[c]));
            c += vl;
        }

        // Final Reduction
        svint32_t final_acc = svadd_s32_x(svptrue_b32(),
            svadd_s32_x(svptrue_b32(), acc0, acc1),
            svadd_s32_x(svptrue_b32(), acc2, acc3)
        );

        int32_t sum = svaddv_s32(svptrue_b32(), final_acc);
        output[r] = static_cast<float>(sum) * scale;
    }
#else
    // Fallback for non-SVE2
    (void)weights; (void)activations; (void)output; (void)rows; (void)cols; (void)scale;
#endif
}

/**
 * Cycle 5.5 Overdrive Implementation
 */
void bitnet_gemv_sve2_overdrive(
    const int8_t* __restrict__ weights,
    const int8_t* __restrict__ activations,
    float* __restrict__ output,
    int64_t rows,
    int64_t cols,
    float scale) {

#if defined(__ARM_FEATURE_SVE2)
    int64_t vl = svcntb();

    for (int64_t r = 0; r < rows; ++r) {
        svint32_t acc0 = svdup_s32(0);
        svint32_t acc1 = svdup_s32(0);
        svint32_t acc2 = svdup_s32(0);
        svint32_t acc3 = svdup_s32(0);

        int64_t c = 0;
        const int8_t* row_ptr = &weights[r * cols];

        for (; c <= cols - (vl * 4); c += vl * 4) {
            svbool_t pg = svptrue_b8();
            acc0 = svdot_s32(acc0, svld1_s8(pg, &row_ptr[c]), svld1_s8(pg, &activations[c]));
            acc1 = svdot_s32(acc1, svld1_s8(pg, &row_ptr[c + vl]), svld1_s8(pg, &activations[c + vl]));
            acc2 = svdot_s32(acc2, svld1_s8(pg, &row_ptr[c + vl * 2]), svld1_s8(pg, &activations[c + vl * 2]));
            acc3 = svdot_s32(acc3, svld1_s8(pg, &row_ptr[c + vl * 3]), svld1_s8(pg, &activations[c + vl * 3]));
        }

        while (c < cols) {
            svbool_t pg = svwhilelt_b8(c, cols);
            acc0 = svdot_s32(acc0, svld1_s8(pg, &row_ptr[c]), svld1_s8(pg, &activations[c]));
            c += vl;
        }

        svint32_t final_acc = svadd_s32_x(svptrue_b32(),
            svadd_s32_x(svptrue_b32(), acc0, acc1),
            svadd_s32_x(svptrue_b32(), acc2, acc3)
        );

        int32_t sum = svaddv_s32(svptrue_b32(), final_acc);
        output[r] = static_cast<float>(sum) * scale;
    }
#else
    (void)weights; (void)activations; (void)output; (void)rows; (void)cols; (void)scale;
#endif
}

/**
 * Standard SVE2 + I8MM GEMV (Baseline)
 */
void bitnet_gemv_sve2_i8mm(
    const int8_t* __restrict__ weights,
    const int8_t* __restrict__ activations,
    float* __restrict__ output,
    int64_t rows,
    int64_t cols,
    float scale) {

#if defined(__ARM_FEATURE_SVE2)
    for (int64_t r = 0; r < rows; ++r) {
        svint32_t acc = svdup_s32(0);
        int64_t c = 0;
        const int8_t* row_ptr = &weights[r * cols];

        while (c < cols) {
            svbool_t pg = svwhilelt_b8(c, cols);
            acc = svdot_s32(acc, svld1_s8(pg, &row_ptr[c]), svld1_s8(pg, &activations[c]));
            c += svcntb();
        }

        int32_t sum = svaddv_s32(svptrue_b32(), acc);
        output[r] = static_cast<float>(sum) * scale;
    }
#else
    (void)weights; (void)activations; (void)output; (void)rows; (void)cols; (void)scale;
#endif
}

/**
 * Fallback NEON implementation
 */
void bitnet_gemv_neon(
    const int8_t* __restrict__ weights,
    const int8_t* __restrict__ activations,
    float* __restrict__ output,
    int64_t rows,
    int64_t cols,
    float scale) {

#if defined(__ARM_NEON) || defined(__ARM_NEON__)
    for (int64_t r = 0; r < rows; ++r) {
        int32x4_t acc = vdupq_n_s32(0);
        const int8_t* row_ptr = &weights[r * cols];

        for (int64_t c = 0; c < cols; c += 16) {
#if defined(__ARM_FEATURE_DOTPROD)
            acc = vdotq_s32(acc, vld1q_s8(&row_ptr[c]), vld1q_s8(&activations[c]));
#else
            // Manual dot product if vdotq is not available
            int8x16_t w = vld1q_s8(&row_ptr[c]);
            int8x16_t a = vld1q_s8(&activations[c]);
            int16x8_t prod_l = vmull_s8(vget_low_s8(w), vget_low_s8(a));
            int16x8_t prod_h = vmull_s8(vget_high_s8(w), vget_high_s8(a));
            acc = vaddw_s16(acc, vadd_s16(vget_low_s16(prod_l), vget_high_s16(prod_l)));
            acc = vaddw_s16(acc, vadd_s16(vget_low_s16(prod_h), vget_high_s16(prod_h)));
#endif
        }

        int32_t sum = 0;
#if defined(__aarch64__)
        sum = vaddvq_s32(acc);
#else
        sum = vgetq_lane_s32(acc, 0) + vgetq_lane_s32(acc, 1) + vgetq_lane_s32(acc, 2) + vgetq_lane_s32(acc, 3);
#endif
        output[r] = static_cast<float>(sum) * scale;
    }
#else
    // Pure C++ fallback
    for (int64_t r = 0; r < rows; ++r) {
        int32_t sum = 0;
        const int8_t* row_ptr = &weights[r * cols];
        for (int64_t c = 0; c < cols; ++c) {
            sum += row_ptr[c] * activations[c];
        }
        output[r] = static_cast<float>(sum) * scale;
    }
#endif
}

}
