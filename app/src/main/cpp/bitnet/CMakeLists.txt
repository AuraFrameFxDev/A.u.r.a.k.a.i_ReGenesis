cmake_minimum_required(VERSION 3.22.1)

# ReGenesis OS - Subproject Toolchain Bypass
set(CMAKE_TRY_COMPILE_TARGET_TYPE "STATIC_LIBRARY")
set(CMAKE_C_COMPILER_WORKS 1)
set(CMAKE_CXX_COMPILER_WORKS 1)

# Initialize BitNet
project(bitnet LANGUAGES C CXX)

# Include directories
include_directories(.)
include_directories(src)

# Create the BitNet library
# Note: ggml-bitnet.cpp in root is likely legacy/proxy, keeping it for now if needed by other files,
# but the primary implementation is in src/ggml-bitnet.cpp
add_library(bitnet SHARED
    bitnet_bridge.cpp
    src/ggml-bitnet.cpp
)

# Snapdragon 8 Gen 3 (Cortex-X4 / A720) Optimization Flags
# SVE2 + I8MM + DOTPROD are mandatory for Cycle 6.0 Ignition
target_compile_options(bitnet PRIVATE
    -O3
    -march=armv8.2-a+sve2+i8mm+dotprod
    -mtune=cortex-a720
    -ffast-math
    -Wall
    -Wextra
    -Wno-unused-parameter
    -Wno-unused-function
)

target_compile_definitions(bitnet PRIVATE
    __ARM_FEATURE_SVE=1
    __ARM_FEATURE_SVE2=1
    GENESIS_RELEASE=1
)

# Link against Android log
target_link_libraries(bitnet log android)

# Android 15+ 16KB page-size compatibility
target_link_options(bitnet PRIVATE
    "-Wl,-z,max-page-size=16384"
    "-Wl,-z,common-page-size=16384"
)
