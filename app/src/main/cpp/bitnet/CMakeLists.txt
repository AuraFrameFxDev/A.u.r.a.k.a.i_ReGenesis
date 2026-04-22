cmake_minimum_required(VERSION 3.22.1)

project(bitnet LANGUAGES C CXX)

# Include directories
include_directories(.)
include_directories(src)

# Create the BitNet library
add_library(bitnet SHARED
    bitnet_bridge.cpp
    src/ggml-bitnet.cpp
)

# Architecture-specific optimization flags
if(ANDROID_ABI STREQUAL "arm64-v8a")
    # Snapdragon 8 Gen 3 (Cortex-X4 / A720) Optimization Flags
    # SVE2 + I8MM + DOTPROD are mandatory for Cycle 6.0 Ignition
    target_compile_options(bitnet PRIVATE
        -O3
        -march=armv9-a+sve2+i8mm+dotprod
        -mtune=cortex-x4
        -ffast-math
    )
elseif(ANDROID_ABI STREQUAL "armeabi-v7a")
    target_compile_options(bitnet PRIVATE
        -O3
        -mfpu=neon
        -mfloat-abi=softfp
    )
else()
    # x86/x86_64 - use SSE/AVX optimizations
    target_compile_options(bitnet PRIVATE -O3)
endif()

target_compile_options(bitnet PRIVATE
    -Wall
    -Wextra
    -Wno-unused-parameter
    -Wno-unused-function
)

target_compile_definitions(bitnet PRIVATE
    GENESIS_RELEASE=1
)

# Link against Android log
target_link_libraries(bitnet log android)

# Android 15+ 16KB page-size compatibility
target_link_options(bitnet PRIVATE
    "-Wl,-z,max-page-size=16384"
    "-Wl,-z,common-page-size=16384"
)
